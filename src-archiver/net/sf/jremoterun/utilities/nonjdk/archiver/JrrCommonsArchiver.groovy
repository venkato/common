package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.FileInputStream2
import net.sf.jremoterun.utilities.FileOutputStream2;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.JavaBeanStore
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.CommonsArchiverOriginal
import org.rauschig.jarchivelib.IOUtils

import java.nio.file.attribute.FileTime
import java.security.MessageDigest
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@CompileStatic
class JrrCommonsArchiver extends CommonsArchiverOriginal {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Deprecated
    public static String archivePath = JrrArchiveSettings.archivePath;
    public int maxErrorFiles = 0;
    public long processedFiles = 0;
    public long uncompressedFileSize = 0;
    public boolean removeArchiveOnError = true;
    public boolean checkOnZeroFiles = true;
    public boolean createEntryForDir = true;
    public List<File> errorFiles = []
    public File destinationFile;
    public volatile File lastFile;
    public volatile File lastWrittenFile;
    public volatile boolean needStop = false;
    public ArchiveOutputStream outputStream;
    public File archiveFile
    public boolean checkOnDuplicates = true
    public Set<String> addedEntries = new HashSet<>()
    public volatile ArchiveAcceptRejectI archiveAcceptRejectI
    public String digestResult;

    public FileTime dateOfAllFiles;

    /**
     * @see org.apache.commons.codec.digest.DigestUtils
     */
    public MessageDigest digest;

    JrrCommonsArchiver(ArchiveFormat archiveFormat) {
        super(archiveFormat)
    }


    void writeMetaInfo(ArchiveInfo archiveInfo) {
        archiveInfo.creationDate = DateWithFormat.createCurrentDate()
        if (digest != null) {
            digestResult = org.apache.commons.codec.binary.Base64.encodeBase64String(digest.digest());
            archiveInfo.contentHash = digestResult
        }
        String info = new JavaBeanStore(archiveInfo).saveComplexObject(archiveInfo)
        createArchiveEntry(JrrArchiveSettings.archivePath, info)
    }

    void createDestinationStream(File dest) {
        if (outputStream != null) {
            throw new Exception("output stream already set")
        }
        archiveFile = dest
        outputStream = createArchiveOutputStream(archiveFile);
    }

    void addDir(File dir) {
        if (outputStream == null) {
            throw new Exception("output stream was not created")
        }
        File[] files = dir.listFiles()
        writeToArchive(dir, files, outputStream);
    }

    @Override
    File create(String archive, File destinationDir, File... sources) throws IOException {
        try {
            IOUtils.requireDirectory(destinationDir);
            archiveFile = createNewArchiveFile(archive, this.getFilenameExtension(), destinationDir);
            outputStream = createArchiveOutputStream(archiveFile);
            try {
                writeToArchive(sources, outputStream);
                outputStream.flush();
            } finally {
                closeOutStream()
            }
            postChecks()
            return archiveFile;
        } catch (Throwable e) {
            if (removeArchiveOnError && destinationFile != null) {
                destinationFile.delete()
            }
            throw e;
        }
    }

    void closeOutStream() {
        JrrIoUtils.closeQuietly2(outputStream, log);
    }


    void postChecks() {
        if (checkOnZeroFiles && processedFiles == 0) {
            throw new Exception('No files in archive')
        }
    }

    @Override
    protected File createNewArchiveFile(String archive, String extension, File destination) throws IOException {
        destinationFile = super.createNewArchiveFile(archive, extension, destination)
        return destinationFile;
    }




/**
 * @see org.apache.commons.compress.archivers.ArchiveStreamFactory#createArchiveOutputStream(String, OutputStream, String)
 */
    @Override
    public ArchiveOutputStream createArchiveOutputStream(File archiveFile) throws IOException {
        File parentDir1 = archiveFile.getParentFile()
        assert parentDir1.exists()
        if (getArchiveFormat() == ArchiveFormat.ZIP||getArchiveFormat() == ArchiveFormat.JAR||getArchiveFormat() == ArchiveFormat.TAR) {
            OutputStream fileOutputStream = createOutputStream(archiveFile)
            return createArchiveOutputStream1(fileOutputStream);
        }
        outputStream = super.createArchiveOutputStream(archiveFile)
        return outputStream;
    }

    public ArchiveOutputStream createArchiveOutputStream1(OutputStream fos) throws IOException {
        if (getArchiveFormat() == ArchiveFormat.ZIP) {
            //OutputStream fileOutputStream = createOutputStream(archiveFile)
            ZipArchiveOutputStreamJrr ze = new ZipArchiveOutputStreamJrr(fos, this)
            customizeZipArchiveOutputStreamJrr(ze)
//            log.info "cp111"
            return ze
        }
        if (getArchiveFormat() == ArchiveFormat.JAR) {
            //OutputStream fileOutputStream = createOutputStream(archiveFile)
            ZipArchiveOutputStreamJrr ze = new ZipArchiveOutputStreamJrr(fos, this)
            ze.jarMarkerAddedNeeded = true
            customizeZipArchiveOutputStreamJrr(ze)
//            log.info "cp1122"
            return ze
        }

        if (getArchiveFormat() == ArchiveFormat.TAR) {
            //OutputStream fileOutputStream = createOutputStream(archiveFile)
            TarArchiveOutputStream ze = createArchiveOutputStreamTarBig1 (fos)
            return ze
        }
        throw new IllegalArgumentException("${getArchiveFormat()}")

    }

