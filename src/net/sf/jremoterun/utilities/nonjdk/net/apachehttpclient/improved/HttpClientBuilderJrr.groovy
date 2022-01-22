package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.improved

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.ConnectionReuseStrategy
import org.apache.http.client.AuthenticationStrategy
import org.apache.http.client.UserTokenHandler
import org.apache.http.conn.ConnectionKeepAliveStrategy
import org.apache.http.conn.HttpClientConnectionManager
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.execchain.ClientExecChain
import org.apache.http.impl.execchain.MainClientExec
import org.apache.http.protocol.HttpProcessor
import org.apache.http.protocol.HttpRequestExecutor;

import java.util.logging.Logger;

@CompileStatic
class HttpClientBuilderJrr extends HttpClientBuilder{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrHttpUtils jrrHttpUtils

    HttpClientBuilderJrr(JrrHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
        setRequestExecutor(new HttpRequestExecutorJrr(jrrHttpUtils))
    }

    @Override
    protected ClientExecChain createMainExec(HttpRequestExecutor requestExec, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, HttpProcessor proxyHttpProcessor, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler) {
        //log.info "connManager = ${connManager.getClass().getName()}"
        return new MainClientExecJrr(
                requestExec,
                connManager,
                reuseStrategy,
                keepAliveStrategy,
                proxyHttpProcessor,
                targetAuthStrategy,
                proxyAuthStrategy,
                userTokenHandler,jrrHttpUtils);
        //return super.createMainExec(requestExec, connManager, reuseStrategy, keepAliveStrategy, proxyHttpProcessor, targetAuthStrategy, proxyAuthStrategy, userTokenHandler)
    }
}
