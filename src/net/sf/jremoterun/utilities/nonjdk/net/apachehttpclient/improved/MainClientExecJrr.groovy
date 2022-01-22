package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.improved

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.ConnectionReuseStrategy
import org.apache.http.HttpEntity
import org.apache.http.HttpEntityEnclosingRequest
import org.apache.http.HttpException
import org.apache.http.client.AuthenticationStrategy
import org.apache.http.client.UserTokenHandler
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpExecutionAware
import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.conn.ConnectionKeepAliveStrategy
import org.apache.http.conn.HttpClientConnectionManager
import org.apache.http.conn.routing.HttpRoute
import org.apache.http.impl.execchain.MainClientExec
import org.apache.http.protocol.HttpProcessor
import org.apache.http.protocol.HttpRequestExecutor;

import java.util.logging.Logger;

@CompileStatic
class MainClientExecJrr extends MainClientExec{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrHttpUtils jrrHttpUtils

    MainClientExecJrr(HttpRequestExecutor requestExecutor, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, HttpProcessor proxyHttpProcessor, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler, JrrHttpUtils jrrHttpUtils) {
        super(requestExecutor, connManager, reuseStrategy, keepAliveStrategy, proxyHttpProcessor, targetAuthStrategy, proxyAuthStrategy, userTokenHandler)
        this.jrrHttpUtils = jrrHttpUtils
    }

    MainClientExecJrr(HttpRequestExecutor requestExecutor, HttpClientConnectionManager connManager, ConnectionReuseStrategy reuseStrategy, ConnectionKeepAliveStrategy keepAliveStrategy, AuthenticationStrategy targetAuthStrategy, AuthenticationStrategy proxyAuthStrategy, UserTokenHandler userTokenHandler) {
        super(requestExecutor, connManager, reuseStrategy, keepAliveStrategy, targetAuthStrategy, proxyAuthStrategy, userTokenHandler)
    }

    @Override
    CloseableHttpResponse execute(HttpRoute route, HttpRequestWrapper request, HttpClientContext context, HttpExecutionAware execAware) throws IOException, HttpException {
//        log.info "request = ${request.getClass().getName()} ${request}"
//        if (request instanceof HttpEntityEnclosingRequest) {
//            HttpEntityEnclosingRequest enclosingRequest = (HttpEntityEnclosingRequest) request;
//            HttpEntity entity1 = enclosingRequest.getEntity()
//
//            if(entity1!=null){
//                log.info "entity1 ${entity1.getClass().getName()} = ${entity1}"
//            }
//        }
        return super.execute(route, request, context, execAware)
    }
}
