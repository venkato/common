package net.sf.jremoterun.utilities.nonjdk.tcpmon.tcpserver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils;

import java.util.logging.Logger;

@CompileStatic
class SocketHandler implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public final Socket socket2;
    public final TcpServer tcpServer;
    public volatile InputStream in1;
    public Thread thread;

    SocketHandler(Socket socket2, TcpServer tcpServer) {
        this.socket2 = socket2
        this.tcpServer = tcpServer
    }

    @Override
    void run() {
        run4()
    }

    void close(){
        try {
            socket2.shutdownOutput()
            socket2.shutdownInput()
        }catch (Exception e){
            log.warn("",e)
        }
        JrrIoUtils.closeQuietly2(in1,log)
        JrrIoUtils.closeQuietly2(socket2,log)
    }

    public void run4() throws Exception {
        tcpServer. appendInSwingThreadWithCurrentDate("new client from " + socket2.getInetAddress() + "\n");
        // log.info(new Date());
        in1 = socket2.getInputStream();
        final byte[] bs = new byte[8000];
        try {
            while (true) {
                final int readed = in1.read(bs);
                if (readed == -1) {
                    // log.info("a");
                    break;
                }
                String s = new String(bs, 0, readed, tcpServer.encoding.getText())
                tcpServer.textArea.appendInSwingThread(s);
            }
        } catch (final java.net.SocketException e) {
            log.info2(e);
//            System.out.println();
            tcpServer.appendInSwingThreadWithCurrentDate("${e}\n");
        } finally {
            JrrIoUtils.closeQuietly2(in1, log);
            JrrIoUtils.closeQuietly2(socket2, log);
            tcpServer.appendInSwingThreadWithCurrentDate("Connection closed\n");
        }
    }
}
