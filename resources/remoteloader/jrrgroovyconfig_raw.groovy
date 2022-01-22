import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.classpath.*;
import net.sf.jremoterun.utilities.groovystarter.*
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory;

@CompileStatic
class sshRemoteDownloader extends GroovyRunnerConfigurator2 {

    private ClRef customRunClass = new ClRef('changeMe')

    @Override
    void doConfig() {
        doImpl()
    }

    private void doImpl() {
        GroovyMethodRunnerParams gmrp = GroovyMethodRunnerParams.instance
        gmrp.addL(JrrRunnerPhase.checks, false, this.&bcs);
    }


    private void bcs() {
        GroovyMethodRunnerParams gmrp = GroovyMethodRunnerParams.instance
        File parentFile1 = gmrp.grHome.parentFile
        assert parentFile1.getName() == 'copy'
        File parentFile2 = gmrp.grHome.parentFile.parentFile
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
        gmrp.addFilesToClassLoader.addAllJarsInDir(preloadedLibsCopy)
        ClRef someAuxCommonClass = new ClRef('net.sf.jremoterun.utilities.nonjdk.sftploader.CommonSftpRunner')

        RunnableWithParamsFactory.fromClass4(someAuxCommonClass, customRunClass.className)
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


