package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
import org.eclipse.jgit.transport.http.HttpConnection
import org.eclipse.jgit.transport.http.apache.HttpClientConnection
import org.eclipse.jgit.transport.http.apache.HttpClientConnectionFactory;

import java.util.logging.Logger;

@CompileStatic
class GitHttpFactoryJrr extends HttpClientConnectionFactory{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();




    GitHttpFactoryJrr() {
    }

    @Override
    public HttpConnection create(URL url) throws IOException {
        return create2(url,null);
    }

    @Override
    public HttpConnection create(URL url, Proxy proxy) throws IOException {
        return create2(url, proxy);
    }

    JrrHttpUtils createJrrHttpUtils(){
        return new JrrHttpUtils()
    }

    HttpConnection create2(URL url, Proxy proxy) throws IOException{
        JrrHttpUtils jrrHttpUtils =createJrrHttpUtils()
        configure(jrrHttpUtils)
        jrrHttpUtils.createClient()
        return new GitJrrHttpClientConnection(url.toString(), proxy,jrrHttpUtils.httpClient1);
    }

    void configure(JrrHttpUtils jrrHttpUtils ){
        jrrHttpUtils.addRequestLogger(false)
        jrrHttpUtils.addResponseLogger(false)
    }


    void setSelf(){
        org.eclipse.jgit.transport.HttpTransport.setConnectionFactory(this)
    }





}
