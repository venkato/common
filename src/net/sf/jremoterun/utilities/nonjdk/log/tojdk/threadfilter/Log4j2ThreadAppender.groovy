package net.sf.jremoterun.utilities.nonjdk.log.tojdk.threadfilter

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Handler
import java.util.logging.LogRecord
import java.util.logging.Logger

@CompileStatic
public abstract class Log4j2ThreadAppender extends Handler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    public volatile Thread loggingThread;

    public HashSet<String> loggingLogger = new HashSet<String>();
    public HashSet<String> ignoreLoggers = new HashSet<String>();

    public volatile boolean translateAllLoggers = false;
    public volatile boolean translateAllLoggersForExecuterThread = false;

    public Log4j2ThreadAppender() throws Exception {
        loggingThread = Thread.currentThread();
    }

    public void addClassLoggingIgnore(Class clazz) {
        ignoreLoggers.add(clazz.getName())
    }

    public void addClassToLog4jTranslate(Class clazz) {
        loggingLogger.add(clazz.getName());
    }

//    void setAppenderName(String name){
//        JrrClassUtils.setFieldValue(this,'name',name)
//    }


    public boolean isPassEvent(LogRecord loggingEvent) {
        boolean passed = false;
        if (!passed && translateAllLoggers) {
            passed = true;
        }
        if (!passed && Thread.currentThread() == loggingThread && translateAllLoggersForExecuterThread) {
            passed = true
        }
        if (!passed && loggingLogger.contains(loggingEvent.getLoggerName())) {
            passed = true
        }
        if (passed) {
            if (ignoreLoggers.contains(loggingEvent.getLoggerName())) {
                passed = false
            }
        }
//        System.out.println("event passed ${passed} , msg = ${loggingEvent.message}")
        return passed
    }


    public abstract void filterPassed(LogRecord loggingEvent);

    @Override
    void publish(LogRecord loggingEvent) {
        boolean isLog = isPassEvent(loggingEvent);
//        System.out.println("filter passed ${isLog}");
        if (isLog) {
            filterPassed(loggingEvent);
        } else {
//            Thread.dumpStack()
        }
    }

    @Override
    void flush() {

    }

    @Override
    void close() throws SecurityException {

    }
}
