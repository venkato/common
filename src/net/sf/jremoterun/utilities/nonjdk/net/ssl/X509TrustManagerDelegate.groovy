package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils

import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate;
import java.util.logging.Logger;

@CompileStatic
class X509TrustManagerDelegate implements X509TrustManager{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public X509TrustManager delegate;
    public SslSocketCatcher httpUtils;
    public Object handshaker;


    X509TrustManagerDelegate(X509TrustManager delegate, SslSocketCatcher httpUtils) {
        this.delegate = delegate
        this.httpUtils = httpUtils
    }

    @Override
    void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        f1()
        delegate.checkClientTrusted(chain,authType)
    }

    @Override
    void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        f1()
        delegate.checkServerTrusted(chain,authType)
    }

    @Override
    X509Certificate[] getAcceptedIssuers() {
        return delegate.getAcceptedIssuers()
    }

    void f1(){
        if(handshaker==null) {
//            assert httpUtils.sslSocketFactory!=null
//            handshaker = httpUtils.fetchHandshaker()
//            assert handshaker!=null
//            log.info "got handshaker"
        }else{
            log.info "handshaker already received"
        }
    }
}
