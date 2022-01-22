package net.sf.jremoterun.utilities.nonjdk.tcpmon.tcpclient

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea
import net.sf.jremoterun.utilities.nonjdk.swing.NameAndTextField
import org.fife.ui.rtextarea.RTextScrollPane

import javax.swing.JButton
import javax.swing.JPanel
import java.awt.BorderLayout
import java.awt.FlowLayout
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.nio.charset.Charset
import java.util.logging.Level;
import java.util.logging.Logger;

@Deprecated
@CompileStatic
class TcpClient  {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private NameAndTextField hostTextField = new NameAndTextField("Host", "127.0.0.1");
    private NameAndTextField portTextField = new NameAndTextField("Port", "800");


    private NameAndTextField encoding = new NameAndTextField("Encoding", Charset.defaultCharset().name());

    public Socket socket1;

    public OutputStream outputStream1;
//    ArrayList<Socket> sockets = new ArrayList();

    private TextAreaAndView textAreaRequest = new TextAreaAndView('Request',new MyTextArea());
    private TextAreaAndView textAreaResponse = new TextAreaAndView('Response',new MyTextArea());

    private JButton sendButton = new JButton("Send");

    private JButton closeSocketButton = new JButton("Close socket");

    private JButton restartAndSendButton = new JButton("Restart and send");


    SplitWindow splitWindow = new SplitWindow(true,textAreaRequest.view,textAreaResponse.view)

    private Object lock = new Object();

    public TcpClient(String host, int port) throws IOException {
        super();
//        textAreaResponse.append("Listening " + port + "\n");
        textAreaResponse.textArea.setEditable(false);
        hostTextField.setText(host);
        portTextField.setText(port + "");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(hostTextField);
        panel.add(portTextField);
        // portTextField.setMinimumSize(new Dimension());
        panel.add(encoding);
        panel.add(sendButton);
        panel.add(closeSocketButton);
        panel.add(restartAndSendButton);
        textAreaRequest.panel.add(panel, BorderLayout.NORTH);
//        textArea.setSyntaxScheme(SyntaxConstants.)
        sendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                sendText()
            }
        });
        restartAndSendButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    restartAndSend();
                } catch (Exception e1) {
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(null, e1);
                    log.warn("", e1);
                }
            }

        });
        closeSocketButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    closeCurrentSocket();
                } catch (IOException e1) {
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(null, e1);
                    log.warn(e1);
                }

            }

        });
    }

    public void clearText() {
        textAreaResponse.textArea.setText("");
    }

    public void restartAndSend() throws IOException {
        clearText();
        Runnable r = {
            try {
                restartAndSendImpl();
            } catch (Exception e) {
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(null, e);
                log.warn("", e);
            }
        }
        Thread thread = new Thread(r);
        thread.start();
    }

    public void closeCurrentSocket() throws IOException {
        JrrIoUtils.closeQuietly2(socket1, log)
        JrrIoUtils.closeQuietly2(outputStream1, log)
    }


    public void restartAndSendImpl() throws Exception {
        closeCurrentSocket()
        String host1 = hostTextField.getText().trim();
        int port1 = Integer.parseInt(portTextField.getText().trim());
        Thread.currentThread().setName("Tcp client ${host1}:${port1}")
        textAreaResponse.textArea.appendInSwingThread("connecting ${host1} ${port1} \n");
        Socket socket2 = new Socket(hostTextField.getText().trim(), port1);
        socket1 = socket2
        // log.info(new Date());
        final InputStream inn = socket2.getInputStream();
        textAreaResponse.textArea.appendInSwingThread("connected \n");
        Runnable r = {
            final byte[] bs = new byte[8000];
            try {
                while (true) {
                    final int readed = inn.read(bs);
                    if (readed == -1) {
                        // log.info("a");
                        break;
                    }
                    textAreaResponse.textArea.appendInSwingThread(new String(bs, 0, readed, encoding.getText()));
                }
            } catch (final java.net.SocketException e) {
//            System.out.println();
                log.log(Level.WARNING, null, e)
                textAreaResponse.textArea.appendInSwingThread("${e}\n");
            } finally {
                JrrIoUtils.closeQuietly2(inn, log)
                JrrIoUtils.closeQuietly2(outputStream1, log)
                JrrIoUtils.closeQuietly2(socket2, log)
                textAreaResponse.textArea.appendInSwingThread("\nConnection closed\n");
            }
        }
        Thread thread1 = new Thread(r, "tcp client ${host1}:${port1} inputstream")
        thread1.start()
        outputStream1 = socket2.getOutputStream()
        sendText()
    }

    void sendText() {
        String text = textAreaRequest.textArea.getText()
        if (text.length() > 0) {
            byte[] bytes
            bytes = text.getBytes(encoding.getText())
            outputStream1.write(bytes, 0, bytes.length)
        } else {
            log.info "no text"
        }

    }


}
