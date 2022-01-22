package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface SocketI extends AutoCloseable{

    void shutdownInput()

    void shutdownOutput()


    String dumpSocketDebugInfo()

    InetAddress getInetAddress()


    InputStream getInputStream()


    OutputStream getOutputStream()


    boolean isClosed()

    SocketAddress getRemoteSocketAddress();

}
