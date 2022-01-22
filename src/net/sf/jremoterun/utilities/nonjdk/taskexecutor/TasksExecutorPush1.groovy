package net.sf.jremoterun.utilities.nonjdk.taskexecutor

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
abstract class TasksExecutorPush1<T> extends TasksExecutorStat{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    final LinkedHashSet<T> tasksData=new LinkedHashSet<>()

    private int maxThreds

    final Object lock2 =new Object()
    boolean started = false
    Date startDate;
    HashSet<T> currentExecutingTasks =new HashSet<>()

    TasksExecutorPush1(int maxThreds) {
        this.maxThreds = maxThreds
    }

    void start(){
        synchronized (lock2){
            assert !started
        }
        started=true
        int threads7 =  Math.min(maxThreds,getTasksSize())
        if(threads7==0 && getTasksSize()!=0){
            log.warning('no threads')
        }
        setThreadSize(threads7)
        startDate = new Date()
    }
    void stop(){
        synchronized (lock2){
            setThreadSize(0)
            waitingFinish()
            started = false
        }
    }

    void setMaxThreads(int maxThreads){
        synchronized (lock2) {
            this.maxThreds = maxThreads
            if (getThreadSize() < maxThreads) {
                int threads7 = Math.min(maxThreads, getTasksSize())
                setThreadSize(threads7)
            } else {
                setMaxThreads(maxThreads)
            }
        }
    }



    int getTasksSize(){
        return tasksData.size()
    }


    void addTask(T task){
        synchronized (lock2){
            tasksData.add(task)
            addThreadsIfNeeded()
        }
    }



    void addThreadsIfNeeded(){
        if(started){
            if(getThreadSize()<maxThreds){
    int threads7=Math.min(maxThreds,getTasksSize())
                setThreadSize(threads7)
            }else {
                setThreadSize(maxThreds)
            }
        }
    }

    T takeTask(){
        synchronized (lock2){
            if(tasksData.isEmpty()){
                return null
            }
            T task = tasksData.first()
            tasksData.remove(task)
            return task
        }
    }

    @Override
    boolean executingTask2() {
        T task= takeTask()
        if(task==null){
            return false
        }
        try{
            currentExecutingTasks.add(task)
            executingTask(task)
        }catch (RuntimeException e){
            log.info3("${tasksData.size()} ${task}",e)
        }catch (Throwable e){
            handleError(task,e)
        }finally {
            currentExecutingTasks.remove(task)
        }
        return tasksData.size()>0
    }

    void handleError(T task,Throwable e){
        log.error(task,e)
    }

    BigDecimal getEstimatedRemainingTime(){
        int threadSize1 = getThreadSize()
        if(threadSize1==null){
            return null
        }
        return (getAllTime()*tasksData.size())/(getExecutedTasks()*threadSize1)
    }

    abstract void executingTask(T task);


    void removeAllData(){
        if(!tasksData.isEmpty()){
            synchronized (lock2){
                tasksData.clear()
            }
        }
    }

    void addAll(Collection<T> tasks){
        synchronized (lock2){
            tasksData.addAll(tasks)
            addThreadsIfNeeded()
        }
    }

    void removeTask(T task){
        synchronized (lock2){
            tasksData.remove(task)
        }
    }

    @Override
    void setThreadSize(int threads1) {
        assert  threads1<=maxThreds
        super.setThreadSize(threads1)
    }
}
