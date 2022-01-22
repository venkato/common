package net.sf.jremoterun.utilities.nonjdk.shareduserstart

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.JrrRunnerPhase
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode

import java.util.logging.Logger;

@CompileStatic
class AddSettings1 extends InjectedCode {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    @Override
    Object get(Object key) {
        assert key!=null
        List args= key as List
        assert args.size()>1
        Object obj= args[1]
        assert obj instanceof Boolean
        SharedFolderSettingsGeneral.doDebugLogging=obj
        addImpl(args[0] as File)
        return null
    }


    void addImpl(File baseDir1) {
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info('inside AddSettings1')
        }
        AddFilesToClassLoaderGroovy adder = GroovyMethodRunnerParams.gmrp.addFilesToClassLoader
        SharedFolderSettingsGeneral.baseDir = baseDir1
        assert SharedFolderSettingsGeneral.baseDir!=null
        assert SharedFolderSettingsGeneral.baseDir.exists()
        String arg3 = net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2.getFirstParam()
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info("first arg = ${arg3}")
        }
        if(SharedFolderSettingsGeneral.runAfter1!=null) {
            RunnableFactory.runRunner(SharedFolderSettingsGeneral.runAfter1)
        }
        if (arg3 == SharedFolderSettingsGeneral.addiffS) {
            if(SharedFolderSettingsGeneral.doDebugLogging) {
                log.info("adding iff.jar to classpath")
            }
            net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2.removeFirstParam()
            MavenDefaultSettings.mavenDefaultSettings.grapeFileFinder.setMavenLocalDir2(AddSettings1.buildFile(SharedFolderSettingsEnum.ivyCacheDir))
            MavenDefaultSettings.mavenDefaultSettings.jrrDownloadDir = AddSettings1.buildFile(SharedFolderSettingsEnum.jrrDownloadDir)
            GroovyMethodRunnerParams.gmrp.addL(JrrRunnerPhase.checks, false, new ClRef('net.sf.jremoterun.utilities.nonjdk.shareduserstart.AddSettings2'))
        }
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info("AddSettings1 fionished")
        }
    }

    static File buildFile(SharedFolderSettingsEnum s) {
        assert SharedFolderSettingsGeneral.baseDir != null
        return new File(SharedFolderSettingsGeneral.baseDir, s.childPath)
    }


}
