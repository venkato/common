package net.sf.jremoterun.utilities.nonjdk.sshd

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.archiver.JarAcceptRejectFilter
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrCommonsArchiver
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesFirstDownload
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesSrc
import net.sf.jremoterun.utilities.nonjdk.compile.SftpLoaderCompiler
import net.sf.jremoterun.utilities.nonjdk.sftploader.JrrDownloadFilesLayout
import org.apache.commons.io.FileUtils
import org.rauschig.jarchivelib.ArchiveFormat;

import java.util.logging.Logger;

@CompileStatic
class JrrBuildCreator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    public List<JrrStarterOsSpecificFiles> jrrStarterOsSpecificFilesss = [JrrStarterOsSpecificFiles.originDir,
//                                                                   JrrStarterOsSpecificFiles.log4j2_config,
                                                                          JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef,
    ]

    public List<JrrStarterOsSpecificFilesFirstDownload> firstDownloads = JrrStarterOsSpecificFilesFirstDownload.values().toList();

    public File saveToDir

    public JarAcceptRejectFilter acceptRejectFilter = new JarAcceptRejectFilter([], []){
        @Override
        String map(String relativePath, File file, JrrCommonsArchiver archiver) {
            String s = super.map(relativePath, file, archiver)
            if(s==null){
                return s
            }
            return JrrDownloadFilesLayout.firstdownload.customName+'/'+s
        }
    }

    public JrrJarArchiver commonsArchiver = new JrrJarArchiver(ArchiveFormat.ZIP)

    public SftpLoaderCompiler sftpLoaderCompiler = new SftpLoaderCompiler()

    JrrBuildCreator(File saveToDir) {
        this.saveToDir = saveToDir
    }



    void doJob() {
        File jrrfilesDir = new File(saveToDir, JrrDownloadFilesLayout.jrrfiles.customName)
        jrrfilesDir.mkdir()

        List<ExactChildPattern> filesToAdd = getFilesToAdd()
        filesToAdd.each {
            addExactChildPattern(it)
        }
        commonsArchiver.archiveAcceptRejectI = acceptRejectFilter
        try {
            commonsArchiver.step1(new File(jrrfilesDir, JrrDownloadFilesLayout.firstdownload_zip.customName), true)
            commonsArchiver.step2(GitSomeRefs.starter.resolveToFile())
            commonsArchiver.stepFinish()
        } finally {
            commonsArchiver.stepFinalAlways()
        }
        sftpLoaderCompiler.all2()
        File preloadedLibsF = new File(jrrfilesDir, JrrDownloadFilesLayout.preloadedLibs.customName)
        preloadedLibsF.mkdir()
        File sftLoaderJar = new File(preloadedLibsF,JrrDownloadFilesLayout.sftpLoader_jar.customName)
        sftpLoaderCompiler.zipp(sftLoaderJar)
        List<JrrDownloadFilesLayout> createDirOnly = JrrDownloadFilesLayout.values().toList().findAll { it.createDirOnly }
        createDirOnly.each {
            new File(jrrfilesDir,it.customName).mkdir()
        }
        SftpLoaderCompiler.mavenIds.each {
            File file3 = it.m.resolveToFile()
            File destFile1 = new File(preloadedLibsF,file3.getName())
            FileUtils.copyFile(file3,destFile1)
        }

    }

    List<ExactChildPattern> getFilesToAdd() {
        List<ExactChildPattern> r = []
        r.addAll(firstDownloads.collect { it.ref })
        r.addAll(jrrStarterOsSpecificFilesss.collect { it.ref })
        r.add JrrStarterOsSpecificFilesSrc.JrrInit.getSrcPath()
        r.add JrrStarterOsSpecificFilesSrc.custom.getSrcPath()
        return r
    }


    void addExactChildPattern(ExactChildPattern adddd) {
        acceptRejectFilter.startWithClasses.add(adddd.child)
    }

}
