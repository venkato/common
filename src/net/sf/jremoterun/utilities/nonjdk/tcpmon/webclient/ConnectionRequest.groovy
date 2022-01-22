package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.net.ssl.SslConnectionInspect;

import java.util.logging.Logger;

@CompileStatic
class ConnectionRequest {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String host;
    public int port=-1;
    public Integer timeout;
    public boolean isSsl = false;
    public SslConnectionInspect sslConnectionInspect = new SslConnectionInspect()


    @Override
    String toString() {
        return "${host}:${port}"
    }

}
