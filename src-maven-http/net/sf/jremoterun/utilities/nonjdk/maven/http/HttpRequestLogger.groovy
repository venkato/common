package net.sf.jremoterun.utilities.nonjdk.maven.http

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ApacheHttpUrlTracker
import org.apache.maven.wagon.providers.http.httpclient.HttpException
import org.apache.maven.wagon.providers.http.httpclient.HttpRequest
import org.apache.maven.wagon.providers.http.httpclient.HttpRequestInterceptor
import org.apache.maven.wagon.providers.http.httpclient.client.methods.HttpRequestWrapper
import org.apache.maven.wagon.providers.http.httpclient.client.methods.HttpUriRequest
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpContext

import java.util.logging.Level

import java.util.logging.Logger

@CompileStatic
class HttpRequestLogger implements HttpRequestInterceptor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Level level = Level.INFO
    public JrrMavenHttpUtils jrrHttpUtils;

    HttpRequestLogger(JrrMavenHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }

    @Override
    void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request instanceof HttpRequestWrapper) {
            logGetRequest2(request, context)
        } else {
            if (request instanceof HttpUriRequest) {
                HttpUriRequest httpGet = (HttpUriRequest) request;
                logGenericRequest(httpGet, context)
            } else {
                log.log(level, "strange request : ${request.getClass().getName()} ${context.getClass().getName()} ${request.getRequestLine()}")
            }

        }
    }


    void logGetRequest2(HttpRequestWrapper request, HttpContext context) {
        String uri1 = getRequestUrl(request)
        onURIRequest(uri1, request, context)
    }


    String getRequestUrl(HttpRequestWrapper httpRequest) {
        String uri = httpRequest.getOriginal().getRequestLine().getUri()
        return uri
    }

    void onURIRequest(String uri1, HttpRequestWrapper request, HttpContext context) {
        jrrHttpUtils.onUrl(uri1, ApacheHttpUrlTracker.request)
        log.log(level, "${uri1}")
    }


    void logGenericRequest(HttpUriRequest request, HttpContext context) {
        jrrHttpUtils.onUrl(request.getURI().toString(), ApacheHttpUrlTracker.request)
        log.log(level, "${request.getMethod()} ${request.getURI()}")
    }



}
