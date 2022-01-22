package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanStore
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.archivers.ArchiveEntry
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry
import org.rauschig.jarchivelib.ArchiveFormat
import org.rauschig.jarchivelib.CommonsArchiverOriginal
import org.rauschig.jarchivelib.IOUtils

import java.security.MessageDigest;
import java.util.logging.Logger;

@CompileStatic
class JrrCommonsArchiver extends CommonsArchiverOriginal {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Deprecated
    public static String archivePath = JrrArchiveSettings.archivePath;
    public int maxErrorFiles = 10;
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

    /**
     * @see org.apache.commons.codec.digest.DigestUtils
     */
    public MessageDigest digest;

    JrrCommonsArchiver(ArchiveFormat archiveFormat) {
        super(archiveFormat)
    }

    @Deprecated
    static void createJar(File dest, File source, List<String> startWithClasses, List<String> startWithIgnoreClasses, boolean writeMetaInfo) {
        JrrCommonsArchiver archiver2 = new JrrCommonsArchiver(ArchiveFormat.JAR) {

            @Override
            protected ArchiveOutputStream createArchiveOutputStream(File archiveFile) throws IOException {
                FileOutputStream outputStream3 = new FileOutputStream(archiveFile)
                return new JarArchiveOutputStream(outputStream3)
            }
        };
        if (writeMetaInfo) {
            archiver2.digest = DigestUtils.getSha256Digest()
        }
        archiver2.archiveAcceptRejectI = new JarAcceptRejectFilter(startWithClasses, startWithIgnoreClasses);
        archiver2.checkOnZeroFiles = true
        archiver2.createEntryForDir = false
        archiver2.createDestinationStream(dest)
        try {
            archiver2.addDir(source);
            if (writeMetaInfo) {
                ArchiveInfo archiveInfo = new ArchiveInfo()
                archiveInfo.includeFilter = startWithClasses
                archiveInfo.ignoreFilter = startWithIgnoreClasses
                archiver2.writeMetaInfo(archiveInfo)
            }
            archiver2.outputStream.flush()
        } finally {
            archiver2.closeOutStream()
        }
        archiver2.postChecks()
    }

    void writeMetaInfo(ArchiveInfo archiveInfo) {
        archiveInfo.creationDate = new DateWithFormat(new Date(), DateWithFormat.fullDateFormat)
        if (digest != null) {
            digestResult = org.apache.commons.codec.binary.Base64.encodeBase64String(digest.digest());
            archiveInfo.contentHash = digestResult
        }
        String info = JavaBeanStore.save3(archiveInfo)
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
        JrrIoUtils.closeQuietly2(outputStream,log);
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

    @Override
    protected ArchiveOutputStream createArchiveOutputStream(File archiveFile) throws IOException {
        outputStream = super.createArchiveOutputStream(archiveFile)
        return outputStream;
    }

    protected static TarArchiveOutputStream createArchiveOutputStreamTarBig(File archiveFile) throws IOException {
        TarArchiveOutputStream archiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(archiveFile));
        archiveOutputStream.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_POSIX);
        archiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_POSIX);
        return archiveOutputStream;
    }

    @Override
    protected void writeToArchive(File parent, File[] sources, ArchiveOutputStream archive) throws IOException {
        if (needStop) {
            throw new Exception('stop requested')
        }
        for (File source : sources) {
            lastFile = source;
            String relativePath = IOUtils.relativePath(parent, source);
            relativePath = relativePath.replace('\\', '/')
            String relativePathNew = isNeedWriteFile2(source, relativePath, archive)
            if (relativePathNew != null) {
                createArchiveEntry(source, relativePathNew, archive);
                if (source.isDirectory()) {
                    File[] files = source.listFiles()
                    writeToArchive(parent, files, archive);
                }
            }
        }
    }

    String isNeedWriteFile2(File file, String relativePath, ArchiveOutputStream archive) {
        if (archiveAcceptRejectI != null) {
            return archiveAcceptRejectI.map(relativePath, file, this)
        }
        return relativePath
    }

    @Override
    protected void createArchiveEntry(File file, String entryName, ArchiveOutputStream archive) throws IOException {
        if (needStop) {
            throw new Exception('stop requested')
        }
        lastFile = file;
        if (!file.isDirectory() || createEntryForDir) {
            if(!addedEntries.add(entryName)){
                boolean continue1 = onDuplicateEntry(file,entryName,archive)
                if(!continue1){
                    return
                }
            }
            ArchiveEntry entry = archive.createArchiveEntry(file, entryName);
            // TODO #23: read permission from file, write it to the ArchiveEntry
            archive.putArchiveEntry(entry);

            if (!entry.isDirectory()) {
                FileInputStream input = null;
                try {
                    lastWrittenFile = file
                    input = new FileInputStream(file);
                    copy(input, archive, 8024);
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

    // boolean return true if continue
    boolean onDuplicateEntry(File file, String entryName, ArchiveOutputStream archive){
        if(checkOnDuplicates ){
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

}
