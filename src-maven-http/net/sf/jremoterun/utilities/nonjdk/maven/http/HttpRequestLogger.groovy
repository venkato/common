package net.sf.jremoterun.utilities.nonjdk.maven.http;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.maven.wagon.providers.http.httpclient.HttpException
import org.apache.maven.wagon.providers.http.httpclient.HttpHost
import org.apache.maven.wagon.providers.http.httpclient.HttpRequest
import org.apache.maven.wagon.providers.http.httpclient.HttpRequestInterceptor
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthSchemeProvider
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScope
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials
import org.apache.maven.wagon.providers.http.httpclient.auth.NTCredentials
import org.apache.maven.wagon.providers.http.httpclient.auth.UsernamePasswordCredentials
import org.apache.maven.wagon.providers.http.httpclient.client.AuthenticationStrategy
import org.apache.maven.wagon.providers.http.httpclient.client.config.AuthSchemes
import org.apache.maven.wagon.providers.http.httpclient.client.config.RequestConfig
import org.apache.maven.wagon.providers.http.httpclient.client.methods.HttpRequestWrapper
import org.apache.maven.wagon.providers.http.httpclient.client.methods.HttpUriRequest
import org.apache.maven.wagon.providers.http.httpclient.client.protocol.HttpClientContext
import org.apache.maven.wagon.providers.http.httpclient.config.Registry
import org.apache.maven.wagon.providers.http.httpclient.config.RegistryBuilder
import org.apache.maven.wagon.providers.http.httpclient.conn.routing.HttpRoute
import org.apache.maven.wagon.providers.http.httpclient.conn.routing.HttpRoutePlanner
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.NTLMSchemeFactory
import org.apache.maven.wagon.providers.http.httpclient.impl.client.*
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpContext
//import org.apache.http.Header
//import org.apache.http.HttpException
//import org.apache.http.HttpRequest
//import org.apache.http.HttpRequestInterceptor
//import org.apache.http.RequestLine
//import org.apache.http.auth.Credentials
//import org.apache.http.auth.UsernamePasswordCredentials
//import org.apache.http.client.methods.HttpGet
//import org.apache.http.client.methods.HttpUriRequest
//import org.apache.http.impl.auth.BasicScheme
//import org.apache.http.protocol.BasicHttpContext
//import org.apache.http.protocol.HttpContext

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class HttpRequestLogger implements HttpRequestInterceptor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Level level = Level.INFO

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
        log.log(level, "${uri1}")
    }


    void logGenericRequest(HttpUriRequest request, HttpContext context) {
        log.log(level, "${request.getMethod()} ${request.getURI()}")
    }



}
