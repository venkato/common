package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.improved;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.ClientConnectionRequest;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ManagedClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.scheme.SchemeRegistry;

import java.util.concurrent.TimeUnit;

public class JrrHttpClientConnectionManager implements ClientConnectionManager {

    public HttpClientConnectionManager connManager;

    public JrrHttpClientConnectionManager(HttpClientConnectionManager connManager) {
        this.connManager = connManager;
    }

    @Override
    public void shutdown() {
        connManager.shutdown();
    }

    @Override
    public ClientConnectionRequest requestConnection(
            final HttpRoute route, final Object state) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void releaseConnection(
            final ManagedClientConnection conn,
            final long validDuration, final TimeUnit timeUnit) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SchemeRegistry getSchemeRegistry() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void closeIdleConnections(final long idletime, final TimeUnit timeUnit) {
        connManager.closeIdleConnections(idletime, timeUnit);
    }

    @Override
    public void closeExpiredConnections() {
        connManager.closeExpiredConnections();
    }

}
