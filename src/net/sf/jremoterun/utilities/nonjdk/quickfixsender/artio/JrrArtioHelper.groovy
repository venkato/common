package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio

import groovy.transform.CompileStatic
import io.aeron.archive.Archive
import io.aeron.archive.ArchiveThreadingMode
import io.aeron.archive.ArchivingMediaDriver
import io.aeron.driver.MediaDriver
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.quickfixsender.*
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaScriptHelper
import net.sf.jremoterun.utilities.nonjdk.timer.TimerPeriod
import org.agrona.concurrent.SleepingIdleStrategy
import org.apache.commons.io.FileUtils
import quickfix.*
import quickfix.field.MsgType
import quickfix.field.Text
import uk.co.real_logic.artio.engine.EngineConfiguration
import uk.co.real_logic.artio.engine.FixEngine
import uk.co.real_logic.artio.library.FixLibraryJrr
import uk.co.real_logic.artio.library.LibraryConfiguration
import uk.co.real_logic.artio.library.LibraryUtil
import uk.co.real_logic.artio.library.SessionAcquireHandler
import uk.co.real_logic.artio.library.SessionAcquiredInfo
import uk.co.real_logic.artio.library.SessionConfiguration
import uk.co.real_logic.artio.library.SessionHandler

import java.awt.Color
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
abstract class JrrArtioHelper extends RstaScriptHelper {
    protected static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrArtioDataHolder d;
    public ArtioRstaRunnerWithStackTrace t;

    void init2() {
        t = runner as ArtioRstaRunnerWithStackTrace;
        d = t.d;
        d.application = new ArtioApplication(d);
    }

    @Override
    void init() {
        super.init()
        init2()
    }

    boolean isNeedAddMsgToTable(Message msg, boolean isOutMsg) {
        String msgType = msg.getHeader().getString(MsgType.FIELD)
        boolean isUsefull = true
        if (msgType == MsgType.HEARTBEAT) {
            isUsefull = false
        }
        if (msgType == MsgType.TEST_REQUEST) {
            isUsefull = false
        }
        if (d.autoFixSeqNum && msgType == MsgType.LOGOUT) {
            if (msg.isSetField(Text.FIELD)) {
                onLogoutWithText(msg, isOutMsg)
            }
        }
        return isUsefull
    }

    void onLogoutWithText(Message msg, boolean isOutMsg) {
        //d.fixMsgAutoFixer.onLogoutWithText(msg, isOutMsg, this)
    }

    void fixSeqNum(int expected, int my, boolean isOutMsg) {
        log.info "expected = ${expected}, my = ${my}, isOutMsg = ${isOutMsg} "
        if (isOutMsg) {
            log.error "outbound reset not supported"
        }else{
            fixSeqNumImpl(expected)
        }
    }

    void fixSeqNumImpl(int expected) {
        int newExpected = expected
        d.ac.onLogonSentSeqNumber = newExpected
        t.addMessageToLogViewerCurrentDate("setting newExpected = ${newExpected}")
//        t.modCount++
        t.showLogs()
//        d.session.setNextSenderMsgSeqNum(newExpected)
        d.session.lastSentMsgSeqNum(newExpected)
//        d.loginTryCount = 1
    }


    void updateLastRun() {
        d.lastMsgSend = new Date()
        //d.timerPeriod.getTimerImpl().setLastRun(d.lastMsgSend);
    }


    void setAutoDisconnectTimeInSecNew(long time) {
        d.autoDisconnectTimeInSec = time
        if (d.timerPeriod != null) {
            d.timerPeriod.setPeriod(time * 1000)
        }
    }


    void onTimeout() {
        long disconeedNeededAt = d.lastMsgSend.getTime() + d.autoDisconnectTimeInSec * DurationConstants.oneSec.timeInMsLong
        boolean needStop = System.currentTimeMillis() > disconeedNeededAt
        if (needStop) {
            stopConnectionFromTimer()
            String msg2= "disconnecting , passed at ${t.sdf.format(new Date(disconeedNeededAt))}"
            String s = "${t.sdf.format(new Date())} : ${msg2}  "
            log.info s
            t.addMessageToLogViewerCurrentDate(msg2)
//            t.modCount++
            t.showLogs()
            d.timerPeriod.stop()
        } else {

        }

    }

    void stopConnectionFromTimer() {
        int modificationsCountRemember = d.modificationsCount;
        synchronized (d.disconnectLock) {
            d.disconnectLock.wait(3 * DurationConstants.oneSec.timeInMsLong);
        }
        if (d.modificationsCount == modificationsCountRemember) {
            log.info "stopping qf connection ${new Date()}"
            t.stopCurrent()
        } else {
            log.info "count diff modificationsCountRemember = ${modificationsCountRemember}, now = ${d.modificationsCount}"
        }
    }


