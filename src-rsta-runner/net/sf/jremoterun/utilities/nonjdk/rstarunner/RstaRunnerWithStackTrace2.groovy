package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow
import net.infonode.docking.TabWindow
import net.infonode.docking.title.DockingWindowTitleProvider
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCountStatWithCount
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.MyDockingWindowTitleProvider
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.idwutils.ViewAndPanel
import net.sf.jremoterun.utilities.nonjdk.swing.JPanel4FlowLayout


import javax.swing.*
import java.awt.*
import java.text.SimpleDateFormat
import java.util.List
import java.util.logging.LogRecord
import java.util.logging.Logger

@CompileStatic
class RstaRunnerWithStackTrace2 extends RstaRunnerWithStackTrace3  implements StatusDelayListener{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public StackTraceTextArea textAreaStackTrace = new StackTraceTextArea()
    public JCheckBox showAllStackTraceCheckBox = new JCheckBox('Show all',false)
    public JCheckBox logsDateCheckBox = new JCheckBox(RstaLogsShowColumns.date.name(),true)
    public JCheckBox logsLocationFullCheckBox = new JCheckBox(RstaLogsShowColumns.locationFull.name(),false)
    //public List<JCheckBox> logsColumnCheckBox = []
    public JPanel logsUpperPanel = new JPanel4FlowLayout()

//    public ViewAndPanel runnerView = new ViewAndPanel("Runner", panel);
    public ViewAndPanel stackTraceView = new ViewAndPanel("Thread dump");
    public TextAreaAndView logsView = new TextAreaAndView("Logs  ");
    public TabWindow rightPanel
    public SplitWindow mainPanel4;

    public volatile StringBuffer logs = new StringBuffer()

    public JButton cloneButton = new JButton("Clone")

    public SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
    public volatile int modCount = 0
    public volatile int modDelayCount = 0
    public int maxOneLogMsgLength = 10000

    volatile int analizedModCount = 0
    volatile int analizedDelayModCount = 0
    public int totalLogSizeTruncated = 0;
    public int maxLogSizeTextLength = 20000;
    public int maxLogSizeTextNewLength = maxLogSizeTextLength/2 as int;

    RstaRunnerWithStackTrace2(File file) {
        super(file)
        init4()
    }

    RstaRunnerWithStackTrace2(String text2) {
        super(text2)
        init3()
        init4()
    }

    void doLoayout() {
        rightPanel = new TabWindow()
        rightPanel.addTab(stackTraceView.view)
        rightPanel.addTab(logsView.view)
        doLayoutCreateMainPanel()
    }

    void doLayoutCreateMainPanel() {
        mainPanel4 = new SplitWindow(true, 0.8f, runnerView.view, rightPanel)
    }


    protected void init4() {
        doLoayout()
        textAreaStackTrace.textArea.selectCursorToBegin = true
        logsView.textArea.setEditable(false)
        logsView.textArea.setLineWrap(true)
        cloneButton.addActionListener {
            onCloneActionListener()
        }
        panelButtons.add(cloneButton)
        logsUpperPanel.add(logsLocationFullCheckBox)
        logsUpperPanel.add(logsDateCheckBox)
        logsView.panel.add(logsUpperPanel, BorderLayout.NORTH)
        stackTraceView.panel.add(showAllStackTraceCheckBox, BorderLayout.NORTH)
        stackTraceView.panel.add(textAreaStackTrace.scrollPane, BorderLayout.CENTER)
    }

    @Override
    protected boolean isShowAllStackTraces() {
        return showAllStackTraceCheckBox.isSelected()
    }

    RstaRunnerWithStackTrace2 createClonePanelBasic(){
        return new RstaRunnerWithStackTrace2(fileWithConfig)
    }

    void onCloneActionListener(){
        TabWindow parentIdwWindowSpecial = IdwUtils.getParentIdwWindowSpecial(runnerView.view.getWindowParent(), TabWindow)
        RstaRunnerWithStackTrace2 newPanel = createClonePanelBasic()
        parentIdwWindowSpecial.addTab(newPanel.getMainPanel3())
        String titleBefore = ' titleBefore '
        DockingWindowTitleProvider titleProvider = getMainPanel3().getWindowProperties().getTitleProvider()
        if(titleProvider!=null){
            titleBefore =  titleProvider.getTitle(getMainPanel3())
        }
        newPanel.getMainPanel3().getWindowProperties().setTitleProvider(new MyDockingWindowTitleProvider(titleBefore))
    }

    @Override
    protected void resetLogs() {
        logs.setLength(0)
        modCount = 0
        analizedModCount = 0
        modDelayCount = 0
        analizedDelayModCount = 0
        statusDelay = null
        statusDelayedFetcher = null
    }

    @Override
    void additionalHilighter() {
        super.additionalHilighter()
        showSTackTrace()
        showLogs()
    }

    public String statusDelay;

    @Override
    void setStatusWithDelay(String status) {
        statusDelay = status
        delayedStatusChanged()
    }

    void delayedStatusChanged(){
        modDelayCount++
    }

    public static int intHalfMax = Integer.MAX_VALUE.intdiv(3)

    public StatusDelayedFetcher statusDelayedFetcher;

    void setDelayFetcher(LineCountStatWithCount countStatWithCount){
        statusDelayedFetcher = countStatWithCount;
        countStatWithCount.statusDelayListener = this;
    }

