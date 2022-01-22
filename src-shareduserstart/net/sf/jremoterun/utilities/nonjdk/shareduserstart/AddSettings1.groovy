package net.sf.jremoterun.utilities.nonjdk.shareduserstart

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.FileScriptSource
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2ParamsI
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.JrrRunnerPhase
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.CallerInfo
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.CallerInfoGetter
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode

import java.util.logging.Logger;

@CompileStatic
class AddSettings1  extends InjectedCode implements CallerInfo, CallerInfoGetter  {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Object callerInfo;




    @Override
    Object get(Object key) {
        assert key!=null
        List args= key as List
        assert args.size()>1
        Object doDebug=  getObjectGetArg(1,args)
        assert doDebug instanceof Boolean
        SharedFolderSettingsGeneral.doDebugLogging=doDebug
        SharedFolderSettingsGeneral.callerInfo= getObjectGetArg(2,args) as FileScriptSource
        addImpl( getObjectGetArg(0,args) as File)
        return null
    }

    Object getObjectGetArg(int id,List list){
        if(list.size()<=id){
            throw new Exception("args size ${list.size()} needed arg ${id}")
        }
        return list.get(id)
    }


    void addImpl(File baseDir1) {
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info('inside AddSettings1')
        }
        AddFilesToClassLoaderGroovy adder = GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader
        SharedFolderSettingsGeneral.baseDir = baseDir1
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info "base dir = ${SharedFolderSettingsGeneral.baseDir}"
        }
        assert SharedFolderSettingsGeneral.baseDir!=null
        assert SharedFolderSettingsGeneral.baseDir.exists()
        String arg3 = net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator22.getFirstParam()
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
            net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator22.removeFirstParam()
            MavenDefaultSettings.mavenDefaultSettings.grapeFileFinder.setMavenLocalDir2(BaseDirAdded.buildFile(SharedFolderSettingsEnum.ivyCacheDir))
            MavenDefaultSettings.mavenDefaultSettings.jrrDownloadDir = BaseDirAdded.buildFile(SharedFolderSettingsEnum.jrrDownloadDir)
            GroovyMethodRunnerParams.gmrpn.addL(JrrRunnerPhase.checks, false, new ClRef('net.sf.jremoterun.utilities.nonjdk.shareduserstart.AddSettings2'))
        }else{
            if(SharedFolderSettingsGeneral.doDebugLogging) {
                log.info "add param=${ SharedFolderSettingsGeneral.addiffS} to get default methods"
            }
        }
        if(SharedFolderSettingsGeneral.doDebugLogging) {
            log.info("AddSettings1 fionished")
        }
    }

}
