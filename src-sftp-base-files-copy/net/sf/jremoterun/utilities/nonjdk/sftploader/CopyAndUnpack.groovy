package net.sf.jremoterun.utilities.nonjdk.sftploader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.sftploader.settings.SftpConnectionSettings
import org.apache.commons.io.FileUtils;

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class CopyAndUnpack {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public SftpLoader sftpLoader;


    public List<String> unzipedEntries = [];
    public List<File> copiedFiles = [];
// mkdir copy
// cd copy
// sftp -P 12572 -r 127.0.0.1
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
        File origin = new File(baseDir, JrrDownloadFilesLayout.origin.customName)
        File copy1 = new File(baseDir, JrrDownloadFilesLayout.copy.customName)
        sftpLoader.doJob1(origin)
        File otherLibsF = new File(origin, JrrDownloadFilesLayout.otherLibs.customName)
        assert otherLibsF.exists()
        copyDirOrFile(otherLibsF, copy1)
        File firstdownloadSrcF = new File(origin, JrrDownloadFilesLayout.firstdownload_zip.customName)
        File firstdownloadDestF = copy1
//        File firstdownloadDestF = new File(copy1, JrrDownloadFilesLayout.firstdownload.customName)
        assert firstdownloadSrcF.exists()
        boolean needUnpackGr = false
        if (!new File(copy1, JrrDownloadFilesLayout.firstdownload.customName).exists()) {
            needUnpackGr = true
        }
        if (sftpLoader.downloadedFiles2.contains(firstdownloadSrcF)) {
            needUnpackGr = true
        }
        if (needUnpackGr) {
            firstdownloadSrcF.mkdir()
            unpackZip(firstdownloadSrcF, firstdownloadDestF)
        }
    }

    void unpackZip(File zipF, File unpackTo) {
        assert unpackTo.exists()
        BufferedInputStream inputStream1 = zipF.newInputStream()
        ZipInputStream zipInputStream = new ZipInputStream(inputStream1)
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
                    handleEntry(nextEntry, zipInputStream, unpackTo)

                } catch (Throwable e) {
                    log.info("failed handle ${nextEntry.getName()}")
                    throw e
                }
            }
            zipInputStream.closeEntry()
        }

        zipInputStream.close()
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
            unzipedEntries.add(nextEntry.getName())
        }
    }


    void copyDirOrFile(File src, File dest) {
        assert dest.exists()
        File dest2 = new File(dest, src.getName())
        if (src.isFile()) {
            boolean needed = isNeedCopyFile(src, dest2)
            if (needed) {
                FileUtils.copyFile(src,dest)
                dest2.setLastModified(src.lastModified())
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

    }

    boolean isNeedCopyFile(File src, File dest) {
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
