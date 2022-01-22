package net.sf.jremoterun.utilities.nonjdk.docker

import com.google.api.client.http.HttpRequest
import com.google.api.client.http.apache.v2.GuavaApacheHttpTransportJrr
import com.google.cloud.tools.jib.api.Credential
import com.google.cloud.tools.jib.api.DescriptorDigest
import com.google.cloud.tools.jib.api.JibEvent
import com.google.cloud.tools.jib.event.EventHandlers
import com.google.cloud.tools.jib.http.Authorization
import com.google.cloud.tools.jib.http.FailoverHttpClient
import com.google.cloud.tools.jib.http.FailoverHttpClientJrr
import com.google.cloud.tools.jib.http.Request
import com.google.cloud.tools.jib.http.Response
import com.google.cloud.tools.jib.image.json.BuildableManifestTemplate
import com.google.cloud.tools.jib.image.json.ManifestTemplate
import com.google.cloud.tools.jib.registry.AuthenticationMethodRetrieverJrr
import com.google.cloud.tools.jib.registry.ManifestAndDigest
import com.google.cloud.tools.jib.registry.RegistryAuthenticator
import com.google.cloud.tools.jib.registry.RegistryClient
import com.google.cloud.tools.jib.registry.RegistryEndpointProviderJrr
import com.google.cloud.tools.jib.registry.RegistryEndpointRequestPropertiesJrr
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLoggerSwitch
import net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.HttpRequestExecutorJrr
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaScriptHelper
import org.apache.http.HttpClientConnection
import org.apache.http.HttpException
import org.apache.http.HttpHost
import org.apache.http.HttpResponse
import org.apache.http.client.config.RequestConfig
import org.apache.http.conn.socket.LayeredConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.protocol.HttpContext
import org.apache.http.protocol.HttpProcessor
import org.apache.http.util.TextUtils

