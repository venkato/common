package net.sf.jremoterun.utilities.nonjdk.taskexecutor

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
abstract class TasksExecutorStat extends TaskExecutor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    TasksExecutorStat() {
    }

    final Object lock3 = new Object()
    long executedTasks = 0
    long maxExecutedTime = 0
    long minExecutedTime = Long.MAX_VALUE
    long allTime = 0

    boolean executingTask2() {

    }

    @Override
    boolean executingTask() {
        synchronized (lock3) {
            executedTasks++
        }
        long startTime = System.currentTimeMillis()
        try{
            return executingTask2()
        }finally {
            long taskTaskRTime = System.currentTimeMillis()-startTime
            if(taskTaskRTime>0){
                synchronized (lock3){
                    maxExecutedTime=Math.max(maxExecutedTime,taskTaskRTime)
                    minExecutedTime=Math.max(minExecutedTime,taskTaskRTime)
                    allTime+=taskTaskRTime
                }
            }
        }
    }

    BigDecimal getAvgExecTime(){
        if(executedTasks==0){
            return null
        }
        return new BigDecimal(allTime).divide(new BigDecimal(executedTasks))
    }


}
