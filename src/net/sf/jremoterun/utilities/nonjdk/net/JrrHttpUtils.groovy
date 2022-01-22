package net.sf.jremoterun.utilities.nonjdk.net

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.langutils.JrrException
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.BasicCredentialsProviderJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.HttpRequestLogger
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.HttpResponseLogger
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.improved.HttpClientBuilderJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.improved.InternalHttpClientJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.NTLMSchemeFactoryJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ProxyAuthenticationStrategyJrr
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.ApacheHttpUrlTracker
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.win.WindowsCredentialsProvider
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.win.WindowsNTLMSchemeFactory
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxyHostInfo
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxyHostInfoImpl
import net.sf.jremoterun.utilities.nonjdk.net.ssl.DelegatingSSLSocketFactory
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SSLConnectionSocketFactoryJrr
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslAllTrustManager
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslChecksDisable
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslHostNameVerifierAllowAll
import net.sf.jremoterun.utilities.nonjdk.net.ssl.X509TrustManagerCertCollection
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.JsseProviders
import net.sf.jremoterun.utilities.nonjdk.net.ssl.enums.SslProtocolsEnum
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslSocketCatcher
import org.apache.http.Header
import org.apache.http.HttpClientConnection
import org.apache.http.HttpHeaders
import org.apache.http.HttpRequest
import org.apache.http.StatusLine
import org.apache.http.auth.AuthSchemeProvider
import org.apache.http.auth.AuthScope
import org.apache.http.auth.Credentials
import org.apache.http.auth.NTCredentials
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.client.AuthenticationStrategy
import org.apache.http.client.config.AuthSchemes
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpRequestBase
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.SystemDefaultRoutePlanner
import org.apache.http.protocol.HttpContext

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import java.security.cert.X509Certificate
import java.text.SimpleDateFormat
import java.util.logging.Level
import java.util.logging.Logger

/**
 * https://en.wikipedia.org/wiki/List_of_HTTP_header_fields
 * @see net.sf.jremoterun.utilities.nonjdk.maven.http.JrrMavenHttpUtils
 */
@CompileStatic
public class JrrHttpUtils implements SslSocketCatcher {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public HttpClientBuilder httpClientBuilder = new HttpClientBuilderJrr(this)
    //public HttpDebugAction httpDebugAction;
    public CloseableHttpClient httpClient1;
    public AuthSchemeProvider ntlmSchemeFactory = new NTLMSchemeFactoryJrr(this)
    public AuthenticationStrategy proxyAuthenticationStrategy = new ProxyAuthenticationStrategyJrr();
    public BasicCredentialsProvider credentialsProvider1 = new BasicCredentialsProviderJrr();
    public DelegatingSSLSocketFactory sslSocketFactory


    public boolean defaultConfigAlwaysUse = false

    public boolean logResponseCode = false
    public boolean logResponseException = true
//    public SslHostNameVerifierDelegate hostNameVerifierDelegate;
//    public SSLContext sslContext;

    // https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Last-Modified
    public SimpleDateFormat lastModifiedDateFormat = new SimpleDateFormat('EEE, dd MMM yyyy HH:mm:ss z')

    void createClient() {
        CloseableHttpClient httpClient2 = httpClientBuilder.build();
        InternalHttpClientJrr clientJrr = createInternalHttpClientJrr()
        clientJrr.copyPropsFromOtherClient(httpClient2)
        httpClient1 = clientJrr
    }

    InternalHttpClientJrr createInternalHttpClientJrr() {
        return new InternalHttpClientJrr(this)
    }

//    @Deprecated
//    void addLogger(boolean logAsWarning){
//        addRequestLogger(logAsWarning)
//    }


    void onUrl(String url, ApacheHttpUrlTracker type) {

    }

    void addRequestLogger(boolean logAsWarning) {
        assert httpClient1 == null
        HttpRequestLogger requestLogger = new HttpRequestLogger(this);
        if (logAsWarning) {
            requestLogger.level = Level.WARNING;
        }
        addLogger2(requestLogger)
    }

