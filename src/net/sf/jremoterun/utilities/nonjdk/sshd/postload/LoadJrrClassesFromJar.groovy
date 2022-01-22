package net.sf.jremoterun.utilities.nonjdk.sshd.postload

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.LoadSpecificClassesFromJar
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.sftploader.JrrDownloadFilesLayout
import net.sf.jremoterun.utilities.nonjdk.sftploader.settings.SftpConnectionSettings;

import java.util.logging.Logger;

@CompileStatic
class LoadJrrClassesFromJar implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public LoadSpecificClassesFromJar classesFromJar = new LoadSpecificClassesFromJar()
    public SftpConnectionSettings settings = SftpConnectionSettings.settings
    File firstdownloadCopyDir = new File(settings.copyDir, JrrDownloadFilesLayout.firstdownload.customName)

    public static LoadJrrClassesFromJar loadJrrClassesFromJar

    LoadJrrClassesFromJar() {
        loadJrrClassesFromJar = this
    }

    @Override
    void run() {
        loadJrrClasses()
    }

    void loadJrrClasses() {
        classesFromJar.loadClassesFromJar2(new File(firstdownloadCopyDir, JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef.customName))
        loadFromFiles(JrrStarterJarRefs2.jremoterun)
        loadFromFiles(JrrStarterJarRefs2.jrrassist)
        loadFromFiles(JrrStarterJarRefs2.jrrbasetypes)
        loadFromFiles(JrrStarterJarRefs2.java11base)
        classesFromJar.printStat()
    }

    void loadFromFiles(JrrStarterJarRefs2 ref) {
        classesFromJar.loadClassesFromJar2(new File(firstdownloadCopyDir, ref.ref.child.approximatedName()))
    }

}
