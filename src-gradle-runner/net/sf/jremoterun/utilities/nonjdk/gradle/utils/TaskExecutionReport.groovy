package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.internal.tasks.TaskStateInternal
import org.gradle.api.tasks.TaskContainer
import org.gradle.api.tasks.TaskState;

import java.util.logging.Logger;

/**
 * @see org.gradle.api.tasks.diagnostics.TaskReportTask
 */

@groovy.transform.TupleConstructor
@CompileStatic
class TaskExecutionReport {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean printClassName = false
    public boolean printStatus = true
    public boolean printGroup = false
    //public boolean printDidWork = true
    public boolean displayOnlyExecuted = true

    void printTasksAll(Project project){
        if(project==null){
            throw new NullPointerException("Project is null")
        }
        TaskContainer tasks = project.getTasks()
        if(tasks.size()==0){
            log.warning("no tasks")
        }else {
            Set<Task> takskss = tasks.findAll { isNeedPrint(it as Task) }
            if (takskss.size() == 0) {
                log.warning("no printable tasks of ${tasks.size()}")
            }else {
                takskss.each {
                    printTask(it)
                }
            }
        }
    }

    void printTask(Task t){
        log.warning printTaskImpl(t).join('\t')
    }

    List<Object> printTaskImpl(Task t){
        List r= []
        r.add(t.getName())
        if(printGroup) {
            r.add t.getGroup()
        }
        if(printStatus){
            TaskState state = t.getState()
            if (state instanceof TaskStateInternal) {
                TaskStateInternal stt = (TaskStateInternal) state;
                r.add(stt.getOutcome())
            }else{
                r.add('outcomeUnknown')
            }
        }
//        if(printDidWork){
//            r.add(t.getDidWork())
//        }
        if(printClassName){
            r.add(t.getClass().getName())
        }
        return r;
    }


    boolean isNeedPrint(Task t){
        if(displayOnlyExecuted){
            return t.getState().getExecuted()
        }
        return true
    }


}
