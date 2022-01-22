package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.apache.http.Header
import org.apache.http.HttpRequest
import org.apache.http.auth.Credentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.methods.HttpRequestWrapper
import org.apache.http.client.methods.HttpUriRequest
import org.apache.http.impl.auth.BasicScheme
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext;

import java.util.logging.Logger;

@CompileStatic
class UsernamePasswordHeader extends HttpRequestLogger{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<String> urlAuthPrefixes
    public UsernamePasswordCredentials credentials;

    UsernamePasswordHeader(List<String> urlAuthPrefixes, UsernamePasswordCredentials credentials, JrrHttpUtils jrrHttpUtils) {
        super(jrrHttpUtils)
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
