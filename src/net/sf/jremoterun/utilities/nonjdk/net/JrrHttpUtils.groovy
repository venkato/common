package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.BasicCredentialsProviderJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.HttpRequestLogger
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.NTLMSchemeFactoryJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ProxyAuthenticationStrategyJrr
import net.sf.jremoterun.utilities.nonjdk.net.ssl.DelegatingSSLSocketFactory
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SSLConnectionSocketFactoryJrr
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslAllTrustManager
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslChecksDisable
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslHostNameVerifierAllowAll
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.JsseProviders
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.SslProtocolsEnum
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslSocketCatcher
import org.apache.http.Header
import org.apache.http.HttpHeaders
import org.apache.http.auth.AuthSchemeProvider
import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.auth.NTCredentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.AuthenticationStrategy
import org.apache.http.client.config.AuthSchemes
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.ssl.TrustStrategy
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import org.apache.http.ssl.SSLContextBuilder

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.logging.Level
import java.util.logging.Logger

/**
 * https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
 * @see net.sf.jremoterun.utilities.nonjdk.maven.http.JrrMavenHttpUtils
 */
@CompileStatic
public class JrrHttpUtils implements SslSocketCatcher{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public HttpClientBuilder httpClientBuilder = HttpClients.custom()
    //public HttpDebugAction httpDebugAction;
    public CloseableHttpClient httpClient1;
    public AuthSchemeProvider ntlmSchemeFactory = new NTLMSchemeFactoryJrr(this)
    public AuthenticationStrategy proxyAuthenticationStrategy = new ProxyAuthenticationStrategyJrr();
    public BasicCredentialsProvider credentialsProvider1 = new BasicCredentialsProviderJrr();
    public DelegatingSSLSocketFactory sslSocketFactory
//    public SslHostNameVerifierDelegate hostNameVerifierDelegate;
//    public SSLContext sslContext;

    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Last-Modified
    public SimpleDateFormat lastModifiedDateFormat = new SimpleDateFormat('EEE, dd MMM yyyy HH:mm:ss z')

    void createClient() {
        httpClient1 = httpClientBuilder.build();
    }

    void addLogger(boolean logAsWarning){
        assert httpClient1 ==null
        HttpRequestLogger requestLogger = new HttpRequestLogger();
        if(logAsWarning){
            requestLogger.level = Level.WARNING;
        }
        addLogger2(requestLogger)
    }

    void setUserPasswordForAny(UsernamePasswordCredentials usernamePasswordCredentials){
        assert httpClient1 ==null
        credentialsProvider1.setCredentials(AuthScope.ANY,usernamePasswordCredentials)
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
    }

    void addLogger2(HttpRequestLogger requestLogger){
        assert httpClient1 ==null
        httpClientBuilder.addInterceptorFirst(requestLogger)
    }

    // pass NTCredentials or org.apache.http.auth.UsernamePasswordCredentials
    void setCred(Credentials credential, String proxyHost, int proxyPort) {
        assert httpClient1==null
        credentialsProvider1.setCredentials(new AuthScope(proxyHost, proxyPort), credential);
        InetAddress name = InetAddress.getByName(proxyHost)
        String ipAddress = name.getHostAddress()
        if (ipAddress != proxyHost) {
            credentialsProvider1.setCredentials(new AuthScope(ipAddress, proxyPort), credential);
        }
    }

    NTCredentials createNTCredentials(String user, String password, String domain) {
        return new NTCredentials(user, password, null, domain);
    }

    /**
     * Set ProxySelector.getDefault() before !!
     * call setRouterPlanner() after
     */
    void addProxyCredentialGeneric(Credentials credential, String proxyHost, int proxyPort) {
        setCred(credential, proxyHost, proxyPort)
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
        httpClientBuilder.setProxyAuthenticationStrategy(proxyAuthenticationStrategy)
    }

    /**
     * Set ProxySelector.getDefault() before !!
     */
    void addProxyNtlmAuth3(NTCredentials credentials, String proxyHost, int proxyPort) {
        addProxyCredentialGeneric(credentials, proxyHost, proxyPort)
        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
        setRouterPlanner()
    }

    void setRouterPlanner() {
        SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault())
        httpClientBuilder.setRoutePlanner(routePlanner)
    }

    void addRedirectionNon() {
        httpClientBuilder.setRedirectStrategy(new RedirectStrategyNone())
    }


    void sslCheckDisable2(JsseProviders jsseProvider) {
        SslHostNameVerifierAllowAll hostNameVerifierAllowAll = new SslHostNameVerifierAllowAll()
        SSLSocketFactory sSLSocketFactory2 = SslChecksDisable.sslContextCreator(SslChecksDisable.defaultProtocol, jsseProvider, new SslAllTrustManager()).getSocketFactory()
        initSslParams(sSLSocketFactory2,hostNameVerifierAllowAll)
    }

    void sslCheckDisable() {
        SslHostNameVerifierAllowAll hostNameVerifierAllowAll = new SslHostNameVerifierAllowAll()
        initSslParams(SslChecksDisable.createAllTrustSslContext().getSocketFactory(),hostNameVerifierAllowAll)
    }

    void initSslParams(SSLSocketFactory sslSocketFactory2,HostnameVerifier hostNameVerifierAllowAll){
//        hostNameVerifierDelegate = new SslHostNameVerifierDelegate(hostNameVerifierAllowAll,this)
        httpClientBuilder.setSSLHostnameVerifier(hostNameVerifierAllowAll);
        if(sslSocketFactory==null) {
            sslSocketFactory = new DelegatingSSLSocketFactory( sslSocketFactory2);
        }

        SSLConnectionSocketFactoryJrr sslConnectionSocketFactory= new SSLConnectionSocketFactoryJrr(sslSocketFactory,hostNameVerifierAllowAll);
        httpClientBuilder.setSSLSocketFactory(sslConnectionSocketFactory);
    }


