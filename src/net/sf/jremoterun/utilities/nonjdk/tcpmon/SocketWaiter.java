package net.sf.jremoterun.utilities.nonjdk.tcpmon;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.ServerSocketI;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.ServerSocketWrapper;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.SocketI;
import net.sf.jremoterun.utilities.nonjdk.tcpmon.socket.SocketWrapper;

import javax.swing.*;
import java.awt.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * wait for incoming connections, spawn a connection thread when stuff comes
 * in.
 */
@CompileStatic
public class SocketWaiter extends Thread {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ServerSocketI sSocket = null;

    public Listener listener;

    public int port;

    public JTextField requestEncoding;

    public JTextField responseEncoding;

    public boolean pleaseStop = false;

    public JComboBox styleIn;

    public JComboBox styleOut;

    public SocketWaiter(final Listener l, final int p,
                        final JTextField requestEncoding,
                        final JTextField responseEncoding, final JComboBox styleIn,
                        final JComboBox styleOut) {
        listener = l;
        port = p;
        start();
        this.requestEncoding = requestEncoding;
        this.responseEncoding = responseEncoding;
        this.styleIn = styleIn;
        this.styleOut = styleOut;
    }

    @Override
    public void run() {
        setName("Socket waiter " + port);
        try {
            listener.setLeft(new JLabel(Tcpmon.getMessage("wait00",
                    " Waiting for Connection...")));
            listener.headerView.panel.repaint();
            listener.requestView.panel.repaint();
            listener.responseView.panel.repaint();
            sSocket = listener.sslInput.createServerSocket(port);
//            sSocket = new ServerSocketWrapper( new ServerSocket(port));
            for (;;) {
                SocketI inSocket = sSocket.accept();

                if (pleaseStop) {
                    break;
                }
                final Connection connection = new Connection(listener,
                        inSocket, requestEncoding, responseEncoding,
                        styleIn, styleOut);
                connection.setName("Tcpmon port " + port);
                connection.setInputTextStyle(listener.inputDefaultStyle);
                connection.setOutputTextStyle(listener.outputDefaultStyle);
                //inSocket = null;
            }
        } catch (final Exception exp) {
            log.log(Level.WARNING,"failed for "+port,exp);
            if (!"socket closed".equals(exp.getMessage())) {
                final JLabel tmp = new JLabel(exp.toString());

                tmp.setForeground(Color.red);
                listener.setLeft(tmp);
                listener.setRight(new JLabel(""));
                listener.stop();
            }
        }
    }

    /**
     * force a halt by connecting to self and then closing the server socket
     */
    public void halt() {
        try {
            pleaseStop = true;
            // following needed ?
            new Socket("127.0.0.1", port);
            if (sSocket != null) {
                sSocket.close();
            }
        } catch (final Exception e) {
            log.log(Level.INFO,"", e);
        }
    }
}
