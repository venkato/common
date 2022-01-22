package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.Buffer
import com.jcraft.jsch.Channel
import com.jcraft.jsch.HostKey
import com.jcraft.jsch.JSch
import com.jcraft.jsch.JSchException
import com.jcraft.jsch.JrrJschSessionOriginal
import com.jcraft.jsch.JrrJschStaticUtils
import com.jcraft.jsch.JrrSchSessionLog
import com.jcraft.jsch.JschBufferHelper
import com.jcraft.jsch.Session
import com.jcraft.jsch.UserInfo
import groovy.json.JsonOutput
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.fileloayout.StoreDataFile
import net.sf.jremoterun.utilities.nonjdk.io.LastByteArrayOutputStream
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.JavaObjectFieldsDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider
import net.sf.jremoterun.utilities.nonjdk.sshsup.auth.AuthState
import net.sf.jremoterun.utilities.nonjdk.sshsup.auth.SshAuthType
import net.sf.jremoterun.utilities.nonjdk.sshsup.channels.JschChannelType
import net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus.SshProposalsParser
import org.apache.commons.codec.binary.Base64

import java.text.SimpleDateFormat
import java.util.logging.Level
import java.util.logging.Logger;

@CompileStatic
class JrrJschSession extends JrrJschSessionOriginal {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //JrrJschIO jrrJschIO = JrrJschIO.createJrrJschIOAndSet(this)

    /**
     * @see com.jcraft.jsch.Request
     */
    public volatile Object latestRequest;


    public volatile boolean logSessionSettings = JSchSettingsDefaults.logSessionSettingsDefault;

    public int connectionTimeoutOverride = JSchSettingsDefaults.connectionTimeoutOverrideDefault;
    public List<ConnectionState> passedConnectionStates = [];
    public ConnectionState connectionState = ConnectionState.notInited;

    public JcraftConnectopnOpener jcraftConnectopnOpener


    public volatile JrrSchSessionLog jrrSchSessionLog = JSchSettingsDefaults.jrrSchSessionLogDefault;

    public List<AuthState> auths = []
    public Throwable exceptionFromConnect;
    public final Date startDate = new Date();

    public volatile boolean logRawMessage = JSchSettingsDefaults.logRawMessageDefault
    public byte[] serverPublicKey;
    public JSch jsch1
    public static String customClientVersion


    public boolean isNeedParsePublicKey = JSchSettingsDefaults.isNeedParsePublicKeyDefault

    public volatile long totalSent;
    public volatile long totalReceived;
    public volatile long lastSentD;
    public volatile long lastReceivedD;
    public LastByteArrayOutputStream lastWrittenBytes =  new LastByteArrayOutputStream()
    public LastByteArrayOutputStream lastReceivedBytes =  new LastByteArrayOutputStream()
    public OutputStream lastWrittenBytes1 = lastWrittenBytes
    public OutputStream lastReceivedBytes1 = lastReceivedBytes




    public List<String> publicKeyShort = []

    public int pubkecKeyExtraLog = 40

    public Map<String, String> usedConfigs = [:]
    public Set<String> ignoreStoreProps = new HashSet<>([new FieldRef(Session,'random').fieldName])

    public boolean logPassword = false
    public NewValueListener<JrrJschSession> connectFinishedNotifier = net.sf.jremoterun.utilities.nonjdk.sshsup.JSchSettingsDefaults.connectFinishedNotifierS
    public boolean dumpConnectionSettingsOnDisconnect = true
    public boolean dumpResolvedProps = net.sf.jremoterun.utilities.nonjdk.sshsup.JSchSettingsDefaults.dumpResolvedPropsS
    public boolean ttySizeSet = false


    JrrJschSession(JSch jsch, String username, String host, int port) throws JSchException {
        super(jsch, username, host, port)
        jsch1 = jsch
        if(customClientVersion!=null) {
            setClientVersion(customClientVersion)
        }
    }

    /**
     * @see net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.JschRequestRed
     */
    void onNewRequest(Object newRequest){
        latestRequest = newRequest
    }

    public boolean isNeedLogRawMsg() {
        return logRawMessage
    }

    void setPreferredAuth(SshAuthType authType) {
        setConfig(JschSshConfigEnum.PreferredAuthentications.name(), authType.customName);
    }


    void logAll() {
        logRawMessage = true
        logSessionSettings = true
        //JdkLogFormatter.findConsoleHandler().setLevel(Level.FINEST)
        // JrrJSch.setLogger(new com.jpto.core.JscpLogger(com.jcraft.jsch.Logger.DEBUG));
    }


