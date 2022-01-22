package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.HttpException
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.protocol.HttpContext

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class HttpRequestLogger implements HttpRequestInterceptor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Level level = Level.INFO
    public JrrHttpUtils jrrHttpUtils

    HttpRequestLogger(JrrHttpUtils jrrHttpUtils) {
        this.jrrHttpUtils = jrrHttpUtils
    }

    @Override
    void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request instanceof org.apache.http.client.methods.HttpRequestWrapper) {
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


    void logGetRequest2(org.apache.http.client.methods.HttpRequestWrapper request, HttpContext context) {
        String uri1 = getRequestUrl(request)
        onURIRequest(uri1, request, context)
    }


    String getRequestUrl(org.apache.http.client.methods.HttpRequestWrapper httpRequest) {
        String uri = httpRequest.getOriginal().getRequestLine().getUri()
        return uri
    }

    void onURIRequest(String uri1, org.apache.http.client.methods.HttpRequestWrapper request, HttpContext context) {
        jrrHttpUtils.onUrl(uri1,ApacheHttpUrlTracker.request);
        log.log(level, "${uri1}")
    }


    void logGenericRequest(HttpUriRequest request, HttpContext context) {
        jrrHttpUtils.onUrl(request.getURI().toString(),ApacheHttpUrlTracker.request);
        log.log(level, "${request.getMethod()} ${request.getURI()}")
    }

    static boolean auth2(HttpRequest request, UsernamePasswordCredentials credentials) {
        return UsernamePasswordHeader.auth2(request, credentials)
    }

    /**
     * sample code for manual auth
     * @see org.apache.commons.httpclient.auth.HttpAuthenticator#selectAuthScheme
     */
    static boolean auth1(HttpRequest request, HttpContext context, Credentials credentials) {
        return UsernamePasswordHeader.auth1(request, context, credentials)
    }


}
