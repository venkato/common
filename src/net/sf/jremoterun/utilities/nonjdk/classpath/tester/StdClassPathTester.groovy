package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import com.sun.jna.Native
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.classpath.CheckNonCache2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorIThrowImmediate
import org.slf4j.LoggerFactory

import java.util.logging.Logger

@CompileStatic
class StdClassPathTester implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassPathTesterHelper2 helper;

    StdClassPathTester(ClassPathTesterHelper2 helper) {
        this.helper = helper
    }

    StdClassPathTester() {
        this.helper = new ClassPathTesterHelper2(new ProblemCollectorIThrowImmediate());
    }

    @Override
    public void run() {
        CheckNonCache2.check();


        helper.checkClassOnceIfPresent(new ClRef('org.apache.log4j.Level'), LatestMavenIds.log4jOld)
        helper.checkClassOnceIfPresent(new ClRef('org.apache.commons.logging.LogFactory'), CustObjMavenIds.commonsLoggingMavenId)

        helper.checkClassOnceIfPresent(new ClRef('org.slf4j.LoggerFactory'), CustObjMavenIds.slf4jApi)
        helper.checkClassOnceIfPresent(new ClRef('org.slf4j.Logger'), CustObjMavenIds.slf4jApi)

        helper.checkClassOnce5(Native, LatestMavenIds.jna)
        helper.loadTester(new ClRef('org.apache.log4j.Level'),new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.tester.Log4j1Tester'))
        helper.loadTester(new ClRef('org.apache.logging.log4j.Logger'),new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.tester.Log4j2Tester'))
        slf4jTester()



        // getHostText used in jedit ssh
        helper.checkResourceExists("META-INF/services/org.codehaus.groovy.runtime.ExtensionModule")
        checkToolsJar()

        assert MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver == null
        IvyDepResolver2.setDepResolver()
        DropshipClasspath.downloadyIvydepToIvyDir()
    }

    void slf4jTester(){
        org.slf4j.Logger loggerSl4j = LoggerFactory.getLogger("test");
    }

//    void log4j1Tester(){
//        org.apache.log4j.Logger logger1 = org.apache.log4j.Logger.getLogger("test");
//        helper.checkClassInstanceOf5(logger1, org.apache.log4j.Logger.class);
//        helper.checkFieldExists5(Level, 'TRACE')
//    }

    void log4j2Tester(){

//        org.apache.logging.log4j.Logger logger2 = LogManager.getLogger("test");
//        helper.checkClassInstanceOf5(logger2, org.apache.logging.log4j.core.Logger.class);
    }


    static void checkToolsJar() {
        ClassPathTesterHelper2 helper2 = new ClassPathTesterHelper2(new ProblemCollectorIThrowImmediate());
        JavaToolsJarTester stdClassPathTester2 = new JavaToolsJarTester(helper2)
        stdClassPathTester2.checkToolsJar()
    }





}