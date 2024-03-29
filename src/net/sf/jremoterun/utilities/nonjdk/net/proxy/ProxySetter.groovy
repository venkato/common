package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ProxyTrackerI

import java.util.logging.Logger

@CompileStatic
class ProxySetter extends ProxySelector {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<java.net.Proxy> noProxy = [Proxy.NO_PROXY];
    public static List<java.net.Proxy> defaultProxy;

    public static String nonProxyHosts = 'nonProxyHosts'

    public static String proxyHost = 'proxyHost'
    public static String proxyPort = 'proxyPort'
    public static String proxyUser = 'proxyUser'
    public static String proxyUserName = 'proxyUserName'
    public static String proxyPassword = 'proxyPassword'
    public static String proxySet = 'proxySet'
    public static List<String> prefix = ['http', 'https', 'ftp',]
    public ProxyTrackerI proxyTracker

    Set<String> noProxyConcreatHosts = new HashSet<>()
    Set<String> noProxyEndsWithHosts = new HashSet<>()

    static void followRedirect() {
        HttpURLConnection.setFollowRedirects(true)
    }

    void addConcreatHostAndIp(String host) {
        long time = System.currentTimeMillis()
        InetAddress address = InetAddress.getByName(host)
        String name = address.hostName
        String address1 = address.address
        long takeTime = System.currentTimeMillis() - time
        if (takeTime > 1000) {
            log.info "resolving ${host} took ${takeTime / 1000} sec"
        }
        noProxyConcreatHosts.add(name)
        noProxyConcreatHosts.add(address1)
    }


    void setProxySelector4(String host, int port, String user, String password) {
        setProxy(host, port)
        setAuth(user, password)
    }

    void setProxySelector3(String host, int port, String user, String password) {
        setProxySelector4(host, port, user, password)
        setProxySelector2()
    }

    public ProxySelector proxySelectorBefore;

    void setProxySelector2() {
        ProxySelector defaultSel = ProxySelector.getDefault()
        if (defaultSel == null) {
            setProxySelector3()
        } else {
            throw new Exception("Proxy selector already set : ${defaultSel.class.name} ${defaultSel}")
        }
    }

    void setProxySelector3() {
        ProxySelector defaultSel = ProxySelector.getDefault()
        List<String> joins = []
        joins.addAll noProxyConcreatHosts
        joins.addAll noProxyEndsWithHosts.collect { '*.' + it }
        setNoProxy(joins)
        ProxySelector.setDefault(this)
        proxySelectorBefore = defaultSel
    }

    @Override
    List<java.net.Proxy> select(URI uri) {
        return selectImpl(uri)
    }

    Throwable onAccessReject(URI uri){
        HostAccessRejected e = new HostAccessRejected("${uri}")
        log.error("rejecting access for ${uri}", e)
        return e
    }

    public boolean doChecks = true

    List<java.net.Proxy> selectImpl(URI uri) {
        if(doChecks) {
            String toString = uri.toString()
            if (toString.startsWith('https:/')) {
                if (!toString.startsWith('https://')) {
                    throw new RuntimeException("invalid url : ${toString}")
                }
            }
            if(toString.startsWith('http:/')){
                if(!toString.startsWith('http://')){
                    throw new RuntimeException("invalid url : ${toString}")
                }
            }

        }
        HostAccessStatus useProxy1 = selectImpl2(uri)
        if (useProxy1 == HostAccessStatus.reject) {
            throw onAccessReject(uri)
        }
        boolean useProxy = useProxy1 == HostAccessStatus.proxy
        log.info "connecting to ${uri} ${useProxy}"
        if (proxyTracker != null) {
            proxyTracker.accessRequested(uri, useProxy)
        }
        if (useProxy) {
            return defaultProxy
        }
        return noProxy

    }

    HostAccessStatus selectImpl2(URI uri) {
        String host = uri.getHost()
        HostAccessStatus useProxy1 = decideBaseOnHostIsProxyNeeded(host)
        if (useProxy1 == HostAccessStatus.reject) {
            return HostAccessStatus.reject
        }
        if(proxyTracker!=null){
            if(!proxyTracker.canAccess(uri)){
                return HostAccessStatus.reject
            }
        }

        if (defaultProxy == null) {
            return HostAccessStatus.noProxy
        }
        return useProxy1;
    }

    public HashSet<String> rejectHosts = new HashSet<>()

    HostAccessStatus decideBaseOnHostIsProxyNeeded(String host) {
        if (rejectHosts.contains(host)) {
            return HostAccessStatus.reject
        }
        if (noProxyConcreatHosts.contains(host)) {
            return HostAccessStatus.noProxy
        }
        String noProxyPrefix = noProxyEndsWithHosts.find { host.endsWith(it) }
        if (noProxyPrefix == null) {
            return HostAccessStatus.proxy
        } else {
            return HostAccessStatus.noProxy
        }
    }


    void setProxy(String host, int port) {
        setProperty(proxyHost, host)
        setProperty(proxyPort, "${port}")

        InetSocketAddress proxy = new InetSocketAddress(host, port)
        java.net.Proxy proxy2 = new Proxy(java.net.Proxy.Type.HTTP, proxy)
        defaultProxy = [proxy2]
        System.setProperty(proxySet, 'true')


    }

    static void setProxySelectorWithJustLogging() {
        ProxySelector defaultSel = ProxySelector.getDefault()
        if (defaultSel == null) {
            ProxySelector.setDefault(new ProxySetter())
        } else {
            log.info "Proxy selector already set : ${defaultSel.class.name} ${defaultSel}"
        }
    }


    static void setAuthImpl(String user, String password) {
        ProxyAuth auth = new ProxyAuth(user, password)
        Authenticator.setDefault(auth)
        setProperty(proxyUser, user)
        setProperty(proxyUserName, user)
        setProperty(proxyPassword, password)
    }

    static Authenticator getCurrentAuth() {
        Authenticator initialAuth = JrrClassUtils.getFieldValue(Authenticator, "theAuthenticator") as Authenticator
        return initialAuth
    }

    static void setAuth(String user, String password) {
        Authenticator initialAuth = getCurrentAuth()
        if (initialAuth == null) {
            setAuthImpl(user, password)
        } else {
            log.info "proxy auth alredy set ${initialAuth.getClass().getName()}"
        }

    }


    static void setProperty(String name, String value) {
        prefix.each {
            System.setProperty("${it}.${name}", value)
            System.setProperty("${it}.${name}", value)
        }
    }

    static void setNoProxy(List<String> noProxy) {
        setProperty(nonProxyHosts, noProxy.join('|'))
    }


    @Override
    void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        log.info "connection failed to ${uri}"
    }

}
