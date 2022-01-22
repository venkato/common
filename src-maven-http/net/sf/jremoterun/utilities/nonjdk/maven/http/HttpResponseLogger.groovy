package net.sf.jremoterun.utilities.nonjdk.maven.http;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ApacheHttpUrlTracker
import org.apache.maven.wagon.providers.http.httpclient.HttpException
import org.apache.maven.wagon.providers.http.httpclient.HttpRequest
import org.apache.maven.wagon.providers.http.httpclient.HttpResponse
import org.apache.maven.wagon.providers.http.httpclient.HttpResponseInterceptor
import org.apache.maven.wagon.providers.http.httpclient.client.methods.HttpRequestWrapper
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpContext
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpCoreContext

//import org.apache.http.HttpException
//import org.apache.http.HttpRequest
//import org.apache.http.HttpResponse
//import org.apache.http.HttpResponseInterceptor
//import org.apache.http.client.methods.HttpRequestWrapper
//import org.apache.http.protocol.HttpContext
//import org.apache.http.protocol.HttpCoreContext

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class HttpResponseLogger implements HttpResponseInterceptor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Level level = Level.INFO

    public JrrMavenHttpUtils jrrHttpUtils;

    HttpResponseLogger(JrrMavenHttpUtils jrrHttpUtils) {
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

    void process2(HttpRequestWrapper httpRequest, HttpResponse response, HttpContext context) throws HttpException, IOException {
        String uri = getRequestUrl(httpRequest)
        process4(uri, httpRequest, response, context)
    }

    String getRequestUrl(HttpRequestWrapper httpRequest) {
        String uri = httpRequest.getOriginal().getRequestLine().getUri()
        return uri
    }

    void process4(String url, HttpRequestWrapper httpRequest, HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (response.getStatusLine().statusCode == 200) {
            jrrHttpUtils.onUrl(url, ApacheHttpUrlTracker.response200)
        } else {
            jrrHttpUtils.onUrl(url, ApacheHttpUrlTracker.responseOther)
        }
        log.log(level, "${response.getStatusLine()} ${url}")
    }
}
