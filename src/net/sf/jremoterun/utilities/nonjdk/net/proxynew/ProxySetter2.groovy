package net.sf.jremoterun.utilities.nonjdk.net.proxynew

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ProxyTrackerI
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessRejected
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessStatus
import net.sf.jremoterun.utilities.nonjdk.net.proxy.ProxyStaticMethods

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class ProxySetter2 extends ProxySelector implements ProxySelectI, ProxyHostInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ProxySelectI proxySelectGeneric;


    public java.net.Proxy defaultProxy1;
    public List<java.net.Proxy> defaultProxy;
    public ProxyTrackerI proxyTracker;

    public boolean doChecks = true
    public ProxyHostInfo proxyHostInfo;

    public static Level loggingLevelS = Level.INFO
    public Level loggingLevel = loggingLevelS

    ProxySetter2(ProxySelectI proxySelectGeneric1, ProxyHostInfo  proxyHostInfo1) {
        proxyHostInfo = proxyHostInfo1
        InetSocketAddress proxy = new InetSocketAddress(proxyHostInfo.proxyHost, proxyHostInfo.proxyPort)
        defaultProxy1 = new Proxy(java.net.Proxy.Type.HTTP, proxy)
        defaultProxy = [defaultProxy1]
        proxySelectGeneric = proxySelectGeneric1
    }

    @Deprecated
    @Override
    String getProxyHost() {
        return proxyHostInfo.proxyHost
    }

    @Deprecated
    @Override
    int getProxyPort() {
        return proxyHostInfo.getProxyPort()
    }

    void setPropsAndObject() {
        ProxySelector.setDefault(this)
    }

    @Override
    List<java.net.Proxy> select(URI uri) {
        return selectImpl(uri)
    }

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
        logStatus(uri, useProxy1)
        if (useProxy1 == HostAccessStatus.reject) {
            throw onAccessReject(uri)
        }
        boolean useProxy = useProxy1 == HostAccessStatus.proxy
        if (proxyTracker != null) {
            proxyTracker.accessRequested(uri, useProxy)
        }
        if (useProxy) {
            return defaultProxy
        }
        return ProxyStaticMethods.noProxy
    }


    void logStatus(URI uri, HostAccessStatus useProxy1) {
        log.log(loggingLevel, "connecting to ${uri} ${useProxy1}")
    }

    String getUrlHost(URI uri){
        String host = uri.getHost()
        return host
    }

    HostAccessStatus selectImpl2(URI uri) {
        String host = getUrlHost(uri)
        HostAccessStatus useProxy1 = decideBaseOnHostIsProxyNeeded(host)
        if (useProxy1 == HostAccessStatus.reject) {
            return HostAccessStatus.reject
        }
        if(!proxySelectGeneric.acceptUrl(uri.toString())){
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

    Throwable onAccessReject(URI uri) {
        if(proxyTracker!=null){
            proxyTracker.accessRejected(''+uri)
        }
        HostAccessRejected e = new HostAccessRejected("${uri}")
        log.error("rejecting access for ${uri}", e)
        return e
    }


    @Override
    void connectFailed(URI uri, SocketAddress sa, IOException ioe) {
        log.info "connection failed to ${uri}"
    }

    @Override
    HostAccessStatus decideBaseOnHostIsProxyNeeded(String host) {
        return proxySelectGeneric.decideBaseOnHostIsProxyNeeded(host)
    }

    @Override
    boolean acceptUrl(String url) {
        return proxySelectGeneric.acceptUrl(url)
    }
}