//    @Deprecated
//    void addProxyNtlmAuth(String user, String passport, String domain, String proxyHost, int proxyPort) {
//        NTCredentials credentials = new NTCredentials(user, passport, null, domain)
//        setCred(credentials,proxyHost,proxyPort)
//        HttpHost proxyHttpHost1 = new HttpHost(proxyHost, proxyPort);
//        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
//        httpClientBuilder.setProxy(proxyHttpHost1)
//        httpClientBuilder.setProxyAuthenticationStrategy(proxyAuthenticationStrategy)
//        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
//        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
//    }

    List<X509Certificate> getCertificatesFromHost(String host) {
        X509Certificate[] chains
        TrustManager trustManager = new X509TrustManager(){

            @Override
            void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                chains = chain;
            }

            @Override
            void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                chains = chain;
            }

            @Override
            X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0]
            }
        }
        SSLContext sSLContext = SslChecksDisable.sslContextCreator(SslProtocolsEnum.TLS, null,trustManager)
        initSslParams(sSLContext.getSocketFactory(),SSLConnectionSocketFactoryJrr.getDefaultHostnameVerifier())

        String urll
        if (host.startsWith('https://')) {
            urll = host
        } else {
            urll = "https://${host}"
        }
        HttpGet httpGet = new HttpGet(urll);
        createClient();
        //httpDebugAction.logActionRequest(httpGet)
        CloseableHttpResponse response = httpClient1.execute(httpGet);
        //httpDebugAction.logActionResponse(httpGet,response)
        JrrIoUtils.closeQuietly2(response, log)
        closeConnectionQuietly(httpGet)
        if (chains == null) {
            throw new NullPointerException("No certificates")
        }
        return chains.toList();
    }

    @Deprecated
    List<X509Certificate> getCertificatesFromHostOld(String host) {
        X509Certificate[] chains
        SSLContextBuilder builder = new SSLContextBuilder();
        TrustStrategy trustStrategy = new TrustStrategy() {

            @Override
            boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                chains = chain;
                return true;
            }
        }
        // for cert auth put private key certificate here for loadTrustMaterial method
        builder.loadTrustMaterial(null, trustStrategy);
        initSslParams(builder.build().getSocketFactory(),SSLConnectionSocketFactoryJrr.getDefaultHostnameVerifier())
//        httpClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactoryJrr(builder.build().getSocketFactory(),SSLConnectionSocketFactoryJrr.getDefaultHostnameVerifier()));

        String urll
        if (host.startsWith('https://')) {
            urll = host
        } else {
            urll = "https://${host}"
        }
        HttpGet httpGet = new HttpGet(urll);
        createClient();
        //httpDebugAction.logActionRequest(httpGet)
        CloseableHttpResponse response = httpClient1.execute(httpGet);
        //httpDebugAction.logActionResponse(httpGet,response)
        JrrIoUtils.closeQuietly2(response, log)
        closeConnectionQuietly(httpGet)
        if (chains == null) {
            throw new NullPointerException("No certificates")
        }
        return chains.toList();
    }

    boolean checkUrlExists(URL url){
        createClient();
        HttpGet httpGet = new HttpGet(url.toString());
        CloseableHttpResponse response = httpClient1.execute(httpGet);
        try {
            int statusCode = response.getStatusLine().getStatusCode()
            return statusCode == org.apache.http.HttpStatus.SC_OK
        } finally {
            JrrIoUtils.closeQuietly2(response, log)
            closeConnectionQuietly(httpGet)
        }
    }

    Header getHeader(URL url, String headerName) {
        createClient();
        HttpGet httpGet = new HttpGet(url.toString());
        CloseableHttpResponse response = httpClient1.execute(httpGet);
        try {
            assert response.getStatusLine().getStatusCode() == org.apache.http.HttpStatus.SC_OK: url
            Header[] headers = response.getAllHeaders()
            Header header = response.getFirstHeader(headerName)
            if (header == null) {
                throw new IllegalStateException("Header not found ${headerName}, headers : ${headers.toList().collect { it.getName() }} ${url}")
            }
            return header
        } finally {
            JrrIoUtils.closeQuietly2(response, log)
            closeConnectionQuietly(httpGet)
        }
    }

    void closeConnectionQuietly(HttpRequestBase httpGet){
        if(httpGet!=null) {
            String s = httpGet.toString()
            try {
                httpGet.reset();
            } catch (Exception e) {
                log.info3(s,e)
            }
        }
    }


    Date getLastModified(URL url) {
        return lastModifiedDateFormat.parse(getHeader(url, HttpHeaders.LAST_MODIFIED).getValue())
    }

    @Override
    DelegatingSSLSocketFactory fetchSSLSocketFactory() {
        return sslSocketFactory
    }
}
