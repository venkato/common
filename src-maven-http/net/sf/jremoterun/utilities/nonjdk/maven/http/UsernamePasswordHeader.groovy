package net.sf.jremoterun.utilities.nonjdk.maven.http;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.maven.wagon.providers.http.httpclient.Header
import org.apache.maven.wagon.providers.http.httpclient.HttpException
import org.apache.maven.wagon.providers.http.httpclient.HttpHost
import org.apache.maven.wagon.providers.http.httpclient.HttpRequest
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
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.BasicScheme
import org.apache.maven.wagon.providers.http.httpclient.impl.auth.NTLMSchemeFactory
import org.apache.maven.wagon.providers.http.httpclient.impl.client.*
import org.apache.maven.wagon.providers.http.httpclient.protocol.BasicHttpContext
import org.apache.maven.wagon.providers.http.httpclient.protocol.HttpContext
//import org.apache.http.Header
//import org.apache.http.HttpRequest
//import org.apache.http.auth.Credentials
//import org.apache.http.auth.UsernamePasswordCredentials
//import org.apache.http.client.methods.HttpRequestWrapper
//import org.apache.http.client.methods.HttpUriRequest
//import org.apache.http.impl.auth.BasicScheme
//import org.apache.http.protocol.BasicHttpContext
//import org.apache.http.protocol.HttpContext;

import java.util.logging.Logger;

@CompileStatic
class UsernamePasswordHeader extends HttpRequestLogger{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<String> urlAuthPrefixes
    public UsernamePasswordCredentials credentials;

    UsernamePasswordHeader(List<String> urlAuthPrefixes, UsernamePasswordCredentials credentials,JrrMavenHttpUtils jrrHttpUtils) {
        super(jrrHttpUtils)
        //super(credentials)
        this.urlAuthPrefixes = urlAuthPrefixes
        this.credentials = credentials
        assert urlAuthPrefixes!=null
        assert credentials!=null
        assert urlAuthPrefixes.size()>0
    }

    @Override
    void onURIRequest(String uri1, HttpRequestWrapper request, HttpContext context) {
        if(isAddAuth(uri1)){
            boolean headerAdded = auth1(request, context, credentials)
            if(headerAdded){
                log.log(level,"auth header added for ${uri1}")
            }else{
                log.log(level,"dup auth header for ${uri1}")
            }
        }
    }

    @Override
    void logGenericRequest(HttpUriRequest request, HttpContext context) {
        String uri1 = request.getURI().toString()
        if(isAddAuth(uri1)){
            boolean headerAdded = auth1(request, context, credentials)
            if(headerAdded){
                log.log(level,"auth header added for ${request.getMethod()} ${uri1}")
            }else{
                log.log(level,"dup auth header for ${request.getMethod()} ${uri1}")
            }
        }
    }

    static boolean auth2(HttpRequest request, UsernamePasswordCredentials credentials3) {
        return auth1(request,new BasicHttpContext(),credentials3)
    }

    /**
     * sample code for manual auth
     * @see org.apache.commons.httpclient.auth.HttpAuthenticator#selectAuthScheme
     */
    static boolean auth1(HttpRequest request, HttpContext context, Credentials credentials3) {
        Header header = new BasicScheme().authenticate(credentials3, request, context)
        if (request.containsHeader(header.getName())) {
            return false
        }else{
            request.addHeader(header)
            return true
        }
    }

    boolean isAddAuth(String url){
        String find1 = urlAuthPrefixes.find { url.contains(it) }
        if(find1==null){
            return false
        }
        return true
    }
}
