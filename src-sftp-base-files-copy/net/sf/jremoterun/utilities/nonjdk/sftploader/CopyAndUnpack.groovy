package net.sf.jremoterun.utilities.nonjdk.sftploader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.JrrStarterConstatnts
import net.sf.jremoterun.utilities.nonjdk.sftploader.settings.SftpConnectionSettings
import org.apache.commons.io.FileUtils

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermissions;
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class CopyAndUnpack {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public SftpLoader sftpLoader;

    public List<File> copiedFiles = [];
// mkdir copy
// cd copy
// sftp -o StrictHostKeyChecking=no  -o UserKnownHostsFile=/dev/null  -P 12572 127.0.0.1
// cd jrrfiles
// get .
// exit
// unzip firstdownload.zip
//     firstdownload\firstdownload\gr.bat
    CopyAndUnpack(SftpLoader sftpLoader) {
        this.sftpLoader = sftpLoader
    }

    void doJobAll(File baseDir) {
        assert sftpLoader != null
        File originDir = new File(baseDir, JrrDownloadFilesLayout.origin.customName)
        final File copyDir1 = new File(baseDir, JrrDownloadFilesLayout.copy.customName)
        sftpLoader.doJob1(originDir)

        File grHomCustomConfig = new File(originDir, JrrStarterConstatnts.rawConfiGrHomeFileName)
        copyDirOrFile(grHomCustomConfig,copyDir1)

        File nonJarsF = new File(originDir, JrrDownloadFilesLayout.nonJars.customName)
        if (nonJarsF.exists()) {
            copyDirOrFile(nonJarsF, copyDir1)
        }


        File otherLibsF = new File(originDir, JrrDownloadFilesLayout.otherLibs.customName)
        assert otherLibsF.exists()
        copyDirOrFile(otherLibsF, copyDir1)

    }


    void copyDirOrFile(File src, File dest) {
        assert dest.exists()
        File dest2 = new File(dest, src.getName())
        try {
            if (src.isFile()) {
                boolean needed = isNeedCopyFile(src, dest2)
                if (needed) {
                    FileUtils.copyFile(src, dest2)
                    dest2.setLastModified(src.lastModified())
                    LinuxExecPermissions.linuxExecPermissions.setExecPermissions(dest2)
                    copiedFiles.add(src)
                }
            } else {
                assert src.isDirectory()
                dest2.mkdir()
                assert dest2.exists()

                List<File> list1 = src.listFiles().toList()
                list1.each {
                    copyDirOrFile(it, dest2)
                }
            }
        } catch (Throwable e) {
            log.info("failed copy ${src} to ${dest2} ${e}")
            throw e
        }
    }

    public int maxBytesCompareAllBytes = 10_000

    boolean isNeedCopyFile(File src, File dest) {
        if (!dest.exists()) {
            return true
        }
        long lengthSrc = src.length()
        if (lengthSrc != dest.length()) {
            return true
        }
        if(lengthSrc<maxBytesCompareAllBytes){
            byte[] bytesSrc = src.bytes
            byte[] bytesDest = dest.bytes
            boolean equalsBytes=Arrays.equals(bytesSrc,bytesDest)
            return !equalsBytes
        }
        if (src.lastModified() != dest.lastModified()) {
            return true
        }
        if (sftpLoader.downloadedFiles2.contains(src)){
            return true
        }
        return false
    }

}
