package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef

import java.util.logging.Logger

@CompileStatic
class CancelAllJavaTimers implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static ClRef clRefTimerThread = new ClRef('java.util.TimerThread')

    @Override
    void run() {
        cancelAllJavaTimers()
    }


    static void cancelAllJavaTimers() {
        Set<Thread> threads = Thread.allStackTraces.keySet().findAll { it.class.name == clRefTimerThread.className }
//        threads.each {it.@daemon = true}
        threads.each { cancelTimerThread(it) }
        log.info "cancelled java timers : ${threads.size()}"
    }

    static void cancelTimerThread(Thread ts) {

        Object queue = JrrClassUtils.getFieldValueR(clRefTimerThread,ts, 'queue')
        synchronized (queue) {
            JrrClassUtils.setFieldValueR(clRefTimerThread, ts, 'newTasksMayBeScheduled', false)
            JrrClassUtils.invokeJavaMethodR(new ClRef('java.util.TaskQueue'), queue, 'clear')
            queue.notify();
        }
    }

}
