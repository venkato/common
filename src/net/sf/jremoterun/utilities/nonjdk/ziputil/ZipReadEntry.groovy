package net.sf.jremoterun.utilities.nonjdk.ziputil

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrArchiveSettings
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.apache.commons.io.IOUtils;

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class ZipReadEntry {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static byte[] extractOneEntry(File zipArchive, String entryName) {
        assert zipArchive.exists()
        BufferedInputStream inputStream = zipArchive.newInputStream()
        try {
            ZipInputStream zipInputStream = new ZipInputStream(inputStream)
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry();
                if (nextEntry == null) {
                    break
                }
                if (nextEntry.getName() == entryName) {
                    //zipInputStream.re
                    return IOUtils.toByteArray(zipInputStream);
                }
            }
            //throw new Exception("Entry not found ${entryName}")
            return null
        } finally {
            JrrIoUtils.closeQuietly2(inputStream, log)
        }
    }

}
