package timmoson.client;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.OutboundConnectionCreator;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.OutboundConnectionCreatorSimple;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.SocketI;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.SocketWrapper;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;
import timmoson.server.ServiceInfo;
import timmoson.server.service.TestService;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TimmosonSessionStoreAndBuilder implements TimmosonSessionStore {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Object connectionLock = new Object();
    public ServiceInfo serviceInfo;
    public String tcpHost;
    public int tcpPort;

    public ArrayList<TcpSessionClosedListener> sessionClosedListeners = new ArrayList<TcpSessionClosedListener>();

    public OutboundConnectionCreatorSimple outboundConnectionCreator = new OutboundConnectionCreatorSimple();

    private TestService testService = ClientSendRequest.makeProxyForService(
            TestService.class, this);

    public TimmosonSessionStoreAndBuilder(String tcpHost, int tcpPort) {
        this.tcpPort = tcpPort;
        this.tcpHost = tcpHost;
    }

    @Override
    public TcpSession getTcpSession() throws Exception {
        synchronized (connectionLock) {
//			log.info(clienTcpSession);
            if (clienTcpSession == null || clienTcpSession.isClosed()) {
                initTcpConnection();
            }
        }
        log.fine(""+clienTcpSession.isClosed());
        log.fine(""+clienTcpSession.socket.isClosed());
        return clienTcpSession;
    }

    public TcpSession clienTcpSession;

    protected TcpSession buildTcpSession(SocketI socket) throws Exception {
        return TcpSession.buildTcpSession(socket);
    }


    public void initTcpConnection() throws Exception {
        SocketI socket;
        log.fine("try create new connection to " + tcpHost + ":" + tcpPort);
        socket = outboundConnectionCreator.createConnection(tcpHost, tcpPort);
        clienTcpSession = buildTcpSession(socket);
        clienTcpSession.sessionClosedListeners.addAll(sessionClosedListeners);
        clienTcpSession.sessionBuilder = this;
        // clienTcpSession.sessionClosedListeners
        // .add(SessionCloseListenClient.sessionCloseListenClient);
        RemoteService.defaultRemoteService.handleSocketNewThread(clienTcpSession);
    }

    @Override
    public TestService getTestService() {
        return testService;
    }

}
