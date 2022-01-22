import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.classpath.*;
import net.sf.jremoterun.utilities.groovystarter.*
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory;

@CompileStatic
class sshRemoteDownloader implements Runnable {

    private ClRef customRunClass = new ClRef('changeMe')
    private List args1 = []

    @Override
    void run() {
        doImpl()
    }

    private void doImpl() {
        GroovyMethodRunnerParams.gmrpn.addL(JrrRunnerPhase.checks, false, this.&bcs);
    }


    private void bcs() {
        //GroovyMethodRunnerParams gmrp = GroovyMethodRunnerParams.gmrp
        File parentFile1 = GroovyMethodRunnerParams.gmrpn.grHome.parentFile
        assert parentFile1.getName() == 'copy'
        File parentFile2 = GroovyMethodRunnerParams.gmrpn.grHome.parentFile.parentFile
        assert parentFile2.exists()
        File preloadedLibsOrigin = new File(parentFile2, 'origin/preloadedLibs')
        File preloadedLibsCopy = new File(parentFile2, 'copy/preloadedLibs')
        if (preloadedLibsOrigin.exists()) {
            preloadedLibsOrigin.listFiles().toList().each {
                File destFile = new File(preloadedLibsCopy, it.getName())
                copyFileIfNeeded(it, destFile);
            }
        }
        assert preloadedLibsCopy.exists()
        GroovyMethodRunnerParams.gmrpn.addFilesToClassLoader.addAllJarsInDir(preloadedLibsCopy)
        ClRef someAuxCommonClass = new ClRef('net.sf.jremoterun.utilities.nonjdk.sftploader.CommonSftpRunner')
        List args2 = [customRunClass, args1]
        RunnableWithParamsFactory.fromClass4(someAuxCommonClass, args2)
    }


    private void copyFileIfNeeded(File from, File to) {
        if (isNeedCopyFile(from, to)) {
            to.bytes = from.bytes
            to.setLastModified(from.lastModified())
        }
    }

    private boolean isNeedCopyFile(File src, File dest) {
        if (!dest.exists()) {
            return true
        }
        if (src.length() != dest.length()) {
            return true
        }
        if (src.lastModified() != dest.lastModified()) {
            return true
        }
        return false
    }

}


