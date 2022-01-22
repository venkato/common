package net.sf.jremoterun.utilities.nonjdk.gradle.runner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.java11.sep1.Java11ModuleAccessF
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker
import org.gradle.invocation.DefaultGradle

import java.util.logging.Logger

@CompileStatic
class JrrGradleInit3 implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        InitLogTracker.defaultTracker.addLog('gradle called4')
        disableJavaModuleCheck()
        if (JrrGradleEnv.gradleEnv.customHandler != null) {
            RunnableFactory.runRunner(JrrGradleEnv.gradleEnv.customHandler)
        }

    }

    public static void disableJavaModuleCheck() {
        Java11ModuleAccessF.disableJavaModuleCheckIfNeeded()
    }


}
