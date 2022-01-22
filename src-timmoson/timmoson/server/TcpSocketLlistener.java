package timmoson.server;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.ServerConnectionCreator;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.ServerConnectionCreatorSimple;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.ServerSocketI;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.SocketI;

import timmoson.client.TimmosonSessionStoreSimple;
import timmoson.common.sertcp.RemoteService;
import timmoson.common.sertcp.TcpSession;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpSocketLlistener {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

	// public static TcpSocketLlistener defaultTcpSocketLlistener = new
	// TcpSocketLlistener();

	public ServerSocketI serverSocket;

	public static WeakHashMap<TcpSocketLlistener, Object> tcpSocketLlisteners = new WeakHashMap();
	public WeakHashMap<TcpSession, Object> tcpSessions = new WeakHashMap();

	// public GetReponseHandler reponseHandler=new GetReponseHandler();

	public TcpSocketLlistener(ServerConnectionCreatorSimple serverConnectionCreator,int port) throws IOException {
		this(serverConnectionCreator.createServerSocket(port));
	}

	public TcpSocketLlistener(ServerSocketI serverSocket) {
		this.serverSocket = serverSocket;
		tcpSocketLlisteners.put(this, object);
	}

	public static final Object object = new Object();

	private volatile boolean stop = false;

	public ServerConnectionCreator serverConnectionCreator = new ServerConnectionCreatorSimple();

	public void startListener() throws IOException {
		while (true) {
			if (stop) {
				log.info("stop requested");
				break;
			}
			final SocketI socket;
			try {
				socket = serverSocket.accept();
			} catch(SocketException e) {
				if(stop) {
					log.info("stop requested "+e);
					break;
				}
				throw e;
			}
			log.fine("new client" + socket);
			TcpSession tcpSession = buildTcpSession(socket);
			tcpSessions.put(tcpSession, object);
			if (tcpSession == null) {
				log.warning("tcp session is null " + socket);
				continue;
			}
			// if (newSessionBuilderServer == null) {
			tcpSession.sessionBuilder = new TimmosonSessionStoreSimple(tcpSession);
			// } else {
			// tcpSession.sessionBuilder = newSessionBuilderServer;
			// }
			RemoteService.defaultRemoteService.handleSocketNewThread(tcpSession);
		}
	}

	public void stop() throws Exception {
		if (!stop) {
			stop = true;
			serverSocket.close();
		}
	}

	public void handleSocketNewThread(TcpSession tcpSession) {
		RemoteService.defaultRemoteService.handleSocketNewThread(tcpSession);
	}

	public Thread startListenerInNewThread() throws IOException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					startListener();
				} catch (IOException e) {
					log.log(Level.WARNING,null, e);
				}

			};
		};
		thread.start();
		return thread;
	}

	public TcpSession buildTcpSession(SocketI socket) throws IOException {
		TcpSession tcpSession = TcpSession.buildTcpSession(socket);
		return tcpSession;
	}
}
