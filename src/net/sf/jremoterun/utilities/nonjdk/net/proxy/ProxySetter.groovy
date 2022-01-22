package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ProxyTrackerI
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySelectI

import java.util.logging.Logger

@Deprecated
@CompileStatic
class ProxySetter extends ProxySelector implements ProxySelectI {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public static List<java.net.Proxy> noProxy = [Proxy.NO_PROXY];
    public static List<java.net.Proxy> defaultProxy;

    @Deprecated
    public static String nonProxyHosts = 'nonProxyHosts'

    @Deprecated
    public static String proxyHost = 'proxyHost'
    @Deprecated
    public static String proxyPort = 'proxyPort'
    @Deprecated
    public static String proxyUser = 'proxyUser'
    @Deprecated
    public static String proxyUserName = 'proxyUserName'
    @Deprecated
    public static String proxyPassword = 'proxyPassword'
    @Deprecated
    public static String proxySet = 'proxySet'

    public ProxyTrackerI proxyTracker

    Set<String> noProxyConcreatHosts = new HashSet<>()
    Set<String> noProxyEndsWithHosts = new HashSet<>()


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
        ProxyStaticMethods.setAuth(user, password)
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
        ProxyStaticMethods.setNoProxy(joins)
        ProxySelector.setDefault(this)
        proxySelectorBefore = defaultSel
    }

    @Override
    List<java.net.Proxy> select(URI uri) {
        return selectImpl(uri)
    }

    Throwable onAccessReject(URI uri) {
        HostAccessRejected e = new HostAccessRejected("${uri}")
        log.error("rejecting access for ${uri}", e)
        return e
    }

    public boolean doChecks = true

    List<java.net.Proxy> selectImpl(URI uri) {
        if (doChecks) {
            String toString = uri.toString()
            if (toString.startsWith('https:/')) {
                if (!toString.startsWith('https://')) {
                    throw new RuntimeException("invalid url : ${toString}")
                }
            }
            if (toString.startsWith('http:/')) {
                if (!toString.startsWith('http://')) {
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
        return ProxyStaticMethods.noProxy

    }

    HostAccessStatus selectImpl2(URI uri) {
        String host = uri.getHost()
        HostAccessStatus useProxy1 = decideBaseOnHostIsProxyNeeded(host)
        if (useProxy1 == HostAccessStatus.reject) {
            return HostAccessStatus.reject
        }
        if (proxyTracker != null) {
            if (!proxyTracker.canAccess(uri)) {
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

    @Override
    boolean acceptUrl(String url) {
        throw new UnsupportedOperationException(url)
    }

    void setProxy(String host, int port) {
        java.net.Proxy proxy2 = ProxyStaticMethods.setProxyS(host, port)
        defaultProxy = [proxy2]
    }


    static void setProxySelectorWithJustLogging() {
        ProxySelector defaultSel = ProxySelector.getDefault()
        if (defaultSel == null) {
            ProxySelector.setDefault(new ProxySetter())
        } else {
            log.info "Proxy selector already set : ${defaultSel.class.name} ${defaultSel}"
        }
    }

    @Override
    void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        log.info "connection failed to ${uri}"
    }

}
