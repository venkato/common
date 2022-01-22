package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio

import groovy.transform.CompileStatic
import io.aeron.archive.ArchivingMediaDriver
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import net.sf.jremoterun.utilities.nonjdk.quickfixsender.QuickFixMsgUtils
import net.sf.jremoterun.utilities.nonjdk.quickfixsender.SeqNumberAutoFixer
import net.sf.jremoterun.utilities.nonjdk.timer.TimerPeriod
import org.agrona.concurrent.SleepingIdleStrategy
import quickfix.DataDictionary
import uk.co.real_logic.artio.engine.FixEngine
import uk.co.real_logic.artio.library.FixLibraryJrr
import uk.co.real_logic.artio.library.LibraryConfiguration
import uk.co.real_logic.artio.library.SessionAcquiredInfo
import uk.co.real_logic.artio.util.MutableAsciiBuffer

import java.text.SimpleDateFormat
import java.util.logging.Logger
import java.util.regex.Pattern

@CompileStatic
class JrrArtioDataHolder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public ArtioRstaRunnerWithStackTrace t
    public ArtioApplication application
    public JrrArtioDataHolder d = this


    public Pattern seqNumMismatch = Pattern.compile('MsgSeqNum too low, expecting (\\d+) but received (\\d)')

    public boolean autoFixSeqNum = true
    public volatile Runnable customCode;;
    public volatile Runnable customCodeOnConnected;;
    public volatile ArtioConfiguration ac;

    public volatile boolean keepRunning = true
    public volatile boolean keepRunningDisconnectTillFinished = true
    public volatile boolean doDisconnect = true

    public SimpleDateFormat sdf = new SimpleDateFormat('HH:mm:ss')

    public volatile int modificationsCount = 100;

    public final Object disconnectLock = new Object()

    public Date lastMsgSend = new Date()
    public long autoDisconnectTimeInSec = 10* DurationConstants.oneMin.timeInSecLong;
    @Deprecated
    public static char fixMsgNativeSep = net.sf.jremoterun.utilities.nonjdk.quickfixsender.QuickFixMsgUtils.fixMsgNativeSep;

    public char humanSep = '|';

    public DataDictionary dictionary;

    public TimerPeriod timerPeriod

    public uk.co.real_logic.artio.session.Session session

    public SleepingIdleStrategy idleStrategy
    public FixLibraryJrr library

    public ArchivingMediaDriver driver;
    public FixEngine engine;
    public SeqNumberAutoFixerArtio fixMsgAutoFixer = new SeqNumberAutoFixerArtio()

    public ArrayDeque<String> msgs=[]
    public LibraryConfiguration libraryConfiguration
    public Thread threadRunning
    public long countOnTimeout;
    public SessionAcquiredInfo sessionAcquiredInfo
    public  String latestIncomingMsgShos

    public  MutableAsciiBuffer latestIncomingMsg = new MutableAsciiBuffer(new byte[8 * 1024]);
    public int latestTestRequestMessageLength = 0;
//    public int loginTryCount = 0

    void sendMsg(String msg) {
        String msgSoh = msg.replace(humanSep, QuickFixMsgUtils.fixMsgNativeSep);
        if(msgSoh.startsWith(QuickFixMsgUtils.fixMsgNativeSepStr)){
            msgSoh = msgSoh.substring(1)
        }
        if(!msgSoh.endsWith(QuickFixMsgUtils.fixMsgNativeSepStr)){
            msgSoh += QuickFixMsgUtils.fixMsgNativeSepStr
        }
        msgs.addLast(msgSoh)
    }

    JrrArtioDataHolder(ArtioRstaRunnerWithStackTrace t) {
        this.t = t
    }
}
