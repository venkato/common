package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.HttpEntity
import org.apache.http.HttpException
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.http.HttpResponseInterceptor
import org.apache.http.HttpStatus
import org.apache.http.StatusLine
import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.protocol.HttpContext
import org.apache.http.protocol.HttpCoreContext

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class HttpResponseLogger implements HttpResponseInterceptor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Level level = Level.INFO

    public JrrHttpUtils jrrHttpUtils


    public boolean dumpContentIfOther = false
    public HashSet<Integer> dumpsSpecificResponseStatuses = new HashSet<>()

    HttpResponseLogger(JrrHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }
/**
 * @see org.apache.http.impl.execchain.ProtocolExec
 */
    @Override
    void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpRequest httpRequest = context.getAttribute(HttpCoreContext.HTTP_REQUEST) as HttpRequest
        process1(httpRequest, response, context)
    }

    void process1(HttpRequest httpRequest, HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (httpRequest instanceof HttpRequestWrapper) {
            HttpRequestWrapper requestWrapper1 = (HttpRequestWrapper) httpRequest;
            process2(requestWrapper1, response, context)
        } else {
            process3(httpRequest, response, context)
        }
    }

    void process3(HttpRequest httpRequest, HttpResponse response, HttpContext context) throws HttpException, IOException {
        log.log(level, "${response.getStatusLine()} strangeRequest=${httpRequest}")
    }

    void process2(org.apache.http.client.methods.HttpRequestWrapper httpRequest, HttpResponse response, HttpContext context) throws HttpException, IOException {
        String uri = getRequestUrl(httpRequest)
        process4(uri, httpRequest, response, context)
    }

    String getRequestUrl(org.apache.http.client.methods.HttpRequestWrapper httpRequest) {
        String uri = httpRequest.getOriginal().getRequestLine().getUri()
        return uri
    }


    boolean isLogOutput(String url, org.apache.http.client.methods.HttpRequestWrapper httpRequest, HttpResponse response, HttpContext context) {
        StatusLine statusLine = response.getStatusLine()
        int statusCode = statusLine.getStatusCode()
        if (statusCode == HttpStatus.SC_OK) {
            return false
        }
//        if (statusCode == HttpStatus.SC_UNPROCESSABLE_ENTITY) {
//            Thread.dumpStack()
//            return true
//        }
        if(dumpsSpecificResponseStatuses.contains(statusCode)){
            return true
        }
        return dumpContentIfOther
    }

    void process4(String url, org.apache.http.client.methods.HttpRequestWrapper httpRequest, HttpResponse response, HttpContext context) throws HttpException, IOException {
        StatusLine statusLine = response.getStatusLine()
        if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
            jrrHttpUtils.onUrl(url, ApacheHttpUrlTracker.response200)
        } else {
            jrrHttpUtils.onUrl(url, ApacheHttpUrlTracker.responseOther)
        }
        if (isLogOutput(url,httpRequest,response,context)) {
            byte[] msgs = getOutputDump(response)
            if (msgs == null) {
                log.info "${statusLine} entity empty"
            } else {
                log.info "${statusLine} entity=${new String(msgs)}"

            }
        }

        log.log(level, "${statusLine} ${url}")
    }

    byte[] getOutputDump(HttpResponse response) {
        HttpEntity entity = response.getEntity()
        if (entity == null) {
            return null
        }
        ByteArrayOutputStream out99 = new ByteArrayOutputStream()
        entity.writeTo(out99)
        byte[] msgs = out99.toByteArray()

        return msgs
    }

}
