package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.improved

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.HttpClientConnection
import org.apache.http.HttpException
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.protocol.HttpContext
import org.apache.http.protocol.HttpRequestExecutor;

import java.util.logging.Logger;

@CompileStatic
class HttpRequestExecutorJrr extends HttpRequestExecutor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrHttpUtils jrrHttpUtils;

    HttpRequestExecutorJrr(JrrHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }

    @Override
    protected HttpResponse doSendRequest(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
        jrrHttpUtils.beforeSendRequest(request,conn,context)
//        log.info "${conn.getClass().getName()}"
        return super.doSendRequest(request, conn, context)
    }
}
