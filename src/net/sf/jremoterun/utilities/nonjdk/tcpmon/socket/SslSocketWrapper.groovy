package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslConnectionInfo

import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import java.util.logging.Logger

@CompileStatic
class SslSocketWrapper extends SocketWrapper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SSLSocket sslSocket;

    SslSocketWrapper(SSLSocket socket2) {
        super(socket2)
        this.sslSocket = socket2
    }

    @Override
    String dumpSocketDebugInfo() {
        SSLSession session1 = sslSocket.getSession()
        return session1.getProtocol() + '  ' + session1.getCipherSuite() + '  ' + sslSocket.getClass().getName() + ' ' + '\n'
    }

    Object showDebugInfo(){
        SSLSession session1 = sslSocket.getSession()
        SslConnectionInfo connectionInfo = new SslConnectionInfo(sslSocket)
        List aa = [connectionInfo,session1]
//        List aa = [connectionInfo,session1.getPeerCertificates(),session1.getPeerCertificateChain(),session1.getLocalCertificates(),session1]
        return aa
    }
}