    String getConnectionStateHuman() {
        if (connectionState == ConnectionState.AuthPassed) {
            return 'auth passed'
        }
        if (connectionState == ConnectionState.notInited) {
            return 'not inited'
        }
        if (connectionState == ConnectionState.inProgressConnected) {
            if (auths.size() == 0) {
                return "inProgressConnected, auth state nothing"
            }
            return "inProgressConnected, auth state : ${auths.last()}"
        }
        return connectionState.toString();
    }


    @Override
    void onOutputBuffer(Buffer buffer) {
        lastSentD = System.currentTimeMillis()
        totalSent+=JschBufferHelper.getBufferIndex(buffer)
        JschBufferHelper.writeToOutputStream(buffer,lastWrittenBytes1)
        String string = convertBufferToString(buffer)
        String publickeyS = 'publickey'
        String passwordS = 'password'
        int i = string.indexOf(publickeyS)
        if (i > 0) {
            onPublicKey(buffer, string)
        } else {
            if (isNeedLogRawMsg()) {
//                int k = string.indexOf(passwordS)
                if (string.contains(passwordS) && string.contains('ssh-connection')) {
                    onPassword(buffer, string)
                } else {
                    jrrSchSessionLog.logMsg("sending .. : ${string}")
                }
            }
        }
    }


    void onPublicKey(Buffer buffer, String string) {
        String publickeyS = 'publickey'
        int i = string.indexOf(publickeyS)

        int maxlength = i + publickeyS.length() + pubkecKeyExtraLog
        String aa;
        if (string.length() > maxlength) {
            aa = string.substring(0, maxlength)
        } else {
            aa = string
        }
        publicKeyShort.add(aa)
        if (isNeedParsePublicKey) {
            jrrSchSessionLog.logMsg("sending publickey .. : ${aa} ... \n")
        }
    }


    void onPassword(Buffer buffer, String string) {
        if (logPassword) {
            jrrSchSessionLog.logMsg("sending .. : ${string}")
        } else {
            String publickeyS = 'password'
            int i = string.indexOf(publickeyS)

            int maxlength = i + publickeyS.length()
            String aa;
            aa = string.substring(0, maxlength)
            jrrSchSessionLog.logMsg("sending .. : ${aa} password_masked")
        }
    }

    String convertBufferToString(Buffer buffer) {
        int index = JschBufferHelper.getBufferIndex(buffer)
        int s = JschBufferHelper.getBufferS(buffer)
        byte[] bytes = JschBufferHelper.getBufferBytes(buffer)
        String sss = new String(bytes, 0, index);
        sss = sss.replace('\u0000', ' ')
        sss = sss.replace('\r',' ')
        return sss
    }

    @Override
    Buffer read(Buffer buf) throws Exception {
        Buffer b = super.read(buf)
        lastReceivedD = System.currentTimeMillis()
        totalReceived+=JschBufferHelper.getBufferIndex(b)
        JschBufferHelper.writeToOutputStream(b,lastReceivedBytes1)
        if (isNeedLogRawMsg()) {
            String string = convertBufferToString(b)
            jrrSchSessionLog.logMsg("received .. : ${string}")
        }
        return b;
    }


    @Override
    String getConfig(String key) {
        String result = super.getConfig(key)
        if (logSessionSettings) {
            log.info "resolved session config : ${key} = ${result}"
        }
        if(!ignoreStoreProps.contains(key)) {
            String dataBefore = usedConfigs.put(key, result)
            if (dataBefore != null) {
                if (dataBefore != result) {
                    boolean staored = false
                    int cc = 1;
                    while (cc < 10) {
                        String key2 = "${key}-${cc}"
                        if (usedConfigs.containsKey(key2)) {
                            cc++
                        } else {
                            usedConfigs.put(key2, dataBefore)
                            staored = true
                            break
                        }
                    }
                    if (!staored) {
                        throw new Exception("many keys ${key} in ${usedConfigs.sort()}")
                    }
                }
            }
        }
        return result;
    }

    @Override
    Channel openChannel(String type) throws JSchException {
        if (!isConnected()) {
            throw new JSchException("session is down");
        }

        JschChannelType typeE = JschChannelType.allMap.get(type)
        log.info "openning channel type = ${type}, ${typeE}"
        jrrSchSessionLog.logMsg "opening channel : ${type}";
        passedConnectionStates.add(ConnectionState.openningChannel)
        if (typeE == null) {
            return openChannel3(type)
        }
        return openChannel2(typeE)
//            Channel channel=Channel.getChannel(type);
//            addChannel(channel);
//            channel.init();
//            if(channel instanceof ChannelSession){
//                applyConfigChannel((ChannelSession)channel);
//            }
//            return channel;

    }

