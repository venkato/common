package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.crypto.BouncyCastleCertificateGenerator
import net.sf.jremoterun.utilities.nonjdk.crypto.CertificatedGeneratorJdk
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.JsseProviders
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.SslProtocolsEnum

import javax.net.ssl.*
import java.security.KeyStore
import java.security.SecureRandom
import java.util.logging.Logger

/**
 * @see fi.iki.elonen.NanoHTTPD#makeSecure
 */
@CompileStatic
class SslServerConnectionInspect {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    // another value tlsProtocolVersion = 'TLS'
    public SslProtocolsEnum tlsProtocolVersion = SslProtocolsEnum.TLSv1_2
    public JsseProviders jsseProvider = JsseProviders.BC
    //public List<X509Certificate> chains = []
    public SSLContext sslContext
    public SSLServerSocketFactory sslSocketFactory;
    public SSLServerSocket socket;
    public char[] keyPass = []
    public String x509Algo;

    public X509TrustManagerCertCollection trustManager = new X509TrustManagerCertCollection();
    public BouncyCastleCertificateGenerator generatorBc = new BouncyCastleCertificateGenerator()
    public SecureRandom secureRandom = new SecureRandom()

    void createSslContext() {
//        jsseProvider = JsseProviders.BC
        //tlsProtocolVersion = SslProtocolsEnum.TLSv1_2
        trustManager.acceptedIssuers1 = []
        if(sslContext==null) {
            sslContext = net.sf.jremoterun.utilities.nonjdk.net.ssl.SslChecksDisable.sslContextCreator2(tlsProtocolVersion, jsseProvider)
        }
        if(x509Algo==null) {
            if(jsseProvider== JsseProviders.BC){
                x509Algo ='X.509'
            }else {
                x509Algo = KeyManagerFactory.getDefaultAlgorithm()
            }
        }
        if(generatorBc.rootCert==null){
            generatorBc.createSelfSigned('127.0.0.1',true)
        }else {
            generatorBc.createCert();
        }
        KeyStore keyStoreBc = generatorBc.generateKeyStore();
//        KeyStore keyStoreSun = new CertificatedGeneratorJdk().generateKeyStore();
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance('X.509',JsseProviders.BC.createProvider())
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(x509Algo,jsseProvider.createProvider())
//        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm(),JsseProviders.jdkInternal.createProvider())
//        keyManagerFactory.init(keyStoreSun,keyPass)
        keyManagerFactory.init(keyStoreBc,keyPass)
        KeyManager[] keyManagers =keyManagerFactory.getKeyManagers()
        sslContext.init(keyManagers,null, secureRandom)
    }




    void prepare() {
        if (sslContext == null) {
            createSslContext()
        }
        if (sslSocketFactory == null) {
            sslSocketFactory = sslContext.getServerSocketFactory()
        }

    }

    void createServerSocket( int port) {
        prepare()
        socket = sslSocketFactory.createServerSocket(port) as SSLServerSocket;
    }

}
