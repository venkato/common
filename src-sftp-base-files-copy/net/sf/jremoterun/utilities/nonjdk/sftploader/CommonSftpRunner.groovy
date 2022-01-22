package net.sf.jremoterun.utilities.nonjdk.sftploader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.sftploader.settings.SftpConnectionSettings;

import java.util.logging.Logger;

@CompileStatic
class CommonSftpRunner extends InjectedCode{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object get(Object key0) throws Exception {
        if(key0==null){
            throw new NullPointerException('key is null')
        }
        if (!(key0 instanceof List)) {
            throw new IllegalArgumentException("key is not a list ${key0.getClass().getName()} ${key0}")

        }
        List key1 = key0 as List
        assert key1.size()>1
        ClRef clName = clName1(key1[0] )
        List key2 = key1[1] as List
        runCl(clName,key2)
        return null
    }

    ClRef clName1(Object obj){
        if (obj instanceof ClRef) {
            return  (ClRef) obj;
        }
        String s= obj
        return new ClRef(s)
    }


    private void runCl(ClRef ref11,List args){
        RunnableWithParamsFactory.fromClass4(ref11,args)
        SftpLoader sftpLoader=new SftpLoader();
        File copyDir =GroovyMethodRunnerParams.gmrpn.grHome.parentFile
        assert copyDir.getName() == JrrDownloadFilesLayout.copy.customName
//        CopyAndUnpack2 copyAndUnpack2 = new CopyAndUnpack2()

        File originDir =new File(copyDir.getParentFile(),JrrDownloadFilesLayout.origin.customName)
        if(!originDir.exists()){
            originDir.mkdir()
            assert originDir.exists()
        }
//        File privateKey = new File(copyDir,JrrDownloadFilesLayout.privateKey.customName)
//        assert privateKey.exists()
//        copyAndUnpack2.loaddd(privateKey)
        assert SftpConnectionSettings.settings!=null
        SftpConnectionSettings.settings.originDir=originDir
        SftpConnectionSettings.settings.copyDir=copyDir
        SftpConnectionSettings.settings.sftpLoader=sftpLoader
        sftpLoader.doLogging = SftpConnectionSettings.settings.doLogging
        sftpLoader.prepare(SftpConnectionSettings.settings.host,SftpConnectionSettings.settings.port)
        assert SftpConnectionSettings.settings.password!=null
        sftpLoader.sshClient.authPassword(SftpConnectionSettings.settings.user,SftpConnectionSettings.settings.password)
//        sftpLoader.doJob1(originDir)


        CopyAndUnpack copyAndUnpack = new CopyAndUnpack(sftpLoader)
        SftpConnectionSettings.settings.copyAndUnpack=copyAndUnpack
        copyAndUnpack.doJobAll(copyDir.getParentFile())
        File otherLibsF = new File(copyDir,JrrDownloadFilesLayout.otherLibs.customName)
        if(otherLibsF.listFiles().length!=0) {
            GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader.addAllJarsInDirAndSubdirsDeep(otherLibsF)
        }
        RunnableFactory.runRunner(SftpConnectionSettings.settings.clRefAfterSshDownload)

    }
}
