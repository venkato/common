package net.sf.jremoterun.utilities.nonjdk.archiver;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.zip.JarMarker;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * @see ArchiveStreamFactory#createArchiveOutputStream(String, OutputStream, String)
 */
@CompileStatic
class ZipArchiveOutputStreamJrr extends ZipArchiveOutputStream{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public boolean jarMarkerAddedNeeded = false;
    public boolean jarMarkerAdded = false ;

    public JrrCommonsArchiver archiver;

    public ZipArchiveOutputStreamJrr(OutputStream out, JrrCommonsArchiver archiver) {
        super(out);
        this.archiver = archiver;
    }

    public ZipArchiveOutputStreamJrr(File file, JrrCommonsArchiver archiver) throws IOException {
        super(file);
        this.archiver = archiver;
    }

    public ZipArchiveOutputStreamJrr(Path file, JrrCommonsArchiver archiver, OpenOption... options) throws IOException {
        super(file, options);
        this.archiver = archiver;
    }

    public ZipArchiveOutputStreamJrr(File file, long zipSplitSize, JrrCommonsArchiver archiver) throws IOException {
        super(file, zipSplitSize);
        this.archiver = archiver;
    }

    public ZipArchiveOutputStreamJrr(SeekableByteChannel channel, JrrCommonsArchiver archiver) throws IOException {
        super(channel);
        this.archiver = archiver;
    }


    // @throws ClassCastException if entry is not an instance of ZipArchiveEntry
    @Override
    public void putArchiveEntry(final ZipArchiveEntry ze) throws IOException {
        ZipArchiveEntry ze2 = ze as ZipArchiveEntry;
        if(jarMarkerAddedNeeded) {
            if (!jarMarkerAdded) {
                ze2.addAsFirstExtraField(JarMarker.getInstance());
                jarMarkerAdded = true;
            }
        }
        super.putArchiveEntry(ze2);
    }

    @Override
    public ZipArchiveEntry createArchiveEntry(File inputFile, String entryName) throws IOException {
        ZipArchiveEntry ze = (ZipArchiveEntry) super.createArchiveEntry(inputFile, entryName);
//        if (finished) {
//            throw new IOException("Stream has already been finished");
//        }
//        ZipArchiveEntry ze = new ZipArchiveEntry(inputFile, entryName);
        archiver.customizeZipArchiveEntry(ze);
        return ze;
    }


    @Override
    public ZipArchiveEntry createArchiveEntry(Path inputPath, String entryName, LinkOption... options) throws IOException {
        ZipArchiveEntry ze = (ZipArchiveEntry) super.createArchiveEntry(inputPath, entryName, options);
        archiver.customizeZipArchiveEntry(ze);
        return ze;
    }
}
