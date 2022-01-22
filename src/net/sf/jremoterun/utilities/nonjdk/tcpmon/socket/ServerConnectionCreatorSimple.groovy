package net.sf.jremoterun.utilities.nonjdk.tcpmon.socket

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslServerConnectionInspect;

import java.util.logging.Logger;

@CompileStatic
class ServerConnectionCreatorSimple implements ServerConnectionCreator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public String commonName = '127.0.0.1';
    public boolean isSsl = false;
    public SslServerConnectionInspect serverConnectionInspect= new SslServerConnectionInspect();
    public int initPort


    @Override
    ServerSocketI createServerSocket(int port) {
        if(isSsl){
            //serverConnectionInspect = new SslServerConnectionInspect()
            return new SocketSslServerWrapper(this,serverConnectionInspect ,port)
        }
        return new ServerSocketWrapper(new ServerSocket(port));
    }
}
