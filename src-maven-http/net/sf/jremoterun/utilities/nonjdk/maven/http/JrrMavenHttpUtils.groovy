package net.sf.jremoterun.utilities.nonjdk.maven.http

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.maven.http.win.WindowsCredentialsProvider
import net.sf.jremoterun.utilities.nonjdk.maven.http.win.WindowsNTLMSchemeFactory
import net.sf.jremoterun.utilities.nonjdk.net.ProxyTrackerI
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ApacheHttpUrlTracker
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxyHostInfo
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySelectI
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthSchemeProvider
import org.apache.maven.wagon.providers.http.httpclient.auth.AuthScope
import org.apache.maven.wagon.providers.http.httpclient.auth.Credentials
import org.apache.maven.wagon.providers.http.httpclient.auth.NTCredentials
import org.apache.maven.wagon.providers.http.httpclient.client.AuthenticationStrategy
import org.apache.maven.wagon.providers.http.httpclient.client.config.AuthSchemes
import org.apache.maven.wagon.providers.http.httpclient.config.Registry
import org.apache.maven.wagon.providers.http.httpclient.config.RegistryBuilder
import org.apache.maven.wagon.providers.http.httpclient.impl.client.*

import java.util.logging.Logger

/**
 * @see net.sf.jremoterun.utilities.nonjdk.net.JrrHttpUtils
 */
@CompileStatic
public class JrrMavenHttpUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClRef clRef = new ClRef('org.apache.maven.wagon.providers.http.wagon.shared.AbstractHttpClientWagon')

    public HttpClientBuilder httpClientBuilder = HttpClients.custom()
    public CloseableHttpClient httpClient1;
    public AuthSchemeProvider ntlmSchemeFactory = new NTLMSchemeFactoryMavenJrr(this)
    public AuthenticationStrategy proxyAuthenticationStrategy = new ProxyAuthenticationStrategyMavenJrr(this);
    public BasicCredentialsProvider credentialsProvider1 = new BasicCredentialsProviderMavenJrr(this);
    public Credentials credentials;
    public  ProxyHostInfo proxyHostInfo
    public String proxyip
//    public int proxyPort
    public boolean doLogging = true

    JrrMavenHttpUtils() {
    }


    void onUrl(String url, ApacheHttpUrlTracker type){
    }


    void createClient() {
        httpClient1 = httpClientBuilder.build();
    }

    void setNTCredentials(String username,String password,String domain){
        credentials = new NTCredentials(username,password,null,domain)
    }

    @Deprecated
    void setCred(NTCredentials credentials, ProxyHostInfo proxyHostInfo1) {
        this.credentials = credentials;
        this.proxyHostInfo =proxyHostInfo1
        setCred2()
    }



    void addNtlmWinNativePrepare(){
        ntlmSchemeFactory = new WindowsNTLMSchemeFactory()
        WindowsCredentialsProvider windowsCredentialsProvider = new WindowsCredentialsProvider(new net.sf.jremoterun.utilities.nonjdk.maven.http.BasicCredentialsProviderMavenJrr(this))
        httpClientBuilder.setDefaultCredentialsProvider(windowsCredentialsProvider)
    }

    void setWindNativeAuth(){
        org.apache.maven.wagon.providers.http.httpclient.config.Registry<org.apache.maven.wagon.providers.http.httpclient.auth.AuthSchemeProvider> authSchemeProviderRegistry = org.apache.maven.wagon.providers.http.httpclient.config.RegistryBuilder.<org.apache.maven.wagon.providers.http.httpclient.auth.AuthSchemeProvider> create().register(org.apache.maven.wagon.providers.http.httpclient.client.config.AuthSchemes.NTLM, ntlmSchemeFactory).build();
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
    }

    void addNtlmWinNativeAll(){
        addNtlmWinNativePrepare()
        setWindNativeAuth()
    }

    void setCred2() {
        credentialsProvider1.setCredentials(new AuthScope(proxyHostInfo.proxyHost,proxyHostInfo.proxyPort), credentials);
        InetAddress name = InetAddress.getByName(proxyHostInfo.proxyHost)
        proxyip = name.getHostAddress()
        if (proxyip != proxyHostInfo.proxyHost) {
            credentialsProvider1.setCredentials(new AuthScope(proxyip, proxyHostInfo.proxyPort), credentials);
        }
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
        httpClientBuilder.setProxyAuthenticationStrategy(proxyAuthenticationStrategy)
        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
//        setRouterPlanner()
    }

    @Deprecated
    void addProxyNtlmAuth3(NTCredentials credentials, ProxyHostInfo proxyHostInfo1) {
        setCred(credentials, proxyHostInfo1)
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
        httpClientBuilder.setProxyAuthenticationStrategy(proxyAuthenticationStrategy)
        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
//        setRouterPlanner()
    }

    void setRouterPlanner(ProxySelectI proxySelect2I, ProxyHostInfo proxyHostInfo, ProxyTrackerI proxyTracker) {
        JrrHttpRoutePlanner2 httpRoutePlanner2=new JrrHttpRoutePlanner2(proxySelect2I,proxyHostInfo);
        httpRoutePlanner2.proxyTracker = proxyTracker
        httpClientBuilder.setRoutePlanner(httpRoutePlanner2)
    }

    void addLoggers(){
        httpClientBuilder.addInterceptorFirst (new HttpRequestLogger(this))
        httpClientBuilder.addInterceptorFirst (new HttpResponseLogger(this))
    }

    void setRef() {
        createClient()
        JrrClassUtils.setFieldValue(clRef, 'httpClient', httpClient1);
    }


}
