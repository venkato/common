package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrCommonsArchiver
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveInputStream
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream
import org.apache.commons.io.IOUtils;

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class UnarchiveTextRef {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ToFileRef2 toFileRef;
    public String pathInArchive;

    UnarchiveTextRef(File toFileRef, String pathInArchive) {
        this(new FileToFileRef(toFileRef),pathInArchive)
    }

    UnarchiveTextRef(ToFileRef2 toFileRef, String pathInArchive) {
        this.toFileRef = toFileRef
        this.pathInArchive = pathInArchive
    }

    String resolveText() {
        byte[] bytes = resolveImpl()
        return new String(bytes)
    }


    byte[] resolveImpl() {
        UnarchiveTextRefImpl unarchiveTextRef = new UnarchiveTextRefImpl(toFileRef.resolveToFile(), pathInArchive)
        return unarchiveTextRef.resolveImpl()
    }
}