    SessionHandler onConnect(final uk.co.real_logic.artio.session.Session session, SessionAcquiredInfo acquiredInfo) {
        d.session = session
        d.sessionAcquiredInfo = acquiredInfo
        if (d.ac.onLogonSentSeqNumber > 0) {
            d.session.lastSentMsgSeqNum(d.ac.onLogonSentSeqNumber)
        }
        if (d.customCodeOnConnected != null) {
            d.customCodeOnConnected.run()
        }
        JrrClassUtils.setFieldValue(session, "fixDictionary", d.ac.fixDictionary);
        JrrClassUtils.setFieldValue(session, "beginString", d.ac.fixDictionary.beginString1);
        d.t.statusField.setText("connect1");
        d.t.statusField.setForeground(Color.YELLOW);
        d.t.addMessageToLogViewerCurrentDate("session connected 1")
        d.t.showLogs();
        return d.application;
    }

    EngineConfiguration createEngineConfiguration(){
        new EngineConfiguration()
                .libraryAeronChannel(d.ac.aeronChannel)
                .monitoringFile(d.ac.monitringFile)
                .logFileDir(d.ac.logDir.getPath()).logOutboundMessages(true);
    }

    SessionConfiguration createSessionConfiguration(){
        return SessionConfiguration.builder()
                .address(d.ac.host, d.ac.port)
                .targetCompId(d.ac.ACCEPTOR_COMP_ID)
                .senderCompId(d.ac.INITIATOR_COMP_ID)
                .fixDictionary(d.ac.fixDictionary.getClass())
                .build();
    }

    void createConnection() {
//        d.loginTryCount = 0
        d.modificationsCount++;
        FileUtils.deleteQuietly(d.ac.logDir)
        d.ac.logDir.mkdir()
        File[] logDirList = d.ac.logDir.listFiles()
        assert logDirList.length == 0
        d.idleStrategy = new SleepingIdleStrategy(d.ac.sleepTime);
        final EngineConfiguration configuration = createEngineConfiguration()

        configuration.aeronArchiveContext()
                .aeronDirectoryName(d.ac.AERON_DIR_NAME.getPath())
                .controlRequestChannel(d.ac.CONTROL_REQUEST_CHANNEL)
                .controlResponseChannel(d.ac.CONTROL_RESPONSE_CHANNEL);

        configuration.aeronContext()
                .aeronDirectoryName(d.ac.AERON_DIR_NAME.getPath());


        final MediaDriver.Context context = new MediaDriver.Context()
                .threadingMode(io.aeron.driver.ThreadingMode.SHARED)
                .dirDeleteOnStart(true)
                .aeronDirectoryName(d.ac.AERON_DIR_NAME.getPath());

        final Archive.Context archiveContext = new Archive.Context()
                .threadingMode(ArchiveThreadingMode.SHARED)
                .deleteArchiveOnStart(true)
                .aeronDirectoryName(d.ac.AERON_DIR_NAME.getPath())
                .archiveDirectoryName(d.ac.ARCHIVE_DIR_NAME.getPath());

        archiveContext
                .controlChannel(d.ac.CONTROL_REQUEST_CHANNEL)
                .replicationChannel(d.ac.REPLICATION_CHANNEL)
                .recordingEventsChannel(d.ac.RECORDING_EVENTS_CHANNEL);

        d.driver = ArchivingMediaDriver.launch(context, archiveContext);
        d.engine = FixEngine.launch(configuration);


        SessionAcquireHandler sessionAcquireHandler = new SessionAcquireHandler() {

            @Override
            SessionHandler onSessionAcquired(uk.co.real_logic.artio.session.Session session, SessionAcquiredInfo acquiredInfo) {
                return onConnect(session, acquiredInfo)
            }
        }
        d.libraryConfiguration = new LibraryConfiguration()
                .sessionAcquireHandler(sessionAcquireHandler)
                .libraryAeronChannels(java.util.Collections.singletonList(d.ac.aeronChannel));

        d.libraryConfiguration.aeronContext()
                .aeronDirectoryName(d.ac.AERON_DIR_NAME.getPath());

        d.library = new FixLibraryJrr(d.libraryConfiguration);
        d.library.connect1();
        // Whilst we only initiate a single Session for a given library here, it is
        // perfectly possible to initiate multiple Sessions on a given library
        // and manage them accordingly.
//        d.session = LibraryUtil.initiate(d.library, sessionConfig, d.ac.attempts, d.idleStrategy);
        createSession()
//        if (d.loginTryCount > 0) {
//            d.loginTryCount = 0
//            log.info("connecting again ...")
//            d.session = LibraryUtil.initiate(d.library, sessionConfig, d.ac.attempts, idleStrategy);
//        }
    }

