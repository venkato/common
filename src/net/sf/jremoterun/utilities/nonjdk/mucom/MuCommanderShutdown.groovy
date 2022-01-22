package net.sf.jremoterun.utilities.nonjdk.mucom

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class MuCommanderShutdown {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static ClRef clRef1 = new ClRef('com.mucommander.ShutdownHook')

    static void performShutdownTasksIfExists() {
        Class clazz
        try {
            clazz = clRef1.loadClass2()
        } catch (ClassNotFoundException e) {
            log.info("${clRef1} ${e}")
        }
        if (clazz != null) {
            JrrClassUtils.invokeJavaMethod(clazz, 'performShutdownTasks')
        }
    }

    static void performShutdownTasks() {
        JrrClassUtils.invokeJavaMethod(clRef1, 'performShutdownTasks')
    }

}
