package net.sf.jremoterun.utilities.nonjdk.taskexecutor

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean
import net.sf.jremoterun.utilities.nonjdk.javassist.jrrbean.JrrBeanMaker

import javax.management.MBeanServer
import javax.management.ObjectName
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class JrrRepeatedTask {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Object lock=new Object()

    List<TaskExecutor> taskExecutors=new Vector<>()

    static MBeanServer mBeanServer= JrrUtils.findLocalMBeanServer()

    String id;
    static SimpleDateFormat sdf = new SimpleDateFormat('HH-mm-ss')

    JrrRepeatedTask(Class clazz, String id2) {
        id = clazz.getSimpleName()+',localtype='+id2
    }

    void addTaskExecutor(TaskExecutor taskExecutor){
        taskExecutors.add(taskExecutor)
        String on = JrrBeanMaker.jrrMBeansPrefix+id+',timer=timer,date='+sdf.format(new Date())
        log.info "${on}"
        mBeanServer.registerMBean(new MBeanFromJavaBean(taskExecutor),  new ObjectName(on))
    }

    void stopAllTaskexecutors(){
        new ArrayList<TaskExecutor>(taskExecutors).each {
            it.setThreadSize(0)
        }
    }


    void stopAndWaitingFinishAll(){
        stopAllTaskexecutors()
        new ArrayList<TaskExecutor>(taskExecutors).each {it.waitingFinish()}
    }



}
