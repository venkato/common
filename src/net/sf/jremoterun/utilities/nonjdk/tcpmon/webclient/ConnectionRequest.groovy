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

    public int readUntrancatedBytesDisplay=8000
    public int readTrancatedBytesDisplay=50

    @Override
    String toString() {
        return "${host}:${port}"
    }

    int bytesToDisplay(int readCount, HttpConnection httpConnection){
        if(httpConnection.totalBytesReceived<readUntrancatedBytesDisplay){
            return  readCount
        }
        return Math.min(readCount,readTrancatedBytesDisplay)
    }

}
