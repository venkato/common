package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
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

    @Override
    HttpResponse execute(HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
        return super.execute(request, conn, context)
    }
}