    @Override
    void showLogs() {
        if(analizedDelayModCount == modDelayCount){

        }else{
            if(modDelayCount>intHalfMax){
                modDelayCount = 1
            }
            analizedDelayModCount = modDelayCount
            String s = statusDelay
            if(statusDelayedFetcher!=null){
                s= statusDelayedFetcher.getDelayedStatus()
            }
            if(s!=null) {
                setStatus(s)
            }
        }
        if (analizedModCount == modCount) {
        } else {
            SwingUtilities.invokeLater {
                if(modCount>intHalfMax){
                    modCount = 1
                }

                analizedModCount = modCount
                String newText = logs.toString();
                int lengthbefore = newText.length();
                if(lengthbefore >maxLogSizeTextLength){
                    newText = truncateText(newText)
                }
//                System.out.println("show text : ${newText}")
//                logsView.textArea.append(new)
                logsView.textArea.setText(newText);
            }

        }
    }

    String truncateText(String newText){
        logs = new StringBuffer()
        totalLogSizeTruncated += maxLogSizeTextNewLength
        logs.append("truncates old logs : ${totalLogSizeTruncated}\n")
        newText= newText.substring(maxLogSizeTextNewLength)
        logs.append(newText)
        return  newText
    }



    @Override
    protected void showStackTraceText(String stackTrace) {
        textAreaStackTrace.textArea.text = stackTrace
    }

    void addMessageToLogViewerCurrentDate(String message){
        addMessageToLogViewer(new Date(),message)
    }

    void addMessageToLogViewer3(String message){
        if(message!=null && message.length()>maxOneLogMsgLength){
            message = message.substring(0,maxOneLogMsgLength)+' .. truncated long msg'
        }
        String msg = message+'\n';
        logs.append(msg)
        modCount++;
    }

    void addMessageToLogViewer(Date date,String message){
        String msg = "${sdf.format(date)} ${message}\n";
        addMessageToLogViewer3(msg)
    }


    //public List<RstaLogsShowColumns> logsShowColumns=[RstaLogsShowColumns.date,]

    @Override
    void addLogEvent(LogRecord loggingEvent){
        StringBuilder sb=new StringBuilder()
        List<RstaLogsShowColumns> logsShowColumns=[]
        if(logsLocationFullCheckBox.isSelected()){
            logsShowColumns.add(RstaLogsShowColumns.locationFull)
        }
        if(logsDateCheckBox.isSelected()){
            logsShowColumns.add(RstaLogsShowColumns.date)
        }
        logsShowColumns.each {
            String d = getData(it,loggingEvent)
            sb.append(d).append(' ')
        }
        String message = loggingEvent.getMessage()
        sb.append(message)
        addMessageToLogViewer3(sb.toString())
    }

//    void addLogEvent(LogEvent loggingEvent){
//        Date date = new Date(loggingEvent.getTimeMillis());
//        StringBuilder sb=new StringBuilder()
//        List<RstaLogsShowColumns> logsShowColumns=[]
//        if(logsLocationFullCheckBox.isSelected()){
//            logsShowColumns.add(RstaLogsShowColumns.locationFull)
//        }
//        if(logsDateCheckBox.isSelected()){
//            logsShowColumns.add(RstaLogsShowColumns.date)
//        }
//        logsShowColumns.each {
//            String d = getData(it,loggingEvent)
//            sb.append(d).append(' ')
//        }
//        String message = loggingEvent.getMessage().getFormattedMessage()
//        sb.append(message)
//        addMessageToLogViewer3(sb.toString())
//    }

    String getData(RstaLogsShowColumns logsShowColumns,LogRecord loggingEvent){
        if(logsShowColumns == RstaLogsShowColumns.date){
            Date date = new Date(loggingEvent.getMillis());
            return sdf.format(date)
        }
        if(logsShowColumns == RstaLogsShowColumns.locationFull){
            StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
            StackTraceElement location = getStacktraceELement(stackTraces);
            return location.toString()
        }
        throw new UnsupportedOperationException("${logsShowColumns}")
    }

//    String getData(RstaLogsShowColumns logsShowColumns,LogEvent loggingEvent){
//        if(logsShowColumns == RstaLogsShowColumns.date){
//            Date date = new Date(loggingEvent.getTimeMillis());
//            return sdf.format(date)
//        }
//        if(logsShowColumns == RstaLogsShowColumns.locationFull){
//            StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
//            StackTraceElement location = getStacktraceELement(stackTraces);
//            return location.toString()
//        }
//        throw new UnsupportedOperationException("${logsShowColumns}")
//    }

    public static Collection<String> ignoreClassesForCurrentClass = JrrClassUtils.ignoreClassesForCurrentClass;

    public StackTraceElement getStacktraceELement(StackTraceElement[] stackTraces) {
        int k = 0;
        for (StackTraceElement stackTraceElement : stackTraces) {
            k++;
            if (k < 11) {
                continue;
            }
            if (acceptStackTraceElement(stackTraceElement)) {
                return stackTraceElement;
            }
        }
        return null;
    }



    public boolean acceptStackTraceElement(StackTraceElement stackTraceElement) {
        String lcassName = stackTraceElement.getClassName();
        for (String ignore : ignoreClassesForCurrentClass) {
            boolean res = lcassName.startsWith(ignore);
            if (res) {
                return false;
            } else {

            }
        }
        return true;

    }

    @Override
    void onStopRequest() {
        addMessageToLogViewerCurrentDate("stop button pressed")
        super.onStopRequest()
    }

    SplitWindow getMainPanel3() {
        return mainPanel4;
    }

    @Override
    void exceptionOccured(Throwable e2) {
        super.exceptionOccured(e2)
        addMessageToLogViewerCurrentDate("${e2}")
    }
}