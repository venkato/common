package net.sf.jremoterun.utilities.nonjdk.net.proxynew

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ProxyTrackerI
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessRejected
import net.sf.jremoterun.utilities.nonjdk.net.proxy.HostAccessStatus
import org.apache.http.HttpException
import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.conn.routing.HttpRoute
import org.apache.http.conn.routing.HttpRoutePlanner
import org.apache.http.protocol.HttpContext

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class JrrHttpRoutePlanner2 implements HttpRoutePlanner {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ProxySelectI proxySelect2I
    public HttpHost httpHostproxy;
    public ProxyTrackerI proxyTracker;


    JrrHttpRoutePlanner2(ProxySelectI proxySelect2I, ProxyHostInfo proxyHostInfo) {
        this.proxySelect2I = proxySelect2I
        httpHostproxy = createApacheHttpHost(proxyHostInfo)
    }

    static HttpHost createApacheHttpHost(ProxyHostInfo proxyHostInfo){
        return new HttpHost(proxyHostInfo.getProxyHost(), proxyHostInfo.getProxyPort())
    }

    @Override
    HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        String hostName1 = target.getHostName()
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig config = clientContext.getRequestConfig();
        final InetAddress local = config.getLocalAddress();
        final boolean secure = target.getSchemeName().equalsIgnoreCase("https");
        String uri1 = target.toURI()
        HostAccessStatus decision = proxySelect2I.decideBaseOnHostIsProxyNeeded(hostName1)
        logDecision(uri1, decision)
        if (decision == HostAccessStatus.reject) {
            throw accessRejected(uri1)
        }
        if (!proxySelect2I.acceptUrl(uri1)) {
            throw accessRejected(uri1)
        }
        if (proxyTracker != null) {
            if (!proxyTracker.canAccess(new URI(uri1))) {
                throw accessRejected(uri1)
            }
        }
        if (decision == HostAccessStatus.noProxy) {
            if (proxyTracker != null) {
                proxyTracker.accessRequested(uri1, false)
            }
            return new HttpRoute(target, local, secure)
        }
        assert decision == HostAccessStatus.proxy
        if (proxyTracker != null) {
            proxyTracker.accessRequested(uri1, true)
        }
        return new HttpRoute(target, local, httpHostproxy, secure);
    }

    void logDecision(String uri1, HostAccessStatus decision) {
        log.log(loggingLevel, "${uri1} proxy=${decision}")
    }

    Throwable accessRejected(String urlOrHost) {
        return new HostAccessRejected(urlOrHost)
    }


    public static Level loggingLevelS = Level.INFO
    public Level loggingLevel = loggingLevelS

}
