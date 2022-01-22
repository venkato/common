package net.sf.jremoterun.utilities.nonjdk.sshd.postload

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.LoadSpecificClassesFromJar
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.sftploader.JrrDownloadFilesLayout
import net.sf.jremoterun.utilities.nonjdk.sftploader.LinuxExecPermissions
import net.sf.jremoterun.utilities.nonjdk.sftploader.settings.SftpConnectionSettings

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission;
import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class UnpackFirstDownloadZip implements Runnable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SftpConnectionSettings settings = SftpConnectionSettings.settings


    @Override
    void run() {
        unpackIfNeeded()
    }




    boolean isUnpackNeeded() {
        File firstdownloadSrcF = new File(settings.originDir, JrrDownloadFilesLayout.firstdownload_zip.customName)
        //File firstdownloadDestF = copyDir1
//        File firstdownloadDestF = new File(copyDir1, JrrDownloadFilesLayout.firstdownload.customName)
        assert firstdownloadSrcF.exists()
        boolean needUnpackGr = false
        if (!new File(settings.copyDir, JrrDownloadFilesLayout.firstdownload.customName).exists()) {
            needUnpackGr = true
        }
        if (settings.sftpLoader.downloadedFiles2.contains(firstdownloadSrcF)) {
            needUnpackGr = true
        }
        return needUnpackGr
    }


    void unpackIfNeeded() {
        if (isUnpackNeeded()) {
            RunnableFactory.runRunner new ClRef('net.sf.jremoterun.utilities.nonjdk.sshd.postload.LoadJrrClassesFromJar')
            File firstdownloadSrcF = new File(settings.originDir, JrrDownloadFilesLayout.firstdownload_zip.customName)
            firstdownloadSrcF.mkdir()
            unpackZip(firstdownloadSrcF, settings.copyDir)
        }
    }


    void unpackZip(File zipF, File unpackTo) {
        assert unpackTo.exists()
        BufferedInputStream inputStream1 = zipF.newInputStream()
        ZipInputStream zipInputStream = new ZipInputStream(inputStream1)
        try {
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry()
                if (nextEntry == null) {
                    break
                }
                if (nextEntry.isDirectory()) {

                } else {
                    if (SftpConnectionSettings.settings.doLogging) {
                        log.info "hanlding ${nextEntry.getName()}"
                    }
                    try {
                        if (!nextEntry.getName().startsWith('META-INF/')) {
                            handleEntry(nextEntry, zipInputStream, unpackTo)
                        }
                    } catch (Throwable e) {
                        log.info("failed handle ${nextEntry.getName()}")
                        throw e
                    }
                }
                zipInputStream.closeEntry()
            }
        } finally {
            JrrIoUtils.closeQuietly(zipInputStream)
            JrrIoUtils.closeQuietly(inputStream1)
        }
    }

    void handleEntry(ZipEntry nextEntry, ZipInputStream zipInputStream, File unpackTo) {
        File destf = new File(unpackTo, nextEntry.getName())
        File parentFile = destf.getParentFile()
        parentFile.mkdir()
        assert parentFile.exists()
        boolean needCopy1 = isNeedCopyFile(nextEntry, destf)
        if (SftpConnectionSettings.settings.doLogging) {
            log.info "copy ? ${needCopy1} ${nextEntry.getName()}"
        }
        if (needCopy1) {
            FileOutputStream fos = new FileOutputStream(destf)
            org.apache.commons.io.IOUtils.copy(zipInputStream, fos)
            fos.close()
//            destf.bytes = bytes1
            destf.setLastModified(nextEntry.getTime())
            LinuxExecPermissions.linuxExecPermissions.setExecPermissions(destf)
            settings.unzipedEntries.add(nextEntry.getName())
        }
    }


    boolean isNeedCopyFile(ZipEntry src, File dest) {
        if (!dest.exists()) {
            return true
        }
        if (src.getSize() != dest.length()) {
            return true
        }
        if (src.getTime() != dest.lastModified()) {
            return true
        }
        return false
    }

}
