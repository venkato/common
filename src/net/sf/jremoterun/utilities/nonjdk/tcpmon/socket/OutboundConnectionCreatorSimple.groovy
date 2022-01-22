package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslConnectionInspect

@CompileStatic
class OutboundConnectionCreatorSimple implements OutboundConnectionCreator {

    public String initHost ;
    public int initPort;
    public boolean isSsl = false
    public SslConnectionInspect sslConnection=new SslConnectionInspect();

    @Override
    SocketI createConnection(String host, int port) {
        if(isSsl){
            sslConnection.prepare()
            sslConnection.check(host,port)
            return new SslSocketWrapper(sslConnection.socket)
//            return new SocketSslClientWrapper(host,port,sslConnection)
        }
        return new SocketWrapper(new Socket(host,port));
    }
}
