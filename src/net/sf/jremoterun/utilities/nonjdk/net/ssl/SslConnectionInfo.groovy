package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.json.JsonOutput
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.fileloayout.StoreDataFile
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider

import javax.net.ssl.ExtendedSSLSession
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket;
import java.util.logging.Logger;

/**
 * https://www.cloudflare.com/en-gb/learning/ssl/what-happens-in-a-tls-handshake/
 */
@CompileStatic
class SslConnectionInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<String> supportedProtocols
    List<String> enabledProtocols
    List<String> enabledCipherSuites
    List<String> disabledCipherSuites
    List<String> supportedCipherSuites
    List<String> localSupportedSignatureAlgorithms
    //String applicationProtocol;
    String cipherSuite;
    String protocol;


    SslConnectionInfo(SSLSocket sslSocket) {
        supportedProtocols = convertToList(sslSocket.getSupportedProtocols());
        enabledProtocols = convertToList(sslSocket.getEnabledProtocols())
        //applicationProtocol= sslSocket.getApplicationProtocol()
        enabledCipherSuites = convertToList(sslSocket.getEnabledCipherSuites())
        supportedCipherSuites = convertToList(sslSocket.getSupportedCipherSuites())
        disabledCipherSuites = new ArrayList<String>(supportedCipherSuites)
        disabledCipherSuites.removeAll(enabledCipherSuites)

        SSLSession session = sslSocket.getSession()
        protocol = session.getProtocol()
        cipherSuite = session.getCipherSuite()
        if (session instanceof ExtendedSSLSession) {
            ExtendedSSLSession extendedSSLSession = (ExtendedSSLSession) session;
            localSupportedSignatureAlgorithms = convertToList(extendedSSLSession.getLocalSupportedSignatureAlgorithms())
//			log.info2 "${extendedSSLSession.getPeerSupportedSignatureAlgorithms()}"
        }
    }

    List<String> convertToList(String[] aa) {
        if (aa == null) {
            return null
        }
        return Arrays.asList(aa);
    }

    String asJson() {
        Object negotiatedDump1 =  new ObjectDumperProvider( true).dumpObject(this)
//        log.info "${negotiatedDump1}"
        return JsonOutput.prettyPrint(JsonOutput.toJson(negotiatedDump1))
    }

    void dumpMainParamsToFile(File folder, String fileSuffix, int rotateCount) {
        File f = new File(folder, fileSuffix)
        dumpMainParamsToFile2(f,rotateCount)
    }

    void dumpMainParamsToFile2(File f, int rotateCount) {
        String human1 = asJson()
        new StoreDataFile(f,rotateCount).store(human1)
    }


}
