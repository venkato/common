package net.sf.jremoterun.utilities.nonjdk;

import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class JrrThreadUtils {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static Runnable getTargetForThread(Thread thread){
        return JrrClassUtils.getFieldValue(thread,'target') as Runnable
    }

    static Set<Thread> getAllThreads(){
        return Thread.getAllStackTraces().keySet()
    }

    static List<Runnable> getAllTargetsInThreads(){
        return getAllThreads().collect {getTargetForThread(it)}.findAll {it!=null}
    }


}
