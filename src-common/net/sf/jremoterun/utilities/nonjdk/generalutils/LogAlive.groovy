package net.sf.jremoterun.utilities.nonjdk.generalutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class LogAlive implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static long defaultLogIntervalInSeconds = DurationConstants.oneHour.timeInSecInt
    public static volatile LogAlive defaultLogTimerThread

    public SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    public long interval;
    public Thread thread
    public boolean needRun = true
    public Date startDate = new Date()

    public String threadName = getClass().getSimpleName()
    //public Object lockObject = new Object()

    LogAlive(long interval) {
        this.interval = interval
    }

    @Override
    void run() {
        while (needRun) {
            Thread.sleep(interval)
            println buildMsg()
        }
        log.info "finished"
    }

    String buildMsg() {
        return "${getClass().getSimpleName()} : ${simpleDateFormat.format(new Date())}"
    }

    static LogAlive startLogTimer() {
        return startLogTimer2(DurationConstants.oneSec.timeInMsLong * defaultLogIntervalInSeconds)
    }

    static LogAlive startLogTimer2(long intervalInMs) {
        if (defaultLogTimerThread != null) {
            log.info("log timer already created before")
            return defaultLogTimerThread
        }
        LogAlive timer = new LogAlive(intervalInMs)
        timer.startLogTimerWithChecks()
        return timer

    }


//    static Thread startLogTimer(long interval) {
//        LogAlive logAlive = new LogAlive(interval)
//        logAlive.startLogTimerImpl()
//        return logAlive.thread
//    }

    void startLogTimerWithChecks() {
        assert defaultLogTimerThread == null
        startLogTimerImpl()
        defaultLogTimerThread = this
    }

    void startLogTimerImpl() {
        thread = new Thread(this, threadName)
        thread.setDaemon(true)
        thread.start()

    }


}
