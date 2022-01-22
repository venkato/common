package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker

import java.util.logging.Logger

@CompileStatic
class JrrGradleAdder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static GroovyClassLoader classLoader1 = JrrClassUtils.getCurrentClassLoaderGroovy()

    public static AddFilesToUrlClassLoaderGroovy adder = new AddFilesToUrlClassLoaderGroovy(classLoader1)


}
