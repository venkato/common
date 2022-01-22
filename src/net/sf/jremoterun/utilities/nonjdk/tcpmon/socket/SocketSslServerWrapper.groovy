package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslConnectionInspect
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslServerConnectionInspect

import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLSocket
import java.util.logging.Logger

@CompileStatic
class SocketSslServerWrapper implements ServerSocketI , AutoCloseable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ServerConnectionCreatorSimple connectionCreatorSimple;
    public SslServerConnectionInspect client;
    public int port;

    SocketSslServerWrapper(ServerConnectionCreatorSimple connectionCreatorSimple, SslServerConnectionInspect client, int port) {
        this.connectionCreatorSimple = connectionCreatorSimple
        this.client = client
        this.port = port
        client.createServerSocket(port)
    }

    @Override
    SocketWrapper accept() {
        return new SslSocketWrapper(client.socket.accept() as SSLSocket)
    }


    void close(){
        if(client.socket!=null) {
            client.socket.close()
        }
    }

}
