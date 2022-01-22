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
import net.sf.jremoterun.utilities.nonjdk.linux.EolConversion
import net.sf.jremoterun.utilities.nonjdk.linux.LinuxEolTranslation
import net.sf.jremoterun.utilities.nonjdk.sftploader.JrrDownloadFilesLayout
import net.sf.jremoterun.utilities.nonjdk.sshsup.SftpUtils
import org.apache.commons.io.FileUtils
import org.rauschig.jarchivelib.ArchiveFormat;

import java.util.logging.Logger;

@CompileStatic
class JrrBuildCreator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JrrStarterFilesToCopy jrrStarterFilesToCopy = new JrrStarterFilesToCopy();

//    public List<JrrStarterOsSpecificFiles> jrrStarterOsSpecificFilesss = [JrrStarterOsSpecificFiles.originDir,
//                                                                          JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef,
//    ]

//    public List<JrrStarterOsSpecificFilesFirstDownload> firstDownloads = JrrStarterOsSpecificFilesFirstDownload.values().toList();

    public static long maxLengthShFiles = 20_000
    public File saveToDir

    public JrrJarArchiver commonsArchiver = new JrrJarArchiver(ArchiveFormat.ZIP){
        @Override
        InputStream createInputStreamForEntry(File fileEntry) {
            if(fileEntry.getName().endsWith('.sh')){
                assert fileEntry.length()<maxLengthShFiles
                String text1 = fileEntry.text
                text1= LinuxEolTranslation.translate(text1, EolConversion.linux);
                return new ByteArrayInputStream(text1.getBytes())
            }
            if(fileEntry.getName().endsWith('.bat')){
                assert fileEntry.length()<maxLengthShFiles
                String text1 = fileEntry.text
                text1= LinuxEolTranslation.translate(text1,EolConversion.windows);
                return new ByteArrayInputStream(text1.getBytes())
            }
            return super.createInputStreamForEntry(fileEntry)
        }
    }

    public SftpLoaderCompiler sftpLoaderCompiler = new SftpLoaderCompiler()

    public JarAcceptRejectFilter acceptRejectFilter = new JarAcceptRejectFilter([], []) {
        @Override
        String map(String relativePath, File file, JrrCommonsArchiver archiver) {
            String s = super.map(relativePath, file, archiver)
            if (s == null) {
                return s
            }
            return JrrDownloadFilesLayout.firstdownload.customName + '/' + s
        }
    }


    JrrBuildCreator(File saveToDir) {
        this.saveToDir = saveToDir
    }

    void uploadViaSftp(String dirPrefix, SftpUtils sftp) {
        uploadViaSftpS(dirPrefix, sftp, saveToDir, JrrDownloadFilesLayout.origin)
        uploadViaSftpS(dirPrefix, sftp, saveToDir, JrrDownloadFilesLayout.copy)
    }

    static void uploadViaSftpS(String remoteDirPrefix, SftpUtils sftp, File saveToDir1, JrrDownloadFilesLayout dir) {
        sftp.createDirSilent("${remoteDirPrefix}/${dir.customName}")
        List<String> ignoreFiles = []
        if (dir == JrrDownloadFilesLayout.copy) {
            ignoreFiles.add(JrrDownloadFilesLayout.firstdownload_zip.customName)
        }
        sftp.uploadFileOrDir(saveToDir1.child(JrrDownloadFilesLayout.jrrfiles.customName), "${remoteDirPrefix}/${dir.customName}", [])
    }


    void doJob() {
        File jrrfilesDir = new File(saveToDir, JrrDownloadFilesLayout.jrrfiles.customName)
        jrrfilesDir.mkdir()

        List<ExactChildPattern> filesToAdd = jrrStarterFilesToCopy.getFilesToAdd()
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
        File sftLoaderJar = new File(preloadedLibsF, JrrDownloadFilesLayout.sftpLoader_jar.customName)
        sftpLoaderCompiler.zipp(sftLoaderJar)
        List<JrrDownloadFilesLayout> createDirOnly = JrrDownloadFilesLayout.values().toList().findAll { it.createDirOnly }
        createDirOnly.each {
            new File(jrrfilesDir, it.customName).mkdir()
        }
        SftpLoaderCompiler.mavenIds.each {
            File file3 = it.m.resolveToFile()
            File destFile1 = new File(preloadedLibsF, file3.getName())
            FileUtils.copyFile(file3, destFile1)
        }

    }

//    List<ExactChildPattern> getFilesToAdd() {
//        List<ExactChildPattern> r = []
//        r.addAll(firstDownloads.collect { it.ref })
//        r.addAll(jrrStarterOsSpecificFilesss.collect { it.ref })
//        r.add JrrStarterOsSpecificFilesSrc.JrrInit.getSrcPath()
//        r.add JrrStarterOsSpecificFilesSrc.custom.getSrcPath()
//        return r
//    }


    void addExactChildPattern(ExactChildPattern adddd) {
        acceptRejectFilter.startWithClasses.add(adddd.child)
    }

}
