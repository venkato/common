package net.sf.jremoterun.utilities.nonjdk.shareduserstart

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.CallerInfo
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.CallerInfoGetter
import net.sf.jremoterun.utilities.groovystarter.st.JrrRunnerPhase2

import java.util.logging.Logger

@CompileStatic
class AddSettings2 implements Runnable  , CallerInfo, CallerInfoGetter  {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Object callerInfo;

    @Override
    void run() {
        runImpl()
    }

    ClRef convertersRegister = new ClRef('net.sf.jremoterun.utilities.nonjdk.str2obj.RegisterConverters')
    ClRef ifFrameWoekAdder = new ClRef('net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure')
    ClRef defaultClasspathAdder = new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.DefaultClasspathAdder')


    ClRef extentionMethodsChecker = new ClRef('net.sf.jremoterun.utilities.nonjdk.ExtentionMethodsChecker')
    ClRef gitAdder77 = new ClRef('net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ClassPathInit3')
    ClRef addDefaultProgi = new ClRef('net.sf.jremoterun.utilities.nonjdk.shareduserstart.AddSettings3')



    void runImpl() {
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info("AddSettings2 starting ..")
        }
        GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader.add(BaseDirAdded.buildFile(SharedFolderSettingsEnum.invocationFrameworkJar))
        GroovyMethodRunnerParams.gmrpn.addL(JrrRunnerPhase2.hostConfigWindowsEnriched,false,this.&addGitRefSupport)

    }



    void addGitRefSupport() {
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info("AddSettings2 adding iff ..")
        }
        AddFilesToUrlClassLoaderGroovy adder = GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader
        adder.addF BaseDirAdded.buildFile(SharedFolderSettingsEnum.invocationFrameworkJar)
//        adder.addF(new File(AddSettings1.buildFile(SharedFolderSettings.InvocationFramework),"src-frameworkloader"));
        RunnableWithParamsFactory.fromClass4(ifFrameWoekAdder, [adder, BaseDirAdded.buildFile(SharedFolderSettingsEnum.InvocationFramework)])
        RunnableWithParamsFactory.fromClass4(gitAdder77, [adder, BaseDirAdded.buildFile(SharedFolderSettingsEnum.gitDir)])
        //RunnableFactory.runRunner extentionMethodsChecker
        RunnableFactory.runRunner convertersRegister
        if(SharedFolderSettingsGeneral.runAfter2!=null) {
            RunnableFactory.runRunner(SharedFolderSettingsGeneral.runAfter2)
        }

        RunnableFactory.runRunner addDefaultProgi

    }
}
