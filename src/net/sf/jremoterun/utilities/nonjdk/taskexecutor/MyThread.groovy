package net.sf.jremoterun.utilities.nonjdk.taskexecutor

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class MyThread extends Thread{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public final TaskExecutor taskExecutor;
    public volatile boolean stopThisThread =false
    public volatile boolean threadIsDead=false
    public volatile boolean isSleep=false

    MyThread(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor
    }

    @Override
    void run() {
        try{
            boolean isLastFinishedThread = false
            while (true){
                synchronized (taskExecutor.lock){
                    if(stopThisThread){
                        taskExecutor.threads.remove(this)
                        taskExecutor.waitingFinishThreads.remove(this)
                        threadIsDead = true
                        isLastFinishedThread =!taskExecutor.isTaskExecuterAlive()
                        break
                    }
                }
                if(TaskExecutor.debugTaskExecuting!=null){
                    TaskExecutor.debugTaskExecuting.run()
                }
                if(!taskExecutor.executingTask()){
                    synchronized (taskExecutor.sleepLock){
                        if(taskExecutor.removeThreadsOnTasks && !taskExecutor.isSomeTasksExecuting()){
                            taskExecutor.setThreadSize(0)
                            continue
                        }
                        isSleep = true
                        taskExecutor.sleepLock.wait()
                        isSleep = false
                    }
                }
            }
            taskExecutor.finishOneThread()
            if(isLastFinishedThread){
                taskExecutor.notiferFinishSucessfully()
            }
        }catch (InterruptedException e){
            log.info('',e)
        }finally {
            if(taskExecutor.threads.remove(this)){
                log.warning("removing thread ${this}")
            }
            threadIsDead = true
            taskExecutor.waitingFinishThreads.remove(this)
            taskExecutor.notifierFinishedFinally()
        }
    }
}
