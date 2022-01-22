package net.sf.jremoterun.utilities.nonjdk.rstarunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.nonjdk.idwutils.ViewAndPanel

import javax.swing.*
import java.util.List
import java.util.logging.LogRecord
import java.util.logging.Logger

@CompileStatic
abstract class RstaRunnerWithStackTrace3 extends RstaRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    StackTraceTextArea textAreaStackTrace = new StackTraceTextArea()

    List<String> ignoreClasses = new ArrayList(RstaRunnerWithStackTrace.ignoreClassesDefault);

    public ViewAndPanel runnerView = new ViewAndPanel("Runner", panel);
    //public ViewAndPanel stackTraceView = new ViewAndPanel("Thread dump");
    //public TextAreaAndView logsView = new TextAreaAndView("Logs  ");
//    public TabWindow rightPanel
//    public SplitWindow mainPanel4;

//    public volatile StringBuffer logs = new StringBuffer()

//    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss")
//    volatile int modCount = 0

    net.sf.jremoterun.utilities.nonjdk.log.tojdk.threadfilter.Log4j2ThreadAppender log4j2ThreadAppenderJdk = new net.sf.jremoterun.utilities.nonjdk.log.tojdk.threadfilter.Log4j2ThreadAppender() {
        @Override
        void filterPassed(LogRecord loggingEvent) {
            addLogEvent(loggingEvent)
        }
    }


    abstract void addLogEvent(LogRecord loggingEvent)
    //abstract void addLogEvent(LogEvent loggingEvent)


//    SplitWindow getMainPanel3() {
//        return mainPanel4;
//    }


    RstaRunnerWithStackTrace3(File file) {
        super(file)
        init3()
    }

    RstaRunnerWithStackTrace3(String text2) {
        super(text2)
        init3()
    }

    //abstract void doLoayout()
//        rightPanel = new TabWindow()
//        rightPanel.addTab(stackTraceView.view)
//        rightPanel.addTab(logsView.view)
//        doLayoutCreateMainPanel()
//    }

//    void doLayoutCreateMainPanel() {
//        mainPanel4 = new SplitWindow(true, 0.8f, runnerView.view, rightPanel)
//    }

    protected void init3() {
        //doLoayout()
//        logsView.textArea.setEditable(false)
//        logsView.textArea.setLineWrap(true)
//        stackTraceView.panel.add(textAreaStackTrace.scrollPane, BorderLayout.CENTER)
//        panel.add(textAreaStackTrace.scrollPane, BorderLayout.EAST)
        log.info "scroll pane added"
        ignoreClasses.addAll(JrrClassUtils.ignoreClassesForCurrentClass)

                JdkLogFormatter.getRootLogger().addHandler(log4j2ThreadAppenderJdk)
            log4j2ThreadAppenderJdk.translateAllLoggersForExecuterThread = true

    }

//    String buildLogAppenderName() {
//        String name2 = "RstaRunner-${getClass().getSimpleName()}-"
//        String name3 = name2 + new Random().nextInt()
//            Set<String> set2 = Log4j2Utils.rootLogger.getAppenders().keySet();
//            while (set2.contains(name2)) {
//                name3 = name2 + new Random().nextInt()
//            }
//
//        return name3
//    }

//    @Override
//    Component getMainPanel() {
//        throw new IllegalStateException()
//    }

    protected abstract void resetLogs()

    @Override
    void codeStarted() {
        super.codeStarted()
        resetLogs()
//        modCount = 0
//        analizedModCount = 0
        log4j2ThreadAppenderJdk.loggingThread = Thread.currentThread()
        super.codeStarted()
    }


    @Override
    void additionalHilighter() {
        super.additionalHilighter()
        showSTackTrace()
        showLogs()
    }

    protected void showSTackTrace() {
        Thread codeThread2 = textAreaRunner.codeThread
        if (codeThread2 == null) {
            if(!continueCollectLogsAfterThreadFinished) {
                log.info("thread value is null");
            }
        } else {
            List<StackTraceElement> stackTraces = codeThread2.getStackTrace().toList();
            //StringBuilder stackTraceText = new StringBuilder()
            List<String> resultst2 = []
            if(isShowAllStackTraces()) {
                resultst2 = stackTraces.collect {it.toString()}
            }else{
                boolean acceptedBefore = true;
                boolean isFirst = true;
                stackTraces.each { StackTraceElement el ->
                    String className = el.getClassName()
                    boolean accept = ignoreClasses.find { className.startsWith(it) } == null
                    if(isFirst){
                        accept = true
                        isFirst = false
                    }
                    if (accept) {
                        acceptedBefore = true
                        resultst2.add(el.toString());
                    }else{
                        if (acceptedBefore) {
                            acceptedBefore = false;
                            resultst2.add '.. '
                        }
                    }
                }
                if(resultst2.size()>0){
                    int indexLast = resultst2.size()-1
                    String lastValue = resultst2.get(indexLast)
                    if(lastValue.contains('java.lang.Thread.run')){
                        resultst2.remove(indexLast)
                    }
                }
            }
            if(reverseStackTrace){
                resultst2 = resultst2.reverse()
            }
            String text3 = resultst2.join('\n');
            SwingUtilities.invokeLater {
                showStackTraceText(text3)
            }
        }
    }

    public static boolean reverseStackTrace = true

    protected abstract boolean isShowAllStackTraces();

    protected abstract void showStackTraceText(String stackTrace);

    protected abstract void showLogs();

    // t:dual
    @Override
    void codeStopped() {
        super.codeStopped()
        SwingUtilities.invokeLater {
            showStackTraceText('')
        }
        // Log4j2Utils.rootLogger.removeAppender(log4j2ThreadAppender)
        showLogs()
    }
}