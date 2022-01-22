package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.http.HttpException
import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.protocol.HttpClientContext
import org.apache.http.conn.routing.HttpRoute
import org.apache.http.conn.routing.HttpRoutePlanner
import org.apache.http.protocol.HttpContext;

import java.util.logging.Logger;

@CompileStatic
class JrrHttpRoutePlanner1 implements HttpRoutePlanner {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ProxySelector proxySelector

    JrrHttpRoutePlanner1(ProxySelector proxySelector) {
        this.proxySelector = proxySelector
    }

    @Override
    HttpRoute determineRoute(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        String toURI = target.toURI()
        final URI targetURI;
        targetURI = new URI(toURI);
        final HttpClientContext clientContext = HttpClientContext.adapt(context);
        final RequestConfig config = clientContext.getRequestConfig();
        final InetAddress local = config.getLocalAddress();
        final boolean secure = target.getSchemeName().equalsIgnoreCase("https");
        List<Proxy> listOfProxies = proxySelector.select(targetURI)
        if (listOfProxies == null || listOfProxies.size() == 0) {
            log.info "direct connection ${toURI}"
            return new HttpRoute(target, local, secure)
        }
        assert listOfProxies.size() == 1
        Proxy proxy1 = listOfProxies[0]
        InetSocketAddress address1 = proxy1.address() as InetSocketAddress
        String hostName = address1.getHostName()
        int port1 = address1.getPort()
        HttpHost httpHostproxy = new HttpHost(hostName, port1);
        log.info "use proxy ${toURI}"
        return new HttpRoute(target, local, httpHostproxy, secure);

    }
}
