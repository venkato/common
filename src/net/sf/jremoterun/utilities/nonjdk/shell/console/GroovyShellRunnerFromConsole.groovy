package net.sf.jremoterun.utilities.nonjdk.shell.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory

import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.util.logging.Logger

@CompileStatic
class GroovyShellRunnerFromConsole implements Runnable, ClassNameSynonym {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<? extends MavenIdContains> mavenIds =(List) (GroovyMavenIds.all +            [LatestMavenIds.jansi, LatestMavenIds.commonsNet])

    public static ClRef cnr = new ClRef("net.sf.jremoterun.utilities.nonjdk.shell.console.ConRunner3")

    @Override
    void run() {
        AddFilesToUrlClassLoaderGroovy adder = net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader
        addClassPathAndRun(adder)
    }

    void addClassPathAndRun(AddFilesToUrlClassLoaderGroovy adder) {
        adder.addMWithDependeciesDownload GroovyMavenIds.groovyAll
        adder.addAll mavenIds
        RunnableFactory.runRunner cnr
    }
}