    void addResponseLogger(boolean logAsWarning) {
        HttpResponseLogger httpResponseLogger = new HttpResponseLogger(this)
        assert httpClient1 == null
        if (logAsWarning) {
            httpResponseLogger.level = Level.WARNING;
        }
        httpClientBuilder.addInterceptorFirst(httpResponseLogger)
    }


    void setUserPasswordForAny(UsernamePasswordCredentials usernamePasswordCredentials) {
        assert httpClient1 == null
        credentialsProvider1.setCredentials(AuthScope.ANY, usernamePasswordCredentials)
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
    }

    void addLogger2(HttpRequestLogger requestLogger) {
        assert httpClient1 == null
        httpClientBuilder.addInterceptorFirst(requestLogger)
    }

    // pass NTCredentials or org.apache.http.auth.UsernamePasswordCredentials
    void setCred(Credentials credential, ProxyHostInfo proxyHostInfo) {
        assert httpClient1 == null
        credentialsProvider1.setCredentials(new AuthScope(proxyHostInfo.proxyHost, proxyHostInfo.proxyPort), credential);
        InetAddress name = InetAddress.getByName(proxyHostInfo.proxyHost)
        String ipAddress = name.getHostAddress()
        if (ipAddress != proxyHostInfo.proxyHost) {
            credentialsProvider1.setCredentials(new AuthScope(ipAddress, proxyHostInfo.proxyPort), credential);
        }
    }

    NTCredentials createNTCredentials(String user, String password, String domain) {
        return new NTCredentials(user, password, null, domain);
    }


    void addNtlmWinNativePrepare() {
        ntlmSchemeFactory = new WindowsNTLMSchemeFactory()
        WindowsCredentialsProvider windowsCredentialsProvider = new WindowsCredentialsProvider(new net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.BasicCredentialsProviderJrr())
        httpClientBuilder.setDefaultCredentialsProvider(windowsCredentialsProvider)
    }

    void addNtlmWinNative() {
        addNtlmWinNativePrepare()
        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
    }

    void addNtlmWinNativeAll() {
        addNtlmWinNativePrepare()
        addNtlmWinNative()
    }

    /**
     * Set ProxySelector.getDefault() before !!
     * call setRouterPlanner() after
     */
    @Deprecated
    void addProxyCredentialGeneric(Credentials credential, String proxyHost, int proxyport) {
        addProxyCredentialGeneric(credential, new ProxyHostInfoImpl(proxyHost, proxyport))
    }

