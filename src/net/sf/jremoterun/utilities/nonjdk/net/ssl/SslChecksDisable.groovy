package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.JsseProviders
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.SslProtocolsEnum

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import java.security.SecureRandom
import java.util.logging.Logger;

@CompileStatic
class SslChecksDisable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory()
    public static HostnameVerifier hostnameVerifier = HttpsURLConnection.getDefaultHostnameVerifier()
    public static SSLContext sslContextDefault = SSLContext.getDefault()
    public static SslProtocolsEnum defaultProtocol = SslProtocolsEnum.TLS
    public static SecureRandom secureRandom1 = new SecureRandom()


    static void allowAll() {
        HttpsURLConnection.setDefaultHostnameVerifier(new SslHostNameVerifierAllowAll())
        SSLContext sSLContext = createAllTrustSslContext()
        SSLContext.setDefault(sSLContext)
        HttpsURLConnection.setDefaultSSLSocketFactory(sSLContext.getSocketFactory())
    }

    static SSLContext createAllTrustSslContext() {
        TrustManager trustManager = new SslAllTrustManager()
        return sslContextCreator(defaultProtocol, null, trustManager)
    }

    static SSLContext sslContextCreator(SslProtocolsEnum sslProtocolsEnum, JsseProviders jsseProvider, TrustManager trustManager) {
        SSLContext ssLContext = sslContextCreator2(sslProtocolsEnum,jsseProvider)
        TrustManager[] trustManagers = [trustManager]
        ssLContext.init(null, trustManagers, secureRandom1)
        return ssLContext
    }

    static SSLContext sslContextCreator2(SslProtocolsEnum sslProtocolsEnum, JsseProviders jsseProvider) {
        SSLContext ssLContext
        if (jsseProvider == null) {
            ssLContext = SSLContext.getInstance(sslProtocolsEnum.customName)
        } else {
            ssLContext = SSLContext.getInstance(sslProtocolsEnum.customName, jsseProvider.createProvider())
        }
        return ssLContext
    }


}
