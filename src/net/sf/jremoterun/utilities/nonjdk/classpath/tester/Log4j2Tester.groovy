package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.logging.log4j.LogManager

import java.util.logging.Logger

@CompileStatic
class Log4j2Tester implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassPathTesterHelper2 helper;

    Log4j2Tester(ClassPathTesterHelper2 helper) {
        this.helper = helper
    }

    void log4j2Tester(){
        org.apache.logging.log4j.Logger logger2 = LogManager.getLogger("test");
        helper.checkClassInstanceOf5(logger2, org.apache.logging.log4j.tojul.JULLogger.class);
//        helper.checkClassInstanceOf5(logger2, org.apache.logging.log4j.core.Logger.class);
    }

    @Override
    void run() {
            log4j2Tester()
    }
}