    protected void customizeZipArchiveOutputStreamJrr(ZipArchiveOutputStreamJrr ze) {

    }

    InputStream createInputStreamForEntry(File fileEntry){
        return  new FileInputStream2(fileEntry);
    }

    /**
     * Used only for zip and jar archive
     */
    OutputStream createOutputStream(File f){
        return new FileOutputStream2(f);
    }


    protected TarArchiveOutputStream createArchiveOutputStreamTarBig(File archiveFile) throws IOException {
        return createArchiveOutputStreamTarBig1( createOutputStream(archiveFile));
    }

    protected TarArchiveOutputStream createArchiveOutputStreamTarBig1(OutputStream outputStream1) throws IOException {
        TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(outputStream1);
        archiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
        archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
        return archiveOutputStream;
    }

    @Override
    void writeToArchive(File parent, File[] sources, ArchiveOutputStream archive) throws IOException {
        if (needStop) {
            throw new Exception('stop requested')
        }
        for (File source : sources) {
            lastFile = source;
            String relativePath = relativePath(parent, source);
            relativePath = relativePath.replace('\\', '/')
            String relativePathNew = isNeedWriteFile2(source, relativePath, archive)
            if (relativePathNew != null) {
                createArchiveEntry(source, relativePathNew, archive);
                if (isDirectory1(source)) {
                    File[] files = source.listFiles()
                    writeToArchive(parent, files, archive);
                }
            }
        }
    }

    public static boolean useNew = true

    static String relativePath(File parent,File child){
        if(useNew){
            return  parent.getPathToParent(child);
        }else{
            return  IOUtils.relativePath(parent, child);
        }
    }

    String isNeedWriteFile2(File file, String relativePath, ArchiveOutputStream archive) {
        if (archiveAcceptRejectI != null) {
            return archiveAcceptRejectI.map(relativePath, file, this)
        }
        return relativePath
    }

    public boolean isFile1(File f) {
        if (f.isDirectory()) {
            return false
        }
        if (f.isFile()) {
            return true
        }
        log.info "not file and not dir : ${f}"
        return false
    }

    public boolean isDirectory1(File f) {
        if (f.isDirectory()) {
            return true
        }
        if (f.isFile()) {
            return false
        }
        log.info "not file and not dir : ${f}"
        return false
    }


    @Override
    void createArchiveEntry(File file, String entryName, ArchiveOutputStream archive) throws IOException {
        if (needStop) {
            throw new Exception('stop requested')
        }
        lastFile = file;
        if (isFile1(file) || createEntryForDir) {
            if (!addedEntries.add(entryName)) {
                boolean continue1 = onDuplicateEntry(file, entryName, archive)
                if (!continue1) {
                    return
                }
            }
            ArchiveEntry entry = archive.createArchiveEntry(file, entryName);
            // TODO #23: read permission from file, write it to the ArchiveEntry
            archive.putArchiveEntry(entry);

            if (!entry.isDirectory()) {
                InputStream input = null;
                try {
                    lastWrittenFile = file
                    input = createInputStreamForEntry(file);
//                    input = new FileInputStream(file);
                    copy(input, archive, copyBufferSize);
                    processedFiles++;
                } catch (Throwable e) {
                    //TODO write dummy bytes, so number of written bytes  matches file size
                    onError(e, file, entryName, archive)
                } finally {
                    IOUtils.closeQuietly(input);
                }
            }
            archive.closeArchiveEntry();
        }
    }

    public int copyBufferSize = 8096

    // boolean return true if continue
    boolean onDuplicateEntry(File file, String entryName, ArchiveOutputStream archive) {
        if (checkOnDuplicates) {
            throw new Exception("Entry already present ${entryName}")
        }
    }

    void createArchiveEntry(String entryName, String data) throws IOException {
        ZipArchiveEntry entry = new ZipArchiveEntry(entryName);
        outputStream.putArchiveEntry(entry)
        outputStream.write(data.getBytes())
        outputStream.closeArchiveEntry();
    }


    protected long copy(InputStream input, OutputStream output, int buffersize) throws IOException {
        byte[] buffer = new byte[buffersize];

        int n;
        long count;
        for (count = 0L; -1 != (n = input.read(buffer)); count += (long) n) {
            doStat(buffer, 0, n);
            output.write(buffer, 0, n);
        }
        return count;
    }

    void doStat(byte[] buffer, int begin, int length) {
        uncompressedFileSize += length
        if (digest != null) {
            digest.update(buffer, begin, length)
        }
    }


    void onError(Throwable e, File file, String entryName, ArchiveOutputStream archive) {
        log.info("${entryName} ${file}", e)
        if (errorFiles.size() > maxErrorFiles) {
            throw e
        }
        errorFiles.add(file);
    }


    void setDateOfAllFiles2(Date date) {
        dateOfAllFiles = FileTime.from(date.getTime(), TimeUnit.MILLISECONDS)
    }


    void customizeZipArchiveEntry(ZipArchiveEntry ze) {
//        log.info "cp111 ${dateOfAllFIles}"
        if (dateOfAllFiles != null) {
            ze.setCreationTime(dateOfAllFiles)
            ze.setLastAccessTime(dateOfAllFiles)
            ze.setLastModifiedTime(dateOfAllFiles)
        }
    }
}
