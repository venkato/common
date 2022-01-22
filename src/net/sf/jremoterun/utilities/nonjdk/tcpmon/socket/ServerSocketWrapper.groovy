package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ServerSocketWrapper implements ServerSocketI , AutoCloseable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ServerSocket serverSocket;

    ServerSocketWrapper(ServerSocket serverSocket) {
        this.serverSocket = serverSocket
    }

    SocketI accept() throws IOException {
        return new SocketWrapper(serverSocket.accept());
    }

    @Override
    void close() throws Exception {
        serverSocket.close()
    }
}
