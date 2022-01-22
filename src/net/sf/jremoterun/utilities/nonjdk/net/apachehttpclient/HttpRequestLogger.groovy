package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.http.Header
import org.apache.http.HttpException
import org.apache.http.HttpRequest
import org.apache.http.HttpRequestInterceptor
import org.apache.http.RequestLine
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class HttpRequestLogger implements HttpRequestInterceptor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Level level = Level.INFO

    @Override
    void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        if (request instanceof org.apache.http.client.methods.HttpRequestWrapper) {
            logGetRequest2(request, context)
        } else {
            if (request instanceof HttpGet) {
                HttpGet httpGet = (HttpGet) request;
                logGetRequest(httpGet, context)
            } else {
                log.log(level, "strange request : ${request.getClass().getName()} ${context.getClass().getName()} ${request.getRequestLine()}")
            }
        }
    }


    void logGetRequest2(org.apache.http.client.methods.HttpRequestWrapper request, HttpContext context) {
        HttpRequest original = request.getOriginal()
        RequestLine requestLine = original.getRequestLine()
        String uri1 = requestLine.getUri()
        onURIRequest(uri1, request, context)
    }

    void onURIRequest(String uri1, org.apache.http.client.methods.HttpRequestWrapper request, HttpContext context) {
        log.log(level, "${uri1}")
    }


    void logGetRequest(HttpGet request, HttpContext context) {
        log.log(level, "get ${request.getURI()}")
    }

    static boolean auth2(HttpRequest request,UsernamePasswordCredentials credentials) {
        return auth1(request,new BasicHttpContext(),credentials)
    }

    /**
     * sample code for manual auth
     * @see org.apache.commons.httpclient.auth.HttpAuthenticator#selectAuthScheme
     */
    static boolean auth1(HttpRequest request, HttpContext context, Credentials credentials) {
        Header header = new BasicScheme().authenticate(credentials, request, context)
        if (request.containsHeader(header.getName())) {
            return false
        }else{
            request.addHeader(header)
            return true
        }
    }


}
