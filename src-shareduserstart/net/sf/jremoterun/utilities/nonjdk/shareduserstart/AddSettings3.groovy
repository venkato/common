package net.sf.jremoterun.utilities.nonjdk.shareduserstart

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.ShortcutSelector
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.CallerInfo
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.CallerInfoGetter
import net.sf.jremoterun.utilities.nonjdk.classpath.CutomJarAdd
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.IfframeworkClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.SshConsoleClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew.MavenIdsCollector
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Pi4j
import net.sf.jremoterun.utilities.nonjdk.classpath.refs2.CutomJarAdd1
import net.sf.jremoterun.utilities.nonjdk.compile.IfFrameworkCompiler
import net.sf.jremoterun.utilities.nonjdk.compile.JeditTermCompilerConsoleCompiler
import net.sf.jremoterun.utilities.nonjdk.consoleprograms.DefaultConsolePrograms

import java.util.logging.Logger

@CompileStatic
class AddSettings3 implements Runnable  , CallerInfo, CallerInfoGetter  {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Object callerInfo;

    Map programs = [b                : this.&addGenericMavenIds,
                    addSshConsoleLibs: this.&addSshConsoleLibs2,
    ]


    @Override
    void run() {
        doConfigImpl()
    }

    void doConfigImpl() {
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info("AddSettings3 starting ..")
        }
        DefaultConsolePrograms.addDefaultPrograms(programs)
        while (true) {
            boolean isRunSomething = ShortcutSelector.runActionRemoveFirstParam2(programs)
            if(SharedFolderSettingsGeneral.doDebugLogging) {
                log.info("isRunSomething = ${isRunSomething}")
            }
            if (!isRunSomething) {
                break
            }
        }
        if (SharedFolderSettingsGeneral.runAfter3 != null) {
            RunnableFactory.runRunner(SharedFolderSettingsGeneral.runAfter3)
        }
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info("AddSettings3 done")
        }
    }

    void addSshConsoleLibs2() {
        AddFilesToUrlClassLoaderGroovy adder = GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader
        adder.addAll new SshConsoleClasspath().getMavenIdsCustom()
        adder.addAll new IfframeworkClasspath().getMavenIdsCustom()
        adder.add JeditTermCompilerConsoleCompiler.compileIfNeededS()
        adder.add CutomJarAdd1.downloadIdw()
        CutomJarAdd.addCustom(adder)
        net.sf.jremoterun.utilities.nonjdk.classpath.SshConsoleAdder.addSshConsole(adder)
        //adder.add net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustomRefsUrls.pureJavacommnyJetBrainsUrl
//        adder.add LatestMavenIds.pureJavaComm
    }

    void addGenericMavenIds() {
        GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader.addAll Pi4j.all
    }



}
