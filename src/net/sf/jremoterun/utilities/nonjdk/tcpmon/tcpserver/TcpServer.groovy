package net.sf.jremoterun.utilities.nonjdk.tcpmon.tcpserver;


import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils;
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea;
import net.sf.jremoterun.utilities.nonjdk.swing.NameAndTextField;

import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
public class TcpServer extends JPanel {

    //private static final Logger log = LogManager.getLogger();
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JPanel mainPanel = this;

    public SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    private NameAndTextField portTextField = new NameAndTextField("Port", "800");


    public NameAndTextField encoding = new NameAndTextField("Encoding",
            Charset.defaultCharset().name());

    public volatile ServerSocket serverSocket;

    public ArrayList<SocketHandler> sockets = new ArrayList();

    public MyTextArea textArea = new MyTextArea();

    private JButton clearButton = new JButton("Clear");

    private JButton closeSocketButton = new JButton("Close socket");

    private JButton restartButton = new JButton("Restart");

    private RTextScrollPane scrollPane = MyTextArea.buildRTextScrollPane(textArea);

    private Object lock = new Object();

    public TcpServer(int port) throws IOException {
        super(new BorderLayout());
        serverSocket = new ServerSocket(port);
        textArea.append(sdf.format(new Date()) + " Listening " + port + "\n");
        textArea.setEditable(false);
        portTextField.setText(port + "");
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(portTextField);
        // portTextField.setMinimumSize(new Dimension());
        panel.add(encoding);
        panel.add(clearButton);
        panel.add(closeSocketButton);
        panel.add(restartButton);
        mainPanel.add(panel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
//        textArea.setSyntaxScheme(SyntaxConstants.)
        clearButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                clearText();
            }
        });
        restartButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    restart();
                } catch (Exception e1) {
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(null, e1);
                    log.log(Level.WARNING, "", e1);
                }
            }

        });
        closeSocketButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    closeCurrentSocket();
                } catch (IOException e1) {
                    log.log(Level.WARNING, "", e1);
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(null, e1);

                }

            }

        });
    }

    public void clearText() {
        textArea.setText(sdf.format(new Date()) + " Listening " + portTextField.getText() + " " + "\n");
    }

    public void restart() throws IOException {
        int port2 = new Integer(portTextField.getText());
        synchronized (lock) {
            if (serverSocket.getLocalPort() == port2) {
                closeCurrentSocket();
                clearText();
            } else {
                ServerSocket serverSocket2 = new ServerSocket(port2);
                JrrIoUtils.closeQuietly2(serverSocket, log);
                closeCurrentSocket();
                serverSocket = serverSocket2;
                clearText();
            }
        }
    }

    public void closeCurrentSocket() throws IOException {
        synchronized (lock) {
            for (SocketHandler socket : sockets) {
                socket.close()
            }
            sockets.clear()
        }
    }

    public void run2() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                Thread.currentThread().setName("Tcp server");
                try {
                    run3();
                } catch (Exception e) {
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(null, e);
                    log.log(Level.WARNING, "", e);
                }
            }

        });
        thread.start();
    }

//    public void run4(Socket socket2) throws Exception {
//        appendInSwingThreadWithCurrentDate("new client from " + socket2.getInetAddress() + "\n");
//        // log.info(new Date());
//        final InputStream in1 = socket2.getInputStream();
//        final byte[] bs = new byte[8000];
//        try {
//            while (true) {
//                final int readed = in1.read(bs);
//                if (readed == -1) {
//                    // log.info("a");
//                    break;
//                }
//                textArea.appendInSwingThread(new String(bs, 0, readed, encoding.getText()));
//            }
//        } catch (final java.net.SocketException e) {
//            log.info2(e);
////            System.out.println();
//            appendInSwingThreadWithCurrentDate("${e}\n");
//        } finally {
//            JrrIoUtils.closeQuietly2(in1, log);
//            JrrIoUtils.closeQuietly2(socket2, log);
//            appendInSwingThreadWithCurrentDate("Connection closed\n");
//        }
//    }

    void appendInSwingThreadWithCurrentDate(String msg) {
        textArea.appendInSwingThread(sdf.format(new Date()) + " " + msg);
    }

    public void run3() throws Exception {
        // log.info(new Date());
        while (true) {
            synchronized (lock) {
                appendInSwingThreadWithCurrentDate("Waiting next Request\n");
            }
            try {
                final Socket socket2 = serverSocket.accept();

                SocketHandler r = new SocketHandler(socket2,this)
                synchronized (lock) {
                    sockets.add(r)
                }
                Thread thread = new Thread(r, 'Tcp server ' + socket2.getInetAddress());
                r.thread = thread
                thread.start();
            } catch (SocketException e) {
                log.log(Level.INFO, "", e);
            }
        }
    }
}
