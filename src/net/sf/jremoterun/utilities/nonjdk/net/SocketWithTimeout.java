package net.sf.jremoterun.utilities.nonjdk.net;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.io.IOException;
import java.net.*;
import java.util.logging.Logger;

@CompileStatic
public class SocketWithTimeout extends Socket {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static ThreadLocal<Integer> customTimeout = new ThreadLocal<>();


    public SocketWithTimeout() {
    }

    public SocketWithTimeout(Proxy proxy) {
        super(proxy);
    }

    public SocketWithTimeout(SocketImpl impl) throws SocketException {
        super(impl);
    }

    public SocketWithTimeout(String host, int port) throws IOException {
        super(host, port);
    }

    public SocketWithTimeout(InetAddress address, int port) throws IOException {
        super(address, port);
    }

    public SocketWithTimeout(String host, int port, InetAddress localAddr, int localPort) throws IOException {
        super(host, port, localAddr, localPort);
    }

    public SocketWithTimeout(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException {
        super(address, port, localAddr, localPort);
    }

    @Override
    public void connect(SocketAddress endpoint, int timeout) throws IOException {
        Integer integer1 = customTimeout.get();
        if(integer1!=null){
            timeout = integer1;
        }
        super.connect(endpoint, timeout);
    }
}
