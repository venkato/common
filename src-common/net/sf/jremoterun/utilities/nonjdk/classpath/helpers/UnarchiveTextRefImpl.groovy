package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

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

@CompileStatic
class UnarchiveTextRefImpl {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public File file1
    public String pathInArchive;
    public HashSet<String> available = new HashSet<>()
    public ArchiveInputStream stream
    public ArchiveEntry entryFound
    public ByteArrayOutputStream ou1 = new ByteArrayOutputStream()

    UnarchiveTextRefImpl(File file1, String pathInArchive) {
        this.file1 = file1
        this.pathInArchive = pathInArchive
    }

    byte[] resolveImpl() {
        stream = createInputStream()
        try {
            while (true) {
                ArchiveEntry entry1 = getNextEntry()
                if (entry1 == null) {
                    break
                }
                handleEntry(entry1)
            }
            if (entryFound == null) {
                return nothingFound()
            }
        } finally {
            closeStream()
        }
        return ou1.toByteArray()
    }

    boolean handleEntry(ArchiveEntry entry1) {
        if (!entry1.isDirectory()) {
            available.add(entry1.getName())
        }
        if (isAcceptEntry(entry1)) {
            if (entryFound == null) {
                copyEntry(entryFound)
                entryFound = entry1
            } else {
                throw new Exception("found many entries ${entryFound.getName()} and ${entry1.getName()}")
            }
        }

    }

    void copyEntry(ArchiveEntry entryFound1) {
        IOUtils.copy(stream, ou1)
    }

    void closeStream() {
        if (stream != null) {
            JrrIoUtils.closeQuietly2(stream, log)
        }
    }

    byte[] nothingFound() {
        throw new Exception("No entry found ${pathInArchive}, available = ${available.sort().join(', ')}")
    }

    ArchiveEntry getNextEntry() {
        return stream.getNextEntry()
    }


    boolean isAcceptEntry(ArchiveEntry entry1) {
        return entry1.getName() == pathInArchive
    }


    ArchiveInputStream createInputStream() {
        assert file1.exists()
        BufferedInputStream stream1 = file1.newInputStream()
        try {
            String name1 = file1.getName()
            return createArchiveInputStream(name1, stream1)
        } catch (Throwable e) {
            JrrIoUtils.closeQuietly2(stream1, log)
            throw e
        }
    }

    ArchiveInputStream createArchiveInputStream(String name1, InputStream stream1) {
        if (name1.endsWith('.tar.gz')) {
            return new TarArchiveInputStream(new GzipCompressorInputStream(stream1))
        }
        if (name1.endsWith('.zip')) {
            return new ZipArchiveInputStream(stream1)
        }
        if (name1.endsWith('.jar')) {
            return new ZipArchiveInputStream(stream1)
        }
        throw new UnsupportedOperationException()
    }


}
