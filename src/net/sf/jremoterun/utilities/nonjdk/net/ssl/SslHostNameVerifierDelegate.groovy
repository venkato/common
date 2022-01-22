package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession
import java.util.logging.Logger

/**
 @see org.apache.http.conn.ssl.NoopHostnameVerifier
 */
@CompileStatic
class SslHostNameVerifierDelegate implements HostnameVerifier {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public HostnameVerifier delegate

    public JrrHttpUtils httpUtils;
    public Object handshaker;

    SslHostNameVerifierDelegate(HostnameVerifier delegate, JrrHttpUtils httpUtils) {
        this.delegate = delegate
        this.httpUtils = httpUtils
    }

    @Override
    boolean verify(String s, SSLSession sslSession) {
        if(handshaker==null) {
            handshaker = httpUtils.sslSocketFactory.fetchHandshaker()
            assert handshaker!=null
            log.info "got handshaker"
        }else{
            log.info "handshaker already received"
        }

        return delegate.verify(s,sslSession)
    }


}
