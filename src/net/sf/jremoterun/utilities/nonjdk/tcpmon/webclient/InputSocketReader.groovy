package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;

@CompileStatic
class InputSocketReader implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    final public InputStream in1;
    final HttpConnection httpConnection

    public long sleepTime=10
    public byte[] buffer1 = new byte[8096]
    public final Object lock1 = new Object()

    InputSocketReader(InputStream in1, HttpConnection httpConnection) {
        this.in1 = in1
        this.httpConnection = httpConnection
        buffer1 = new byte[httpConnection.webClient.inputStreamReadBytesCount]
    }

    @Override
    void run() {
        run1()
    }


    void run1() {
        try {
            while (!httpConnection. stopDownload) {
                httpConnection.readSeq++
                int readCOunt = in1.read(buffer1)
                if(readCOunt==-1){
                    log.info("other part seems disconnected, closing")
                    httpConnection.webClient.addMessageToLogViewerCurrentDate("other part seems disconnected, closing");
                    break
                }
                if (readCOunt > 0) {
                    int diplay1 = httpConnection.connectionRequest.bytesToDisplay(readCOunt, httpConnection)
                    httpConnection.totalBytesReceived+=readCOunt
                    assert diplay1<=readCOunt
                    String s = new String(buffer1, 0, diplay1);
                    if(diplay1!=readCOunt){
                        int diff = readCOunt-diplay1;
                        s+=".. ${diff} bytes"
                    }
                    log.info "new msg = ${s}"
                    httpConnection.webClient.addMessageToLogViewerCurrentDate(s)
                }
                if(!httpConnection.lastSocket.isConnected()){
                    throw new Exception("not connected")
                }
                if(!httpConnection.stopDownload) {
                    synchronized (lock1) {
                        //log.info "sleeping connected=${httpConnection.lastSocket.isConnected()} closed=${httpConnection.lastSocket.isClosed()} in=${httpConnection.lastSocket.isInputShutdown()} b=${httpConnection.lastSocket.isBound()}"
                        lock1.wait(sleepTime)
                    }
                }
            }
        } catch (Throwable e) {
            log.error(e);
            if(httpConnection.stopDownload){
                httpConnection.webClient.addMessageToLogViewerCurrentDate(e.toString());
            }else {
                httpConnection.webClient.addMessageToLogViewerCurrentDate('Error: ' + JrrUtils.exceptionRootToString(e));
            }
        } finally {
            log.info "input thread finished"
//            String date66 = new SimpleDateFormat('HH:mm:ss').format(new Date());
            httpConnection.webClient.addMessageToLogViewerCurrentDate("socket closed");
            httpConnection.close();
        }
    }


    void stopRequest(){
        httpConnection. stopDownload = true
        synchronized (lock1){
            lock1.notifyAll()
        }
    }
}
