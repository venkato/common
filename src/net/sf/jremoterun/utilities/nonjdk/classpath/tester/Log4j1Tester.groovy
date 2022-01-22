package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import org.apache.log4j.Level;

import java.util.logging.Logger;

@CompileStatic
class Log4j1Tester implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassPathTesterHelper2 helper;

    Log4j1Tester(ClassPathTesterHelper2 helper) {
        this.helper = helper
    }

    void log4j1Tester(){
        helper.checkClassOnceIfPresent(new ClRef('org.apache.log4j.Level'), LatestMavenIds.log4jOld)
        org.apache.log4j.Logger logger1 = org.apache.log4j.Logger.getLogger("test");
        helper.checkClassInstanceOf5(logger1, org.apache.log4j.Logger.class);
        helper.checkFieldExists5(Level, 'TRACE')
    }

    @Override
    void run() {
            log4j1Tester()
    }
}
