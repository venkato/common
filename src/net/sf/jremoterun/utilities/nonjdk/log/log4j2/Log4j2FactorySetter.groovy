package net.sf.jremoterun.utilities.nonjdk.log.log4j2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class Log4j2FactorySetter implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        Log4j2Utils.checkAndFixFactory()
    }


    public static void setDefaultClass(Class cl){
        JrrClassUtils.setFieldValue(new ClRef('org.apache.logging.log4j.util.StackLocator'),'DEFAULT_CALLER_CLASS',cl)
    }

}
