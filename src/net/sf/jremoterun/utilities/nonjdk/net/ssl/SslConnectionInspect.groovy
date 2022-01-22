package net.sf.jremoterun.utilities.nonjdk.net.ssl;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.JsseProviders
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.SslProtocolsEnum

import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import java.security.SecureRandom
import java.util.logging.Logger;

@CompileStatic
class SslConnectionInspect {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    // another value tlsProtocolVersion = 'TLS'
    public SslProtocolsEnum tlsProtocolVersion = SslProtocolsEnum.TLSv1_2
    public JsseProviders jsseProvider = JsseProviders.BC
    //public List<X509Certificate> chains = []
    public SSLContext sslContext
    public SSLSocketFactory sslSocketFactory;
    public SSLSocket socket;
    public SecureRandom secureRandom = new SecureRandom()

    public X509TrustManagerCertCollection trustManager = new X509TrustManagerCertCollection();

    void createSslContext() {
        trustManager.acceptedIssuers1 = []
        if(sslContext==null) {
            sslContext = net.sf.jremoterun.utilities.nonjdk.net.ssl.SslChecksDisable.sslContextCreator2(tlsProtocolVersion, jsseProvider)
        }
        TrustManager[] tms = [trustManager]
        sslContext.init(null, tms, secureRandom)
    }

    void prepare() {
        if (sslContext == null) {
            createSslContext()
        }
        if (sslSocketFactory == null) {
            sslSocketFactory = sslContext.getSocketFactory()
        }

    }


    void check(String hostName, int port) {
        prepare()
        socket = sslSocketFactory.createSocket(hostName, port) as SSLSocket;
        startHandshake()
    }

    void startHandshake() {
        socket.startHandshake()
    }


    SSLSession getSslSession(){
        socket.getSession()
    }
}