    void createSession(){
        final SessionConfiguration sessionConfig = createSessionConfiguration()
        d.session = LibraryUtil.initiate(d.library, sessionConfig, d.ac.attempts, d.idleStrategy);
    }

    void mainLoopStart() {

        d.keepRunning = true
        d.keepRunningDisconnectTillFinished = true
        Runnable r = {
            try {
                mainLoop()
            } catch (Throwable t) {
                log.log(Level.SEVERE, "Error in main loop", t)
                JrrUtilitiesShowE.showException("Error in main loop", t);
                close()
            }
        }
        d.threadRunning = new Thread(r, "artio main loop")
        d.threadRunning.start()
    }

    ReplayEncoder createReplayEncoder(String msgType, String msg) {
        ReplayEncoder encoder2 = new ReplayEncoder(msgType, msg);
        encoder2.header222.beginString(d.ac.beginString)
        return encoder2
    }

    void mainLoop() {
        try {
            createConnection()
            Runnable r = { onTimeout() };
            if (d.timerPeriod == null) {
                d.timerPeriod = new TimerPeriod(d.autoDisconnectTimeInSec * DurationConstants.oneSec.timeInMsLong, r)
                d.timerPeriod.start()
            }
            d.timerPeriod.start()
            updateLastRun()

            while (!d.session.isActive() && d.keepRunning) {
                d.idleStrategy.idle(d.library.poll(d.ac.poolTime));
            }
            d.t.statusField.setText("connect2");
            d.t.statusField.setForeground(Color.GREEN);
            d.t.addMessageToLogViewerCurrentDate("session connected 2")
            d.t.showLogs();
            while (d.keepRunning) {
                d.idleStrategy.idle(d.library.poll(d.ac.poolTime));
                if (d.msgs.size() > 0) {
                    String remove = d.msgs.poll()
                    if (remove != null) {
                        int i = remove.indexOf(QuickFixMsgUtils.fixMsgNativeSepStr)
                        String msgType = remove.substring(0, i)
                        String msgType2 = msgType.tokenize('=')[1]
                        ReplayEncoder encoder2 = createReplayEncoder(msgType2, remove);
                        d.session.trySend(encoder2);
                        updateLastRun()
                    }
                }
                if (d.customCode != null) {
                    d.customCode.run()
                    d.customCode = null
                }
            }
            d.t.statusField.setText("stopping");
            d.t.statusField.setForeground(Color.YELLOW);
            d.t.addMessageToLogViewerCurrentDate("stop running 1")
            d.t.showLogs();
            if (d.doDisconnect) {
                d.t.addMessageToLogViewerCurrentDate("logout ...")
                d.t.showLogs();
                d.session.startLogout();
                d.session.requestDisconnect();
                d.countOnTimeout = 0
                while (d.keepRunningDisconnectTillFinished && d.session.state() != uk.co.real_logic.artio.messages.SessionState.DISCONNECTED) {
                    d.idleStrategy.idle(d.library.poll(d.ac.poolTime));
                    d.countOnTimeout++
                    if (needStopOnTimeout()) {
                        d.t.addMessageToLogViewerCurrentDate("max count on timeout reached, stopping")
                        d.t.showLogs();
                        break;
                    }
                }
                d.t.addMessageToLogViewerCurrentDate("logout done")
                d.t.showLogs();
            }
            d.t.addMessageToLogViewerCurrentDate("stop running 2")
            d.t.showLogs();
        } finally {
            close()
        }
    }

    boolean needStopOnTimeout() {
        if (d.countOnTimeout > d.ac.maxCountOnTimeout) {
            return true
        }
        return false

    }


    void disconnectWait() {
        if (d.threadRunning == null) {
            throw new Exception("not connected")
        }
        d.keepRunning = false
        d.keepRunningDisconnectTillFinished = false
        d.t.addMessageToLogViewerCurrentDate("disconnect requested")
        d.threadRunning.join()
    }

    void close() {
        d.t.statusField.setText("closed");
        d.t.statusField.setForeground(Color.RED);
        JrrIoUtils.closeQuietly2(d.library, log)
        JrrIoUtils.closeQuietly2(d.engine, log)
        JrrIoUtils.closeQuietly2(d.driver, log)
    }


    void dataDictionaryAllowAll(DataDictionary dd) {
        dd.setCheckFieldsHaveValues(false)
        dd.setCheckFieldsOutOfOrder(false)
        dd.setCheckUnorderedGroupFields(false)
        dd.setCheckUserDefinedFields(false)
    }


}
