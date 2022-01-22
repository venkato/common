package net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient;

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow;
import net.infonode.docking.TabWindow
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils;
import net.sf.jremoterun.utilities.nonjdk.idwutils.MyDockingWindowTitleProvider;
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaRunnerWithStackTrace2;
import net.sf.jremoterun.utilities.nonjdk.swing.MyTextArea

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener
import java.util.logging.Level;
import java.util.logging.Logger;


@CompileStatic
public class WebClient extends RstaRunnerWithStackTrace2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public HttpConnection httpClient;
    public final TextAreaAndView requestHeadersArea = new TextAreaAndView("Output message", new MyTextArea(true));
    public final SplitWindow splitWindowMain;
    private final Button closeButton = new Button("Close");

    private final Object lock1 = new Object();
    public int inputStreamReadBytesCount = 8196
    public static String initText = """GET / HTTP/1.1
${net.sf.jremoterun.utilities.nonjdk.tcpmon.webclient.HttpClientConst.payloadSep}
"""



    public WebClient(File file) {
        super(file);
        continueCollectLogsAfterThreadFinished = true
        splitWindowMain = new SplitWindow(false,0.2f,requestHeadersArea.view, mainPanel4);
        splitWindowMain.getWindowProperties().setTitleProvider(new MyDockingWindowTitleProvider("Tcp client"));
        final int fontSize = 14;
        final Font labelFont = new Font("Serif", Font.BOLD, fontSize);
        getPanelButtons().add(cloneButton);
        getPanelButtons().add(closeButton);
        requestHeadersArea.textArea.setText(initText);
        cloneButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                WebClient webClient = new WebClient(file);
                webClient.requestHeadersArea.textArea.setText(requestHeadersArea.textArea
                        .getText());
                TabWindow parentIdwWindowSpecial = IdwUtils.getParentIdwWindowSpecial(splitWindowMain, TabWindow);
                if(parentIdwWindowSpecial==null){
                    throw new Exception("failed find TabWindow")
                }
                parentIdwWindowSpecial.addTab(webClient.splitWindowMain);
            }
        });
        closeButton.addActionListener {
            closeActiveConnection()
        };
        closeButton.setFont(labelFont);
    }

@Override
    RstaRunnerWithStackTrace2 createClonePanelBasic(){
        return new WebClient(fileWithConfig)
    }

    void closeActiveConnection(){
        HttpConnection httpClient1 = httpClient
        if (httpClient1 != null) {
            log.info "closing connection ${httpClient1}"
            httpClient1.close();
        }
    }


    void createNewConnection(ConnectionRequest connectionRequest) throws IOException {
        closeActiveConnection()
        HttpConnection httpClient1 = new HttpConnection(connectionRequest, this);
        synchronized (lock1) {
            if (httpClient != null) {
                log.error("http client was not closed, closing ..")
                httpClient.close()
            }
            httpClient = httpClient1
        }
        try {
            httpClient.connect();
        } catch (Exception e) {
            log.log(Level.INFO, "failed connect", e);
            final String s = JrrUtils.exceptionToString(JrrUtils.getRootException(e));
            addMessageToLogViewerCurrentDate(s);
            if (httpClient != null) {
                httpClient.close();
            }
        }
    }


    public java.util.List<String> getInputTextSep() {
        return requestHeadersArea.textArea.getText().readLines();
    }

    @Deprecated
    public String getInputText() {
        return requestHeadersArea.textArea.getText();
    }

    void removeConnection(HttpConnection httpClient1) {
        if (httpClient != null) {
            synchronized (lock1) {
                if (httpClient == httpClient1) {
                    httpClient = null
                }else{
                    log.info "not removing differnt connection"
                }
            }
        }
    }

}
