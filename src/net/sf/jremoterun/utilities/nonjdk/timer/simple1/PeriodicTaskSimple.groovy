package net.sf.jremoterun.utilities.nonjdk.timer.simple1


import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE

import java.util.logging.Logger

@CompileStatic
abstract class PeriodicTaskSimple {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public volatile long sleepTime
    public volatile long startUpDelay = 0

    public volatile Thread thread;
    public volatile boolean doRun = true;
    public volatile boolean stopOnException = true;
    public volatile boolean daemon = true;
    public volatile boolean showExceptionInSwing = false;
    public volatile Object lock = new Object();
    public volatile Throwable lastException;
    public volatile Date lastRun
    public String threadName;
    public String msgOnStart;
    public String msgOnStop;

    public String msgOnError = 'failed'
    public List<Runnable> tasks = []

    PeriodicTaskSimple(long sleepTime, String threadName) {
        this.sleepTime = sleepTime
        this.threadName = threadName
        assert sleepTime > 0
        Runnable r = { doJob() }
        tasks.add(r)
    }

    void start() {
        Runnable r = {
            runImpl()
        }
        thread = new Thread(r, threadName)
        if (daemon) {
            thread.setDaemon(true)
        }
        thread.start()
    }

    void sleep1() {
        synchronized (lock) {
            lock.wait(sleepTime)
        }
    }


    void runImpl() {
        if (msgOnStart != null) {
            log.info msgOnStart
        }
        if (startUpDelay > 0) {
            synchronized (lock) {
                lock.wait(startUpDelay)
            }
        }
        try {
            while (doRun) {
                tasks.each {
                    if (doRun) {
                        try {
                            it.run()
                        } catch (Throwable e) {
                            onException(it, e)
                            lastRun = new Date()
                        }
                    }
                }
                lastRun = new Date()

                sleep1()
            }
        } finally {
            onStopped()
        }
    }

    boolean isRunning() {
        return thread != null && thread.isAlive()
    }

    void stop() {
        doRun = false
        synchronized (lock) {
            lock.notifyAll()
        }
    }


    void onException(Runnable task, Throwable e) {
        log.severe(msgOnError, e)
        lastException = e
        if (showExceptionInSwing) {
            JrrUtilitiesShowE.showException(msgOnError, e);
        }

        if (stopOnException) {
            doRun = false
        }
    }

    abstract void doJob()

    void onStopped() {
        if (msgOnStop != null) {
            log.info msgOnStop
        }
    }

}
