package net.sf.jremoterun.utilities.nonjdk.eclipse.proxy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ProxyTrackerI
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessRejected
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessStatus
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxyHostInfo
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySelectI
import net.sf.jremoterun.utilities.nonjdk.net.proxynew.ProxySetter2

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class EclipseProxyDataImpl implements EclipseProxyData {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static Level loggingLevelS = Level.INFO
    public Level loggingLevel = loggingLevelS

    ProxySelectI proxySelect
    ProxyTrackerI proxyTracker;
    String user
    String password

    ProxyHostInfo proxyHostInfo

    EclipseProxyDataImpl(ProxySelectI proxySelect, ProxyTrackerI proxyTracker, String user, String password,ProxyHostInfo proxyHostInfo) {
        this.proxySelect = proxySelect
        this.proxyTracker = proxyTracker
        this.user = user
        this.password = password
        this.proxyHostInfo = proxyHostInfo;
    }


    void logStatus(String uri, HostAccessStatus useProxy1) {
        log.log(loggingLevel, "connecting to ${uri} ${useProxy1}")
    }

    @Override
    String getProxyHost() {
        return proxyHostInfo.getProxyHost()
    }

    @Override
    int getProxyPort() {
        return proxyHostInfo.getProxyPort()
    }

    @Override
    boolean useProxyForHost(String host) {
        HostAccessStatus decision1 = proxySelect.decideBaseOnHostIsProxyNeeded(host)
        logStatus(host,decision1)
        switch (decision1) {
            case HostAccessStatus.noProxy:
                if (proxyTracker != null) {
                    proxyTracker.accessRequested(host, false)
                }
                return false
            case HostAccessStatus.proxy:
                if (proxyTracker != null) {
                    proxyTracker.accessRequested(host, true)
                }
                return true
            case HostAccessStatus.reject:
                if (proxyTracker != null) {
                    proxyTracker.accessRejected(host)
                }
                throw new HostAccessRejected(host);
            default:
                throw new IllegalStateException(host)

        }
        return false
    }

    @Override
    boolean useProxy(URI uri) {
        String host = getUrlHost(uri)
        if(host==null){
            throw new NullPointerException("host is null for ${uri}")
        }
        HostAccessStatus decision1 = proxySelect.decideBaseOnHostIsProxyNeeded(host)
        logStatus(uri.toString(),decision1)
        switch (decision1) {
            case HostAccessStatus.noProxy:
                if (proxyTracker != null) {
                    proxyTracker.accessRequested(uri, false)
                }
                return false
            case HostAccessStatus.proxy:
                if (proxyTracker != null) {
                    proxyTracker.accessRequested(uri, true)
                }
                return true
            case HostAccessStatus.reject:
                if (proxyTracker != null) {
                    proxyTracker.accessRejected(uri.toString())
                }
                throw new HostAccessRejected(uri.toString());
            default:
                throw new IllegalStateException(host)

        }

        return false
    }


    String getUrlHost(URI uri){
        String host = uri.getHost()
        return host
    }
}
