package net.sf.jremoterun.utilities.nonjdk.sshsup.hierynomus

import groovy.json.JsonOutput
import groovy.transform.CompileStatic
import net.schmizz.sshj.SSHClient
import net.schmizz.sshj.Service
import net.schmizz.sshj.transport.NegotiatedAlgorithms
import net.schmizz.sshj.transport.Transport
import net.schmizz.sshj.transport.TransportImpl
import net.schmizz.sshj.transport.kex.KeyExchange
import net.schmizz.sshj.transport.kex.KeyExchangeBase
import net.schmizz.sshj.transport.verification.PromiscuousVerifier
import net.schmizz.sshj.userauth.keyprovider.KeyProvider
import net.schmizz.sshj.userauth.method.AuthPublickey;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.fileloayout.StoreDataFile
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.JavaObjectFieldsDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider
import net.sf.jremoterun.utilities.nonjdk.sshsup.SshConnectionDetailsI
import org.apache.commons.codec.binary.Base64

import java.lang.reflect.Field
import java.security.PublicKey;
import java.util.logging.Logger;

/**
 * X509 certificates Supported ? :  https://www.rfc-editor.org/rfc/rfc6187
 */
@CompileStatic
class SSHClientJrr extends SSHClient {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String,String> customServerProps = [:]

    SSHClientJrr() {
    }

    SSHClientJrr(net.schmizz.sshj.Config config) {
        super(config)
    }

    void setLoggingClasses(boolean logAll){
        TransportImpl transport = getTransport() as TransportImpl
//        Object encoder = JrrClassUtils.getFieldValue(transport,'encoder');
        CompressionLog compressionLog = new CompressionLog(transport)
        compressionLog.selfSet()
        SSHPacketHandlerProxy handlerProxy = new SSHPacketHandlerProxy(transport)
        handlerProxy.before = new SSHPacketHandlerLog(this, compressionLog,true)
        handlerProxy.selfSet()
    }


    void connectJrr(SshConnectionDetailsI conSet2){
        addHostKeyVerifier(new PromiscuousVerifier())
        connect(conSet2.host)
        if(conSet2.sshKey!=null) {
            authPublicKeyJrr(conSet2.user, new AuthPublickeyJrr(this, conSet2.sshKey))
        }else if(conSet2.passwordReceiver!=null && conSet2.passwordReceiver.isPasswordSet()) {
            authPassword(conSet2.user,conSet2.passwordReceiver.readPassword(conSet2))
        }
    }

    void authPublicKeyJrr(String username, AuthPublickeyJrr authPublickey) {
        authPublickey.setLoggerFactory(loggerFactory)
        boolean authenticate1 = auth.authenticate(username, (Service) conn, authPublickey, trans.getTimeoutMs())
        assert authenticate1
    }

    void authPublicKeyJrr(String username, File f) {
        assert f.exists()
        KeyProvider keys = loadKeys(f.getAbsolutePath())
        AuthPublickey authPublickey = new AuthPublickeyJrr(keys)
        authPublicKeyJrr(username,authPublickey)
    }


    NegotiatedAlgorithms getNegotiatedAlgorithms(){
        TransportImpl transport1= getTransport() as TransportImpl
        new ClRef('net.schmizz.sshj.transport.KeyExchanger')
        Object kexer = JrrClassUtils.getFieldValue(transport1,'kexer')
        net.schmizz.sshj.transport.NegotiatedAlgorithms negotiatedAlgorithms = JrrClassUtils.getFieldValueR(new ClRef('net.schmizz.sshj.transport.KeyExchanger'), kexer,'negotiatedAlgs') as NegotiatedAlgorithms
        return negotiatedAlgorithms;
    }



    Object getClientProposals(){
        TransportImpl transport1= getTransport() as TransportImpl

        Object kexer = JrrClassUtils.getFieldValue(transport1,'kexer')
        ClRef clRef1 = new ClRef('net.schmizz.sshj.transport.KeyExchanger')
        Field field = JrrClassUtils.findField(clRef1, 'clientProposal')
//        Object negotiatedAlgorithms = JrrClassUtils.getFieldValue(kexer,'clientProposal')
        return field.get(kexer);
    }

    Object getServerProposals(){
        net.schmizz.sshj.transport.kex.KeyExchangeBase kex = getKeyExchange() as KeyExchangeBase
        byte[] bs = JrrClassUtils.getFieldValue(kex,'I_S') as byte[];
        net.schmizz.sshj.common.SSHPacket sp = new net.schmizz.sshj.common.SSHPacket(bs)
        ClRef proposals = new ClRef('net.schmizz.sshj.transport.Proposal')
        Object proposals2 = JrrClassUtils.invokeConstructor(proposals,sp)
        return proposals2;
        //proposals.loadClass2().decla
    }


    KeyExchange getKeyExchange(){
        TransportImpl transport1= getTransport() as TransportImpl
        new ClRef('net.schmizz.sshj.transport.KeyExchanger')
        Object kexer = JrrClassUtils.getFieldValue(transport1,'kexer')
        net.schmizz.sshj.transport.kex.KeyExchange keyExchange = JrrClassUtils.getFieldValueR(new ClRef('net.schmizz.sshj.transport.KeyExchanger'), kexer,'kex') as KeyExchange
        return keyExchange;
    }

    void dumpMainParamsToFile(File folder, String fileSuffix ,int rotateCount){
        assert folder.exists()
        File f = new File(folder,getRemoteHostname()+fileSuffix)
        String params1 = dumpMainParams()
        new StoreDataFile(f,rotateCount).store(params1)
    }

    String dumpMainParams(){
//        Map<SshjDumpParams,Object> r = [:]
        Transport transport1 = getTransport()
        Map<SshjDumpParams,Object> r = [:]
        NegotiatedAlgorithms negotiatedAlgorithms1 = getNegotiatedAlgorithms()
        KeyExchange keyExchange = getKeyExchange()
        PublicKey hostKey = keyExchange.getHostKey()
//        log.info "negotiatedAlgorithms=${negotiatedAlgorithms1}, serverPublicKey=${algorithm} ${hostKey.getClass().getName()} ${base64String}"
        Map<String, Object> negotiatedDump1 = new ObjectDumperProvider( new JavaObjectFieldsDumper()).dumpObject(negotiatedAlgorithms1) as Map
//        Map<String, Object> clientProposal =
        r.put(SshjDumpParams.proposalServerProps,customServerProps)
        r.put(SshjDumpParams.versionClient,transport1.getClientVersion())
        r.put(SshjDumpParams.versionServer,transport1.getServerVersion())
        r.put(SshjDumpParams.proposalClient,new ObjectDumperProvider(new JavaObjectFieldsDumper([SshProposalsParser.packetField])).dumpObject(getClientProposals()))
        r.put(SshjDumpParams.proposalServer,new ObjectDumperProvider(new JavaObjectFieldsDumper([SshProposalsParser.packetField])).dumpObject(getServerProposals()))
        r.put(SshjDumpParams.negotiated,negotiatedDump1)
        Map<String,Object> serverKey=[:]
        serverKey.put('algorithm',hostKey.getAlgorithm())
        serverKey.put('class',hostKey.getClass().getName())
        serverKey.put('value',Base64.encodeBase64String(hostKey.getEncoded()))
        r.put(SshjDumpParams.serverPublicKey,serverKey)
        r = r.sort()
        return JsonOutput.prettyPrint( JsonOutput.toJson(r))
    }


    void setClientId(String clientId){
        TransportImpl transport1= getTransport() as TransportImpl
        JrrClassUtils.setFieldValue(transport1,'clientID',clientId)
    }

}