    void addProxyCredentialGeneric(Credentials credential, ProxyHostInfo proxyHostInfo) {
        setCred(credential, proxyHostInfo)
        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider1)
        httpClientBuilder.setProxyAuthenticationStrategy(proxyAuthenticationStrategy)
    }

    /**
     * Set ProxySelector.getDefault() before !!
     */
    void addProxyNtlmAuth3(NTCredentials credentials, ProxyHostInfo proxyHostInfo) {
        addProxyCredentialGeneric(credentials, proxyHostInfo)
        Registry<AuthSchemeProvider> authSchemeProviderRegistry = RegistryBuilder.<AuthSchemeProvider> create().register(AuthSchemes.NTLM, ntlmSchemeFactory).build();
        httpClientBuilder.setDefaultAuthSchemeRegistry(authSchemeProviderRegistry)
        //setRouterPlanner()
    }

    @Deprecated
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
        initSslParams(sSLSocketFactory2, hostNameVerifierAllowAll)
    }

    void sslCheckDisable() {
        SslHostNameVerifierAllowAll hostNameVerifierAllowAll = new SslHostNameVerifierAllowAll()
        initSslParams(SslChecksDisable.createAllTrustSslContext().getSocketFactory(), hostNameVerifierAllowAll)
    }

    void initSslParams(SSLSocketFactory sslSocketFactory2, HostnameVerifier hostNameVerifierAllowAll) {
//        hostNameVerifierDelegate = new SslHostNameVerifierDelegate(hostNameVerifierAllowAll,this)
        httpClientBuilder.setSSLHostnameVerifier(hostNameVerifierAllowAll);
        if (sslSocketFactory == null) {
            sslSocketFactory = new DelegatingSSLSocketFactory(sslSocketFactory2);
        }

        SSLConnectionSocketFactoryJrr sslConnectionSocketFactory = new SSLConnectionSocketFactoryJrr(sslSocketFactory, hostNameVerifierAllowAll);
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

    URL httpsHostUrlCreator(String host) {
        String urll
        if (host.startsWith('https://')) {
            urll = host
        } else {
            urll = "https://${host}"
        }
        return new URL(urll)
    }

    List<X509Certificate> getCertificatesFromHost(String host) {
        X509TrustManagerCertCollection trustManager = new X509TrustManagerCertCollection()
        trustManager.acceptedIssuers1 = []
        SSLContext sSLContext = SslChecksDisable.sslContextCreator(SslProtocolsEnum.TLS, null, trustManager)
        initSslParams(sSLContext.getSocketFactory(), SSLConnectionSocketFactoryJrr.getDefaultHostnameVerifier())

        URL urll = httpsHostUrlCreator(host)
        HttpGet httpGet = new HttpGet(urll.toString());
        createClient();
        CloseableHttpResponse response = httpClient1.execute(httpGet);
        int statusCode = response.getStatusLine().getStatusCode()
        log.info("statusCode = " + statusCode)
        JrrIoUtils.closeQuietly2(response, log)
        closeConnectionQuietly(httpGet)
        if (trustManager.chains.size() == 0) {
            throw new JrrException("No certificates, response code ${statusCode}")
        }
        return trustManager.chains.toList();
    }


    boolean checkUrlExists(URL url) {
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
            StatusLine statusLine = response.getStatusLine()
            if(statusLine.getStatusCode() != org.apache.http.HttpStatus.SC_OK){
                onBadResponseCode(url,statusLine)
            }
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

    void closeConnectionQuietly(HttpRequestBase httpGet) {
        if (httpGet != null) {
            String s = httpGet.toString()
            try {
                httpGet.reset();
            } catch (Exception e) {
                log.info3(s, e)
            }
        }
    }

    byte[] getContent(URL url){
        createClient();
        HttpGet httpGet = new HttpGet(url.toString());
        CloseableHttpResponse response = httpClient1.execute(httpGet);
        try {
            StatusLine statusLine = response.getStatusLine()
            if(statusLine.getStatusCode() != org.apache.http.HttpStatus.SC_OK){
                onBadResponseCode(url,statusLine)
            }
            return response.getEntity().getContent().bytes
        } finally {
            JrrIoUtils.closeQuietly2(response, log)
            closeConnectionQuietly(httpGet)
        }
    }

    void onBadResponseCode(URL url,StatusLine statusLine){
        throw new FileNotFoundException("${statusLine} ${url}")
    }

    Date getLastModified(URL url) {
        return lastModifiedDateFormat.parse(getHeader(url, HttpHeaders.LAST_MODIFIED).getValue())
    }

    @Override
    DelegatingSSLSocketFactory fetchSSLSocketFactory() {
        return sslSocketFactory
    }

    void beforeSendRequest(HttpRequest httpRequest, HttpClientConnection httpClientConnection, HttpContext httpContext) {

    }

    void onResponse(CloseableHttpResponse response1) {
        if (logResponseCode) {
            StatusLine statusLine = response1.getStatusLine()
            log.info "${statusLine}"
            //final int status = response.getStatusLine().getStatusCode();
        }
    }

    void onException(Throwable e) {
        if (logResponseException) {
            log.log(Level.SEVERE, "failed on request ", e)
        }
    }

    public boolean resetConfig = false

    void reConfigure(RequestConfig config) {
        if (resetConfig) {
            targetPreferredAuthSchemes(config, null)
            proxyPreferredAuthSchemes(config, null)
            authenticationEnabled(config, true)
        }
    }

    static void authenticationEnabled(RequestConfig config, boolean authEn) {
        JrrClassUtils.setFieldValue(config, 'authenticationEnabled', authEn)
    }

    static void targetPreferredAuthSchemes(RequestConfig config, Collection<String> pref) {
        JrrClassUtils.setFieldValue(config, 'targetPreferredAuthSchemes', pref)
    }

    static void proxyPreferredAuthSchemes(RequestConfig config, Collection<String> pref) {
        JrrClassUtils.setFieldValue(config, 'proxyPreferredAuthSchemes', pref)
    }
}
