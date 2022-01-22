package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.net.SocketWithTimeout

import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
public class HttpConnection{


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    // private String requestLine;
    //private final String msgToSend;

//    private final MyTextArea outputArea;
    public final WebClient webClient;
    public final Date creationDate = new Date();
//    private boolean stopDownload = false;
    public volatile InputSocketReader inputSocketReader;
    public volatile Thread inputThread
    public volatile OutputStream stream4
    public volatile PrintWriter out1
    public volatile int totalBytesReceived =0
    public volatile int readSeq=0;

    protected final ConnectionRequest connectionRequest;
    public volatile boolean stopDownload = false

    public volatile Socket lastSocket;

    @Override
    String toString() {
        return "${connectionRequest} ${creationDate.format('HH:mm:ss')}"
    }

    public HttpConnection(final ConnectionRequest host,
                          WebClient webClient) throws UnknownHostException, IOException {
        this.connectionRequest = host;
        this.webClient = webClient;

    }


    SimpleDateFormat sdf = new SimpleDateFormat('HH:mm:ss')

    void connect() throws UnknownHostException, IOException {
        webClient.addMessageToLogViewerCurrentDate("Connecting ${connectionRequest} ..")
        if(connectionRequest.isSsl){
            connectionRequest.sslConnectionInspect.prepare()
            connectionRequest.sslConnectionInspect.check(connectionRequest.host.trim(), connectionRequest.port)
            SSLSocket sslSocket = connectionRequest.sslConnectionInspect.socket
            lastSocket = sslSocket
            SSLSession session1 = sslSocket.getSession()
            webClient.addMessageToLogViewerCurrentDate( session1.getProtocol() + '  ' + session1.getCipherSuite() + '  ' + sslSocket.getClass().getName() )
        }else {
            if(connectionRequest.timeout!=null){
                SocketWithTimeout.customTimeout.set(connectionRequest.timeout)
            }
            lastSocket = new SocketWithTimeout(connectionRequest.host.trim(), connectionRequest.port) {
                @Override
                public void connect(SocketAddress endpoint, int timeout) throws IOException {
                    lastSocket = this;
                    super.connect(endpoint, timeout);
                }
            };
        }
        if(stopDownload){
            webClient.addMessageToLogViewerCurrentDate("closing connection")
            close()
        }else {
            webClient.addMessageToLogViewerCurrentDate("Connected")
            createStreams()
            handleConnection();
        }
    }


    void createStreams() {
        stream4 = lastSocket.getOutputStream()
        OutputStreamWriter writer4 = new OutputStreamWriter(stream4, charset1);
        out1 = new PrintWriter(writer4);
        final InputStream inn = lastSocket.getInputStream();
        inputSocketReader = new InputSocketReader(inn, this)

    }

    Charset charset1 = Charset.defaultCharset()


    protected void handleConnection() throws IOException {
        try {
            inputThread = new Thread(inputSocketReader, 'Input socketReader')
            inputThread.start()
//            String date66 = new SimpleDateFormat('HH:mm:ss').format(new Date());
//            webClient.logsView.getTextArea().appendInSwingThread("Connected ${date66} ..\n");
        } catch (final Exception e) {
            webClient.addMessageToLogViewerCurrentDate('Error: ' + e);
            close()
        }
    }


    void sendMsg(String s) {
        out1.write(s)
        out1.flush()
        webClient.addMessageToLogViewerCurrentDate("msg sent\n")
    }

    void close() {
        stopDownload = true
        try {
            lastSocket.shutdownOutput()
            lastSocket.shutdownInput()
        }catch (Exception e){
            log.fine('',e)
        }
        InputSocketReader inputSocketReader1 = inputSocketReader
        if (inputSocketReader1 != null) {
            inputSocketReader1.stopRequest()
            JrrIoUtils.closeQuietly2(inputSocketReader1.in1, log)
            Thread inputThread1 = inputThread
            if (inputThread1 != null && inputThread1!=Thread.currentThread()) {
                inputThread1.join()
                inputThread = null
                inputSocketReader = null
            }
        }
        if (stream4 != null) {
            JrrIoUtils.closeQuietly2(stream4, log)
            stream4 = null
        }
        if (lastSocket != null) {
            JrrIoUtils.closeQuietly2(lastSocket, log)
            lastSocket = null
        }
        webClient.removeConnection(this)
    }

}