import javax.net.ssl.SSLSocket
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer
import java.util.function.Supplier
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class DockerMetadataFetcher {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Consumer<JibEvent> loggerConsumer =  new JibEventLogger();

    public HttpClientBuilder httpClientSslBuilder = GuavaApacheHttpTransportJrr.newDefaultHttpClientBuilder();
    public static String defaultDockerRegistry = 'registry-1.docker.io'

    public boolean doHttpLogging = true
    public boolean curlLogging = false
    public boolean enableRetry = false
    public FailoverHttpClient httpClient;
    public RegistryClient registryClient;
    RstaScriptHelper rstaScriptDockerHelper;

    void setStopper( RstaScriptHelper rstaScriptDockerHelper){
        this.rstaScriptDockerHelper = rstaScriptDockerHelper
        if(rstaScriptDockerHelper!=null){
            rstaScriptDockerHelper.runner.onStopRequestedRunnable = {
                closeConnectionIfCan();
            }
        }
    }

    static void setLogHttpGuava(boolean enable) {
        Level level
        if (enable) {
            // set LEVEL.ALL to log auth headers
            level = Level.CONFIG
        } else {
            level = Level.INFO
        }

            setLogHttpGuava(level)


    }

    static void setLogApacheHttp(boolean enable) {
        if (enable) {
            setLogApacheHttp(Level.CONFIG)
        } else {
            setLogApacheHttp(Level.INFO)
        }

    }

    /**
     * configure apache http client here
     */
    GuavaApacheHttpTransportJrr createSecureTransport(){
        return new GuavaApacheHttpTransportJrr(httpClientSslBuilder.build()){

            /**
             *
             * many logic coded in
             * @see com.google.api.client.http.HttpRequest
             */

            @Override
            HttpRequest buildRequest() {
                HttpRequest httpRequest = super.buildRequest()
                configureHttpRequest(httpRequest)
                return httpRequest;
            }

            @Override
            protected void configureApacheHttpRequest(RequestConfig.Builder requestConfig) {
                super.configureApacheHttpRequest(requestConfig)
                configureApacheHttpRequest2(requestConfig)
            }

            @Override
            void onException3(Exception e) {
                super.onException3(e)
                onException(e)
            }
        }
    }

    protected void configureApacheHttpRequest2(RequestConfig.Builder requestConfig) {

    }


    private static String[] split(final String s) {
        if (TextUtils.isBlank(s)) {
            return null;
        }
        return s.split(" *, *");
    }



    /**
     * used only when upload artifact
     */
    LayeredConnectionSocketFactory createLayeredConnectionSocketFactory(){
        return new SSLConnectionSocketFactory(
                (javax.net.ssl.SSLSocketFactory) javax.net.ssl.SSLSocketFactory.getDefault(),
                split(System.getProperty("https.protocols")),
                split(System.getProperty("https.cipherSuites")),
                SSLConnectionSocketFactory.getDefaultHostnameVerifier()){

            /**
             *  seless, setting will be overwritten in
             * @see org.apache.http.impl.conn.DefaultHttpClientConnectionOperator#connect
             */
            @Override
            Socket createSocket(HttpContext context) throws IOException {
                checkForStop()
                return super.createSocket(context)
            }

            @Override
            Socket createLayeredSocket(Socket socket, String target, int port, HttpContext context) throws IOException {
                checkForStop()
                configureTcpSocket(socket)
                Socket layeredSocket = super.createLayeredSocket(socket, target, port, context)
                return layeredSocket
            }

            @Override
            Socket connectSocket(int connectTimeout, Socket socket, HttpHost host, InetSocketAddress remoteAddress, InetSocketAddress localAddress, HttpContext context) throws IOException {
                checkForStop()
                SSLSocket sslSocket = super.connectSocket(connectTimeout, socket, host, remoteAddress, localAddress, context) as SSLSocket
                configureSslSocket(sslSocket)
                return sslSocket
            }



        };
    }

    void configureSslSocket(SSLSocket socket){
        log.info "ssl socket configure ${socket.getInetAddress()}"
    }

    void configureTcpSocket(Socket socket){

    }


    //org.apache.http.HttpRequest requestLatest;
    public volatile HttpClientConnection apacheHttpConnLatest;

    public volatile boolean requestFinished = false;

    void configureHttpClient() {
        assert httpClient!=null
        setEnableRetry(enableRetry)
//        httpClientBuilder.setRequestExecutor()
        // using "system socket factory" to enable sending client certificate
        // https://github.com/GoogleContainerTools/jib/issues/2585
        httpClientSslBuilder.setSSLSocketFactory(createLayeredConnectionSocketFactory());
        httpClientSslBuilder.setRequestExecutor(new HttpRequestExecutorJrr(){

            @Override
            void preProcess(org.apache.http.HttpRequest request, HttpProcessor processor, HttpContext context) throws HttpException, IOException {
                checkForStop();
                super.preProcess(request, processor, context)
            }

            @Override
            HttpResponse execute(org.apache.http.HttpRequest request, HttpClientConnection conn, HttpContext context) throws IOException, HttpException {
                preExecuteHttpRequest(request,conn,context)
                try {
                    HttpResponse response = super.execute(request, conn, context)
                    onHttpResponse(response)
                    return response
                }finally{
                    requestFinished = true
                }

            }
        });
        GuavaApacheHttpTransportJrr transport = createSecureTransport();
        Supplier supplier1 = { return transport }
        JrrClassUtils.setFieldValue(httpClient, 'secureHttpTransportFactory', supplier1)
    }

    void onHttpResponse(HttpResponse response) {
        requestFinished = true
    }

    void preExecuteHttpRequest(org.apache.http.HttpRequest request, HttpClientConnection conn, HttpContext context)  {
        apacheHttpConnLatest = conn
        requestFinished = false
        checkForStop();
    }


    void closeClientSilent(){
        try{
        httpClient.shutDown()
        }catch(Throwable e){
            log.log(Level.WARNING,'shutDown http client failed',e)
        }
    }

    void closeConnectionIfCan(){
        boolean finished = requestFinished
        HttpClientConnection latest = apacheHttpConnLatest
        if(latest==null){
            log.info "no latest http connection"
        }else{
            if(finished){
                log.info "request finished"
            }else{
                log.info "closing connection .."
                try {
                    latest.close()
                    log.info "connection closed"
                }catch(Throwable e){
                    log.log(Level.WARNING,'failed close connection',e)
                }
            }
        }
    }

    void checkForStop(){
        if(rstaScriptDockerHelper!=null){
            rstaScriptDockerHelper.checkForStop()
        }
    }

    @Deprecated
    void configureHttpRequest(HttpRequest httpRequest) {
        httpRequest.setCurlLoggingEnabled(curlLogging)
        httpRequest.setLoggingEnabled(doHttpLogging)
    }



    static void setLogApacheHttp(java.util.logging.Level level) {
//        DefaultLevelConverter defaultLevelConverter = new DefaultLevelConverter();
//        org.apache.logging.log4j.Level ll = defaultLevelConverter.toLevel(level)
        setJdkLevel2('org.apache.http.wire', level);
        setJdkLevel2('org.apache.http.headers', level);
    }

    static void setLogHttpGuava(java.util.logging.Level level) {
//        DefaultLevelConverter defaultLevelConverter = new DefaultLevelConverter();
//        org.apache.logging.log4j.Level ll = defaultLevelConverter.toLevel(level)
        setJdkLevel(com.google.api.client.http.HttpTransport, level)
        setJdkLevel(com.google.api.client.http.HttpRequest, level)
        setJdkLevel(com.google.api.client.http.HttpResponse, level)

        if (Level.ALL == level || level.intValue() >= Level.CONFIG.intValue()) {
            assert Logger.getLogger(com.google.api.client.http.HttpResponse.getName()).isLoggable(Level.CONFIG)
        }
    }


    static void setJdkLevel2(String logger, java.util.logging.Level level) {
        Logger.getLogger(logger).setLevel(level);
    }

    static void setJdkLevel(Class cl, java.util.logging.Level level) {
        Logger.getLogger(cl.getName()).setLevel(level);
    }

    static void setLogHttpGuavaOld(java.util.logging.Level level) {
//        DefaultLevelConverter defaultLevelConverter = new DefaultLevelConverter();
//        org.apache.logging.log4j.Level ll = defaultLevelConverter.toLevel(level)
//        Log4j2Utils.setLogLevelLog4j2(com.google.api.client.http.HttpTransport, ll)
//        Log4j2Utils.setLogLevelLog4j2(com.google.api.client.http.HttpRequest, ll)
//        Log4j2Utils.setLogLevelLog4j2(com.google.api.client.http.HttpResponse, ll)

        Logger.getLogger(com.google.api.client.http.HttpTransport.getName()).setLevel(level);
        Logger.getLogger(com.google.api.client.http.HttpRequest.getName()).setLevel(level);
        Logger.getLogger(com.google.api.client.http.HttpResponse.getName()).setLevel(level);
        if (Level.ALL == level || level.intValue() >= Level.CONFIG.intValue()) {
            assert Logger.getLogger(com.google.api.client.http.HttpResponse.getName()).isLoggable(Level.CONFIG)
        }
    }



    void createHttpClient(){
        assert httpClient==null
        httpClient = new FailoverHttpClientJrr(true,false,(Consumer)loggerConsumer,enableRetry){
            @Override
            Response call(String httpMethod, URL url, Request request) throws IOException {
                checkForStop()
                try {
                    return super.call(httpMethod, url, request)
                }catch(Exception e){
                    onException(e)
                }
            }
        };
        configureHttpClient()
    }

    void onException(Exception e){
        throw e
    }


    RegistryClient createRegistryClient(String serverUrl, String repoName,String username,String password){
        if(httpClient==null){
            createHttpClient()
        }
        EventHandlers eventHandlers = EventHandlers.builder().add(JibEvent,(Consumer)loggerConsumer).build();
//        String serverUrl = net.sf.jremoterun.utilities.nonjdk.docker.DockerHelper.defaultDockerRegistry;
//        String imageName = 'library/alpine';
        RegistryClient.Factory registryClientF = RegistryClient.factory(eventHandlers,serverUrl,repoName,repoName,httpClient)
        if(password!=null) {
            registryClientF.setCredential(Credential.from(username, password))
        }
        registryClient = registryClientF.newRegistryClient()
        return registryClient
    }

    // https://docs.docker.com/registry/spec/api/#get-catalog
// https://docs.docker.com/registry/spec/api/#listing-repositories
    String listRepos(int n){
        RegistryEndpointRequestPropertiesJrr details = findConnectionDetails()
        String url = "https://${details.getServerUrl()}/v2/_catalog?n=${n}"
        byte[] result = doGenericGet(new URL( url))
        return new String(result);
    }

    // https://www.practical-devsecops.com/lesson-2-docker-images-docker-layers-and-registry/
    // https://docs.docker.com/registry/spec/api/#detail
    // https://docs.docker.com/registry/spec/api/#listing-image-tags
    String fetchTags(){
        // /v2/<name>/tags/list
        RegistryEndpointRequestPropertiesJrr details = findConnectionDetails()
        String url = "https://${details.getServerUrl()}/v2/${details.getSourceImageName()}/tags/list"
        // log.info "url = ${url}"
        byte[] result = doGenericGet(new URL( url))
        return new String(result);
    }

    void setHttp(boolean onn){
        JrrClassUtils.setFieldValue(httpClient,'enableHttpAndInsecureFailover',onn)
    }

    void setEnableRetry(boolean onn){
        JrrClassUtils.setFieldValue(httpClient,'enableRetry',onn)
    }

    // /v2/<name>/blobs/<digest>
    void saveBlobToFile(String sha,File f){
        RegistryEndpointRequestPropertiesJrr details = findConnectionDetails()
        String url = "https://${details.getServerUrl()}/v2/${details.getSourceImageName()}/blobs/${sha}"
        log.info "calling ${url}"
        BufferedOutputStream stream = f.newOutputStream()
        try {
            RegistryEndpointProviderJrr registryEndpointProviderJrr = new RegistryEndpointProviderJrr(new URL(url), stream);
            callRegistryEndpoint(registryEndpointProviderJrr)
            stream.flush()
        }finally {
            JrrIoUtils.closeQuietly2(stream,log)
        }
    }

    Object callRegistryEndpoint(Object registryEndpointProviderJrr){
        return JrrClassUtils.invokeJavaMethod(registryClient,'callRegistryEndpoint',registryEndpointProviderJrr)
    }

    String fetchManifest2(String suffix){
        // /v2/<name>/tags/list
        RegistryEndpointRequestPropertiesJrr details = findConnectionDetails()
        String url = "https://${details.getServerUrl()}/v2/${details.getSourceImageName()}/manifests/${suffix}"
        // log.info "url = ${url}"
        byte[] result = doGenericGet(new URL( url))
        return new String(result);
    }

    byte[] doGenericGetSuffix(String urlSuffix){
        RegistryEndpointRequestPropertiesJrr details = findConnectionDetails()
        String url = "https://${details.getServerUrl()}/v2/${urlSuffix.trim()}"
        // log.info "url = ${url}"
        byte[] result = doGenericGet(new URL( url))
        return result;
    }

    byte[] doGenericGet(URL url){
        log.info "calling ${url}"
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        RegistryEndpointProviderJrr registryEndpointProviderJrr =new RegistryEndpointProviderJrr(url,byteArrayOutputStream);
        callRegistryEndpoint(registryEndpointProviderJrr)
        return byteArrayOutputStream.toByteArray();
    }

    @Deprecated
    void saveBlobsAll(File baseDir){
        saveBlobsAllImpl(baseDir,fetchManifest())
    }

    void saveBlobsAll3(File baseDir,String tag){
        saveBlobsAllImpl(baseDir,registryClient.pullManifest(tag))
    }

    void saveBlobsAllImpl(File baseDir, com.google.cloud.tools.jib.registry.ManifestAndDigest manifestAndDigest){
        baseDir.mkdir()
        assert baseDir.exists()
        com.google.cloud.tools.jib.image.json.BuildableManifestTemplate manifestv22 = manifestAndDigest.getManifest() as com.google.cloud.tools.jib.image.json.BuildableManifestTemplate
        manifestv22.getLayers().each {
            DescriptorDigest digest = it.getDigest()
            String digest2 = digest.toString()
            File f2 = new File(baseDir,digest.getHash()+'.tar.gz')
            saveBlobToFile(digest2,f2)
        }
        BuildableManifestTemplate.ContentDescriptorTemplate configuration = manifestv22.getContainerConfiguration()
        DescriptorDigest digest = configuration.getDigest()
        String digest2 = digest.toString()
        File f2 = new File(baseDir,digest.getHash()+'.json')
        saveBlobToFile(digest2,f2)

    }

    RegistryEndpointRequestPropertiesJrr findConnectionDetails(){
        assert registryClient!=null
        return new RegistryEndpointRequestPropertiesJrr(registryClient)

    }

    /**
     * @see com.google.cloud.tools.jib.registry.RegistryClient#doBearerAuth(boolean)
     */
    void doAuth2(String scope,Credential credential){
        AuthenticationMethodRetrieverJrr jrr = new AuthenticationMethodRetrieverJrr(registryClient, null, httpClient)
        Optional<RegistryAuthenticator> authOpt = callRegistryEndpoint(jrr) as Optional<RegistryAuthenticator>
        RegistryAuthenticator auttt = authOpt.get()
        Authorization authorization = JrrClassUtils.invokeJavaMethod(auttt,'authenticate',credential,scope) as Authorization
        AtomicReference atomicRef = JrrClassUtils.getFieldValue(registryClient,'authorization') as AtomicReference
        atomicRef.set(authorization)
    }


    void doAuth(){
        boolean auth = registryClient.doPullBearerAuth()
        assert auth
    }

    @Deprecated
    ManifestAndDigest<ManifestTemplate> fetchManifest(){
        ManifestAndDigest<ManifestTemplate> manifest = registryClient.pullManifest('latest')
        return manifest;
    }


}
