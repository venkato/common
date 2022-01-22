package net.sf.jremoterun.utilities.nonjdk.net.proxynew

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ProxyTrackerI
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessRejected
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessStatus
import net.sf.jremoterun.utilities.nonjdk.net.proxy.ProxyStaticMethods

import java.util.logging.Logger

@CompileStatic
class NoProxySelector extends ProxySelector implements ProxySelectI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ProxySelectGeneric proxySelectGeneric = new ProxySelectGeneric();

    public ProxyTrackerI proxyTracker;

    public boolean doChecks = true

    NoProxySelector() {
        proxySelectGeneric.defaultDecisionHost = HostAccessStatus.noProxy
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
        log.info "connecting to ${uri} ${useProxy}"
        if (proxyTracker != null) {
            proxyTracker.accessRequested(uri, useProxy)
        }
        assert !useProxy
        return ProxyStaticMethods.noProxy

    }

    void logStatus(URI uri, HostAccessStatus useProxy1) {
        log.info "connecting to ${uri} ${useProxy1}"
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
        return useProxy1;
    }

    Throwable onAccessReject(URI uri) {
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
