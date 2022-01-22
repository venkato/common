package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.logging.Logger

@CompileStatic
class X509TrustManagerCertCollectionDelegate implements X509TrustManager {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<X509Certificate> chains = []
    public boolean accept = true
    public List<X509Certificate> acceptedIssuers1
    public X509TrustManager trustManagerNested

    @Override
    void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        chains.addAll(chain.toList())
        trustManagerNested.checkClientTrusted(chain,authType)
        onFinally1()
    }

    @Override
    void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        chains.addAll(chain.toList())
        trustManagerNested.checkServerTrusted(chain,authType)
        onFinally1()
    }

    void onFinally1(){
        if(!accept){
            throw new CertificateExceptionJrr();
        }
    }

    @Override
    X509Certificate[] getAcceptedIssuers() {
        return trustManagerNested.getAcceptedIssuers()
    }

}
