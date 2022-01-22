package net.sf.jremoterun.utilities.nonjdk.ziputil.unzrchivecommon

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.IOUtils;

import java.util.logging.Logger;

/**
 * @see org.apache.commons.compress.archivers.examples.Expander
 */
@CompileStatic
class UnarchiveCommon {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ArchiveInputStream archiveInputStream;
    public File destDir;
    public AcceptRejectFilterCommon acceptRejectFilterUnarchive
    public int skippedFiles = 0
    public int writtenFiles = 0;
    public int uptodateFiles = 0;
    public int writtenDirs = 0;
    public List<String> errorEntries;
    public String removePrefix
    public boolean writeOnlyIfChangesOfNew = true
    public boolean copyModifiedDateFromSource = true

    public long writtenBytes = 0

    UnarchiveCommon(File srcArchive, File destDir, String extractSubPath) {
        this.archiveInputStream = createInputStream(srcArchive)
        this.destDir = destDir
        this.acceptRejectFilterUnarchive = new AcceptRejectFilterCommon([], null);
        if (extractSubPath != null) {
            this.acceptRejectFilterUnarchive.startWithClasses.add(extractSubPath)
        }
        removePrefix = extractSubPath
    }

    UnarchiveCommon(File srcArchive, File destDir, AcceptRejectFilterCommon acceptRejectFilterUnarchive) {
        this.archiveInputStream = createInputStream(srcArchive)
        this.destDir = destDir
        this.acceptRejectFilterUnarchive = acceptRejectFilterUnarchive
    }

    UnarchiveCommon(ArchiveInputStream archiveInputStream, File destDir, AcceptRejectFilterCommon acceptRejectFilterUnarchive) {
        this.archiveInputStream = archiveInputStream
        this.destDir = destDir
        this.acceptRejectFilterUnarchive = acceptRejectFilterUnarchive
    }

    /**
     * @see org.apache.commons.compress.archivers.ArchiveStreamFactory#detect
     */
    ArchiveInputStream createInputStream(File f) {
        assert f.exists()
        BufferedInputStream stream1 = f.newInputStream()
        try {
            String name1 = f.getName()
            return createArchiveInputStream(name1, stream1)
        } catch (Throwable e) {
            JrrIoUtils.closeQuietly2(stream1, log)
            throw e
        }
    }

    public JrrArchiveType jrrArchiveTypeOverride

    ArchiveInputStream createArchiveInputStream(String name1, InputStream stream1) {
        if (jrrArchiveTypeOverride != null) {
            return createArchiveInputStream4(jrrArchiveTypeOverride, stream1)
        }
        JrrArchiveType jrrArchiveType = detectArchiveType(name1)
        return createArchiveInputStream4(jrrArchiveType, stream1)
    }

    JrrArchiveType detectArchiveType(String name1) {
        if (name1.endsWith('.tar.gz')) {
            return JrrArchiveType.tarGz
        }
        if (name1.endsWith('.zip')) {
            return JrrArchiveType.zip
        }
        if (name1.endsWith('.jar')) {
            return JrrArchiveType.zip
        }

        throw new UnsupportedOperationException("${name1}")
    }

    ArchiveInputStream createArchiveInputStream4(JrrArchiveType jrrArchiveType, InputStream stream1) {
        switch (jrrArchiveType) {
            case JrrArchiveType.tarGz:
                return new TarArchiveInputStream(new GzipCompressorInputStream(stream1))
            case JrrArchiveType.zip:
                return new ZipArchiveInputStream(stream1)
            default:
                throw new UnsupportedOperationException("${jrrArchiveType}")
        }
    }


    boolean acceptFile(ArchiveEntry nextEntry, File destFile) {
        if (writeOnlyIfChangesOfNew) {
            if (!destFile.exists()) {
                return true
            }
            if (nextEntry.getSize() != destFile.length()) {
                return true
            }
            Date modifiedDate1 = nextEntry.getLastModifiedDate()
            if (modifiedDate1 == null) {
                log.info("modifed date is null for ${nextEntry.getName()}")
                return true
            }

            if (destFile.lastModified() != modifiedDate1.getTime()) {
                return true
            }
            return false
        }
        return true
    }

    boolean acceptEntry(ArchiveEntry nextEntry) {
        if (acceptRejectFilterUnarchive == null) {
            return true
        }
        String relativePath = nextEntry.getName();
        if (nextEntry.isDirectory()) {
            return false
        }
        return acceptRejectFilterUnarchive.isAccept3(relativePath,false)
    }

    void doJob() {
        try {
            while (true) {
                ArchiveEntry nextEntry = archiveInputStream.getNextEntry()
                if (nextEntry == null) {
                    break
                }
                boolean accept1 = acceptEntry(nextEntry)
                if (accept1) {
                    writeEntry(nextEntry)
                } else {
                    if (nextEntry.isDirectory()) {

                    } else {
                        skippedFiles++
                    }
                }

            }
            onFinish()
        } finally {
            onFinally()
        }
    }

    void writeEntry(ArchiveEntry nextEntry) {
        String entryName = mapEntry(nextEntry)
        File destFile = new File(destDir, entryName)

        try {
            if (nextEntry.isDirectory()) {
                writeDir(destFile)
                writtenDirs++
            } else {
                boolean b = acceptFile(nextEntry, destFile)
                if (b) {
                    writeFile(destFile, nextEntry)
                    writtenFiles++
                } else {
                    skippedFiles++
                    uptodateFiles++
                }
            }
        } catch (Throwable e) {
            onError(nextEntry, destFile, e)
        }
    }

    void onFinally() {
        JrrIoUtils.closeQuietly2(archiveInputStream, log)
    }

    void writeDir(File destFile) {
        destFile.mkdirs()
        assert destFile.exists()
    }

    String mapEntry(ArchiveEntry nextEntry) {

        String name1 = nextEntry.getName()
        if (removePrefix == null) {
            return name1
        }
        if (name1.startsWith(removePrefix)) {
            return name1.substring(removePrefix.length())
        }
        return name1
    }

    void onError(ArchiveEntry nextEntry, File f, Throwable e) {
        log.info("failed on ${nextEntry.getName()}")
        errorEntries.add(nextEntry.getName())
        throw e
    }


    void writeFile(File destFile, ArchiveEntry nextEntry) {
        File parentFile = destFile.getParentFile()
        parentFile.mkdirs()
        assert parentFile.exists()
        BufferedOutputStream outputStream = destFile.newOutputStream()
        try {
            IOUtils.copy(archiveInputStream, outputStream)
            writtenBytes += destFile.length()
            if (copyModifiedDateFromSource) {
                Date date1 = nextEntry.getLastModifiedDate()
                if (date1 != null) {
                    long time1 = date1.getTime()
                    if (time1 > 0) {
                        destFile.setLastModified(time1)
                    }
                }
            }
        } finally {
            JrrIoUtils.closeQuietly2(outputStream, log)
        }
    }


    void onFinish() {
        if (writtenFiles == 0) {
            if (uptodateFiles == 0) {
                throw new Exception("No files written")
            }
        }
    }

}
