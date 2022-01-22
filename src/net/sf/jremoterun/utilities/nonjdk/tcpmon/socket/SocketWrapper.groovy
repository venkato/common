package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslConnectionInfo

import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket;
import java.util.logging.Logger;

@CompileStatic
class SocketWrapper implements SocketI, AutoCloseable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Socket socket;

    SocketWrapper(Socket socket) {
        this.socket = socket
    }

    @Override
    String dumpSocketDebugInfo() {
        return ''
    }

    @Override
    void shutdownInput() {
        socket.shutdownInput()
    }

    @Override
    void shutdownOutput() {
        socket.shutdownOutput()
    }

    @Override
    void close() {
        socket.close()
    }

    @Override
    InetAddress getInetAddress() {
        return socket.getInetAddress()
    }

    @Override
    InputStream getInputStream() {
        return socket.getInputStream()
    }

    @Override
    OutputStream getOutputStream() {
        return socket.getOutputStream()
    }

    @Override
    boolean isClosed() {
        return socket.isClosed()
    }

    @Override
    SocketAddress getRemoteSocketAddress() {
        return socket.getRemoteSocketAddress();
    }

}
