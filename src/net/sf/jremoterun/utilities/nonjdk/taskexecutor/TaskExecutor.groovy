package net.sf.jremoterun.utilities.nonjdk.taskexecutor

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
abstract class TaskExecutor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    boolean removeThreadsOnTasks = true;

    public static Runnable debugTaskExecuting;

    public final Object sleepLock = new Object()
    public final Object lock = new Object()

    private static WeakHashMap<TaskExecutor, Object> tasksExecutors = new WeakHashMap<>()
    final Vector<MyThread> threads = new Vector()
    final Vector<MyThread> waitingFinishThreads = new Vector<>()

    public static Object object = new Object();

    TaskExecutor() {
        tasksExecutors.put(this, object)
    }

    Set<TaskExecutor> getTasksExecutors() {
        return tasksExecutors.keySet();
    }

    void newThread(Thread t) {

    }


    void addThreads(final int k) {
        synchronized (lock) {
            for (i in 0..<k) {
                l:
                {
                    for (MyThread myThread2 : waitingFinishThreads) {
                        assert myThread2.stopThisThread;
                        if (!myThread2.threadIsDead) {
                            assert myThread2.isAlive()
                            myThread2.stopThisThread = false;
                            waitingFinishThreads.remove(myThread2)
                            break l;
                        }
                    }
                }
                waitingFinishThreads.clear();
                Thread myThread = new MyThread(this);
                myThread.setName('TasksExecutor')
                newThread(myThread);
                myThread.start()
                log.debug('starting new thread')
                this.threads.add(myThread)
            }
        }
    }


    void finishOneThread() {

    }

    void wakeUpOneThread() {
        synchronized (sleepLock) {
            sleepLock.notifyAll()
        }
    }

    void waitingFinish() {
        synchronized (sleepLock) {
            sleepLock.notifyAll()
        }
        if (this.threads.size() > 0) {
            new ArrayList<MyThread>(this.threads).each { it.join() }
            waitingFinish()
        } else {
            if (waitingFinishThreads.size() > 0) {
                List<Thread> waitingFInishThreads2 = new ArrayList<>(waitingFinishThreads)
                waitingFInishThreads2.each { it.join() }
                synchronized (lock) {
                    waitingFinishThreads.removeAll(waitingFInishThreads2)
                }
                waitingFinish()
            }

        }

    }


    boolean isTaskExecuterAlive() {
        ArrayList<Thread> threads = new ArrayList<>(threads)
        Thread find1 = threads.find { it.isAlive() }
        if (find1 != null) {
            return true
        }
        ArrayList<Thread> threads1 = new ArrayList<>(waitingFinishThreads)
        Thread find2 = threads1.find { it.isAlive() }
        if (find2 != null) {
            return true
        }
        return false
    }

    int getThreadSize() {
        return threads.size()
    }

    void setThreadSize(int threads1) {
        assert threads1 >= 0
        synchronized (lock) {
            int size = threads.size()
            int k = size - threads1
            if (k < 0) {
                addThreads(-k)
            } else if (k > 0) {
                while (k > 0) {
                    MyThread myThread = threads.remove(0)
                    myThread.stopThisThread = true
                    synchronized (sleepLock) {
                        sleepLock.notifyAll()
                    }
                    waitingFinishThreads.add(myThread)
                    k--
                }
                // some threads may be at wait state
                Thread.sleep(100)

            }
        }
        synchronized (sleepLock){
            sleepLock.notifyAll()
        }
        if(threads1==0){
            assert threads.size()==0
        }
    }

    void noTasksNotifier(){
        setThreadSize(0)
    }

boolean isSomeTasksExecuting(){
    MyThread find1 = new ArrayList<MyThread>(threads).find { !(it.isSleep) && Thread.currentThread() != it }
    return find1!=null
}

    int getNumberWaitingFinishTHreads(){
        return waitingFinishThreads.size()
    }

    void notiferFinishSucessfully(){

    }

    void notifierFinishedFinally(){

    }
    abstract boolean executingTask();


}
