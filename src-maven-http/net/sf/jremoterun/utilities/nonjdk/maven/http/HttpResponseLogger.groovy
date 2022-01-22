package net.sf.jremoterun.utilities.nonjdk.maven.http;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.maven.wagon.providers.http.httpclient.HttpException
import org.apache.maven.wagon.providers.http.httpclient.HttpHost
import org.apache.maven.wagon.providers.http.httpclient.HttpRequest
import org.apache.maven.wagon.providers.http.httpclient.HttpRequestInterceptor
import org.apache.maven.wagon.providers.http.httpclient.HttpResponse
import org.apache.maven.wagon.providers.http.httpclient.HttpResponseInterceptor
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

    /**
     * @see org.apache.http.impl.execchain.ProtocolExec
     */
    @Override
    void process(HttpResponse response, HttpContext context) throws HttpException, IOException {
        HttpRequest httpRequest = context.getAttribute(HttpCoreContext.HTTP_REQUEST) as HttpRequest
        process1(httpRequest,response,context)
    }

    void process1(HttpRequest httpRequest,HttpResponse response, HttpContext context) throws HttpException, IOException {
        if (httpRequest instanceof HttpRequestWrapper) {
            HttpRequestWrapper requestWrapper1 = (HttpRequestWrapper) httpRequest;
            process2(requestWrapper1,response,context)
        }else{
            process3(httpRequest,response,context)
        }
    }

    void process3(HttpRequest httpRequest,HttpResponse response, HttpContext context) throws HttpException, IOException {
        log.log(level, "${response.getStatusLine()} strangeRequest=${httpRequest}")
    }

    void process2(HttpRequestWrapper httpRequest,HttpResponse response, HttpContext context) throws HttpException, IOException {
        String uri = getRequestUrl(httpRequest)
        process4(uri,httpRequest,response,context)
    }

    String getRequestUrl(HttpRequestWrapper httpRequest){
        String uri = httpRequest.getOriginal().getRequestLine().getUri()
        return uri
    }

    void process4(String url,HttpRequestWrapper httpRequest,HttpResponse response, HttpContext context) throws HttpException, IOException {
        log.log(level,"${response.getStatusLine()} ${url}")
    }
}