    Channel openChannel2(JschChannelType type) throws JSchException {
        Channel channel = type.clazz.newInstance();
//        if (channel instanceof JrrJschSessionMethods) {
//            JrrJschSessionMethods  sessionMethods = (JrrJschSessionMethods) channel;
//            sessionMethods.setJrrJschSession(this)
//        }
        addChannel(channel);
        JrrJschStaticUtils.callChannelInit(channel)
        if (JrrJschStaticUtils.isChannelSession(channel)) {
            JrrClassUtils.invokeJavaMethod(this, 'applyConfigChannel', channel)
            //applyConfigChannel((ChannelSession)channel);
        }
        jrrSchSessionLog.logMsg "channel opened: ${type.clazz.getName()}";
        return channel;
    }



    @Override
    void disconnect() {
        if (connectionState != ConnectionState.AuthPassed) {
            if (dumpConnectionSettingsOnDisconnect) {
                if (connectionState != ConnectionState.notInited) {
                    try {
                        log.info "connection settings : ${fetchGuessHumanS()}"
                    } catch (Throwable e) {
                        log.log(Level.WARNING, 'failed dump session settings', e)
                    }
                }
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat('dd HH:mm');
        jrrSchSessionLog.logMsg("disconnect ${sdf.format(new Date())}");
        super.disconnect()
        connectionState = ConnectionState.disconnected;
        passedConnectionStates.add(ConnectionState.disconnected)
        log.info "disconnected ${getHost()}"
    }

    Channel openChannel3(String type) throws JSchException {
        Channel res = super.openChannel(type)
        if (res == null) {
            jrrSchSessionLog.logMsg "channel opened null";
        } else {
            jrrSchSessionLog.logMsg "channel opened: ${res.getClass().getName()}";
        }
        return res
    }


    @Override
    void connect(int connectTimeout) throws JSchException {
        if (connectionTimeoutOverride >= 0) {
            connectTimeout = connectionTimeoutOverride;
        }

        jrrSchSessionLog.logMsg "connecting with timeout ${connectTimeout} ms ..";
//        if(jcraftConnectopnOpener.conSet2.user!=null){
//            setUserName(jcraftConnectopnOpener.conSet2.user)
//        }
//        if(jcraftConnectopnOpener.conSet2.password!=null){
//            setPassword(jcraftConnectopnOpener.conSet2.password)
//        }
        UserInfo userInfo1 = getUserInfo();
        if (userInfo1 != null) {
            if (userInfo1 instanceof UserInfoJrr) {

            } else {
                UserInfoJrr u = new UserInfoJrr(userInfo1, this)
                setUserInfo(u)
            }
        }
        try {
            super.connect(connectTimeout)
        } catch (Throwable e) {
            exceptionFromConnect = e
            jrrSchSessionLog.logMsg "exception  : ${e}";
            throw e
        } finally {
            if (isAuthed2()) {
                connectionState = connectionState.AuthPassed;
                passedConnectionStates.add(ConnectionState.AuthPassed)
                logRawMessage = false
            } else {
                if (connectionState == connectionState.inProgressConnected) {
                    connectionState = connectionState.AuthFailed;
                    passedConnectionStates.add(ConnectionState.AuthFailed)
                } else {
                    connectionState = connectionState.ConnectionFailed
                    passedConnectionStates.add(ConnectionState.ConnectionFailed)
                }

            }
            if (connectFinishedNotifier != null) {
                connectFinishedNotifier.newValue(this)
            }

        }
    }



    boolean isAuthed2() {
        return (Boolean) JrrClassUtils.getFieldValue(this, 'isAuthed');
    }

    void onConnected() {
        connectionState = connectionState.inProgressConnected;
        passedConnectionStates.add(ConnectionState.inProgressConnected)
        long diff = System.currentTimeMillis() - startDate.getTime()
        diff = (long) (diff / 1000)
        jrrSchSessionLog.logMsg "connected within ${diff}s";
    }


    String getPassword2() {
        return (String) JrrClassUtils.getFieldValue(this, 'password')
    }


    String[] fetchGuess() {
        return JrrClassUtils.getFieldValue(this, 'guess') as String[]
    }


//    public JSch getJsch1(){
//        return JrrClassUtils.getFieldValue(this,'jsch') as JSch
//    }


    Map<String, Object> fetchProposalsJson(FieldRef fieldName) {
        Object obj = fetchProposalsObj(fieldName)
        if (obj == null) {
            return null
        }
        return (Map)  new ObjectDumperProvider( new JavaObjectFieldsDumper([SshProposalsParser.packetField])).dumpObject(obj)
    }


    Object fetchProposalsObj(FieldRef fieldName) {
        byte[] bs = JrrClassUtils.getFieldValue(this, fieldName.fieldName) as byte[]
        if (bs == null || bs.length == 0) {
            return null
        }
        Object serverProposals = SshProposalsParser.parseProposals(bs)
        return serverProposals
    }

    String fetchGuessHumanS() {
        Map<KeyExchangeEnum1, Object> human = fetchGuessHuman()
        return JsonOutput.prettyPrint(JsonOutput.toJson(human))
    }



    Map<KeyExchangeEnum1, Object> fetchGuessHuman() {
//        private byte[] V_S;                                 // server version
//        private byte[] V_C= Util.str2byte("SSH-2.0-JSCH_"+JSch.VERSION); // client version
//
//        private byte[] I_C; // the payload of the client's SSH_MSG_KEXINIT
//        private byte[] I_S; // the payload of the server's SSH_MSG_KEXINIT
//        private byte[] K_S; // the host key
//        log.info2  com.jcraft.jsch.Session.getDeclaredFields().toList().collect {"${it.getName()} ${it.getType().getName()}"}.sort()
        Map<KeyExchangeEnum1, Object> m = [:]
        String[] guess = fetchGuess()
        if (guess != null) {
            for (int i = 0; i < guess.length; i++) {
                m.put(KeyExchangeEnum1.mapp1.get(i), guess[i])
            }
        }

        if (publicKeyShort.size() > 0) {
            m.put(KeyExchangeEnum1.JRR_Client_Publickey_Short, publicKeyShort)
        }
        if (serverPublicKey != null) {
            m.put(KeyExchangeEnum1.JRR_Server_Public_Key, Base64.encodeBase64String(serverPublicKey))
        }
        HostKey hostKey1 = getHostKey()
        if (hostKey1 != null) {
            m.put(KeyExchangeEnum1.JRR_Server_Public_Key2, hostKey1.getFingerPrint(jsch1))
        }
        try {

            m.put(KeyExchangeEnum1.jrr_sig, JrrClassUtils.getFieldValue(this, 'serverSigAlgs'))
            Object not_available_shks = JrrClassUtils.getFieldValue(this, 'not_available_shks')
            if (not_available_shks != null) {
                m.put(KeyExchangeEnum1.jrr_notAvailableSig, not_available_shks)
            }
        } catch (NoSuchFieldException e) {
            log.debug("failed find field serverSigAlgs : ${e}")
        }
        m.put(KeyExchangeEnum1.jrr_proposalServer, fetchProposalsJson(new FieldRef(this.getClass(), 'I_S')))
        m.put(KeyExchangeEnum1.jrr_proposalClient, fetchProposalsJson(new FieldRef(this.getClass(), 'I_C')))
//        boolean hasData = m.get(KeyExchangeEnum1.jrr_proposalServer)!=null || m.get(KeyExchangeEnum1.jrr_proposalClient)!=null
//        if(!hasData){
//            return null
//        }
        m.put(KeyExchangeEnum1.jrr_versionClient, getClientVersion())
        m.put(KeyExchangeEnum1.jrr_connectionState, connectionState)
        if (dumpResolvedProps) {
            m.put(KeyExchangeEnum1.jrr_dumpResolvedProps, new TreeMap<>(usedConfigs))
        }
        try {
            m.put(KeyExchangeEnum1.jrr_versionServer, getServerVersion())
        } catch (NullPointerException e) {
            log.log(Level.FINE, "failed dump version server", e)
        } catch (Throwable e) {
            log.log(Level.WARNING, "failed dump version server", e)
        }
        m.sort()

        return m
    }


    void dumpMainParamsToFile(File folder, String fileSuffix, int rotateCount) {
        Map<KeyExchangeEnum1, Object> human1 = fetchGuessHuman()
        dumpMainParamsToFileImpl(folder, fileSuffix, rotateCount, human1)
    }

    void dumpMainParamsToFileImpl(File folder, String fileSuffix, int rotateCount, Map<KeyExchangeEnum1, Object> human1) {
        assert folder.exists()
        File f = new File(folder, getHost() + fileSuffix)

        boolean pass1 = human1.size() > 4 || connectionState != ConnectionState.notInited
        if (pass1) {
            String human = JsonOutput.prettyPrint(JsonOutput.toJson(human1))


            String params1 = human
//        String params1 =human.collect {"${it.key} = ${it.value}"}.join('\n')
            new StoreDataFile(f,rotateCount).store(params1)
        } else {
            log.info "${getHost()} too low data ${human1.size()} : ${human1}"
        }
    }


}
