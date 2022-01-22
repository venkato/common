package net.sf.jremoterun.utilities.nonjdk.io


import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class StreamCopier extends Thread {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public byte[] buf = new byte[8192];
    public InputStream inn;
    public OutputStream out;
    public volatile boolean doRun = true
    public volatile boolean logException = true
    public volatile long copiedDone = 0
    public volatile int bytesInBuffer = 0

    public StreamCopier(InputStream inn, OutputStream out) {
        this.inn = inn;
        this.out = out;
    }

    void run() {
        try {
            while (doRun) {
                bytesInBuffer = inn.read(buf);
                if (bytesInBuffer == -1) {
                    break;
                }
                copyNext()
            }
        } catch (Exception e) {
            onException(e)
        }
    }

    void copyNext() {
        out.write(buf, 0, bytesInBuffer);
        copiedDone += (long) bytesInBuffer
    }

    void onException(Exception e) {
        if(logException) {
            log.info "failed copy, done=${copiedDone}, latest=${new String(buf, 0, bytesInBuffer)}"
            log.error("failed copy", e)
        }else{
            log.info("failed copy", e)
        }
    }

    static StreamCopier copyOutputStreamInNewThread(InputStream inn, OutputStream output) {
        StreamCopier thread = new StreamCopier(new BufferedInputStream(inn), output);
        thread.setName 'Copy stream'
        thread.setDaemon(true)
        thread.start();
        return thread;
    }

}