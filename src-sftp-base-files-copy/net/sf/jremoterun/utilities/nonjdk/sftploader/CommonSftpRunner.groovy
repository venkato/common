package net.sf.jremoterun.utilities.nonjdk.sftploader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.sftploader.settings.SftpConnectionSettings;

import java.util.logging.Logger;

@CompileStatic
class CommonSftpRunner extends InjectedCode{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object get(Object key) throws Exception {
        runCl((String)key)
        return null
    }


    private void runCl(String clName){
        ClRef ref11 = new ClRef(clName)
        RunnableFactory.runRunner(ref11)
        SftpLoader sftpLoader=new SftpLoader();
        File copyDir =GroovyMethodRunnerParams.gmrp.grHome.parentFile
        assert copyDir.getName() == JrrDownloadFilesLayout.copy.customName
//        CopyAndUnpack2 copyAndUnpack2 = new CopyAndUnpack2()

        File originDir =new File(copyDir.getParentFile(),JrrDownloadFilesLayout.origin.customName)
        if(!originDir.exists()){
            originDir.mkdir()
            assert originDir.exists()
        }
        File privateKey = new File(copyDir,JrrDownloadFilesLayout.privateKey.customName)
        assert privateKey.exists()
//        copyAndUnpack2.loaddd(privateKey)
        assert SftpConnectionSettings.settings!=null
        SftpConnectionSettings.settings.originDir=originDir
        SftpConnectionSettings.settings.copyDir=copyDir
        SftpConnectionSettings.settings.sftpLoader=sftpLoader
        sftpLoader.doLogging = SftpConnectionSettings.settings.doLogging
        sftpLoader.prepare(SftpConnectionSettings.settings.host,SftpConnectionSettings.settings.port)
        sftpLoader.sshClient.authPassword(SftpConnectionSettings.settings.user,privateKey.getAbsolutePath())
//        sftpLoader.doJob1(originDir)


        CopyAndUnpack copyAndUnpack = new CopyAndUnpack(sftpLoader)
        SftpConnectionSettings.settings.copyAndUnpack=copyAndUnpack
        copyAndUnpack.doJobAll(copyDir.getParentFile())
        RunnableFactory.runRunner(SftpConnectionSettings.settings.clRefAfterSshDownload)
    }
}
