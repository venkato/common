package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.compress.archivers.ArchiveOutputStream
import org.apache.commons.compress.archivers.jar.JarArchiveOutputStream
import org.rauschig.jarchivelib.ArchiveFormat;

import java.util.logging.Logger;

@CompileStatic
class JrrJarArchiver extends JrrCommonsArchiver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JarAcceptRejectFilter acceptRejectFilterJar = new JarAcceptRejectFilter([], [])
    public boolean checkMatchedFiles = true


    JrrJarArchiver() {
        this(ArchiveFormat.JAR)
    }

    JrrJarArchiver(ArchiveFormat archiveFormat) {
        super(archiveFormat)
        archiveAcceptRejectI = acceptRejectFilterJar
        checkOnZeroFiles = true
        createEntryForDir = false
    }


    void step1(File dest, boolean writeMetaInfo) {
        if (writeMetaInfo) {
            digest = DigestUtils.getSha256Digest()
        }
        createDestinationStream(dest)
    }

    void step2(File source, List<String> startWithClasses, List<String> startWithIgnoreClasses) {
        long fileWrittenOnStart = processedFiles
        acceptRejectFilterJar.acceptedStartWith.clear()
        acceptRejectFilterJar.startWithClasses.clear()
        acceptRejectFilterJar.startWithIgnoreClasses.clear()
        acceptRejectFilterJar.startWithClasses.addAll acceptRejectFilterJar.normalizePaths(startWithClasses)
        acceptRejectFilterJar.startWithIgnoreClasses.addAll acceptRejectFilterJar.normalizePaths(startWithIgnoreClasses)
        addDir(source);
        outputStream.flush()
        if (checkOnZeroFiles && processedFiles == fileWrittenOnStart) {
            throw new Exception("No files in archive for ${source}")
        }
        if (checkMatchedFiles) {
            Collection<String> strs = new ArrayList<String>(acceptRejectFilterJar.startWithClasses) - acceptRejectFilterJar.acceptedStartWith
            if (strs.size() > 0) {
                throw new Exception("Filter was not used : ${strs.size()} : ${strs}")
            }
        }
    }

    void stepFinish() {
        if (digest != null) {
            writeJarMeta()
        }
        outputStream.flush()
        closeOutStream()
        postChecks()
    }


    void stepFinalAlways() {
        closeOutStream()
    }


    void createJar4(File dest, File source, List<String> startWithClasses, List<String> startWithIgnoreClasses, boolean writeMetaInfo) {
        if (writeMetaInfo) {
            digest = DigestUtils.getSha256Digest()
        }
        acceptRejectFilterJar.startWithClasses.addAll acceptRejectFilterJar.normalizePaths(startWithClasses)
        acceptRejectFilterJar.startWithIgnoreClasses.addAll acceptRejectFilterJar.normalizePaths(startWithIgnoreClasses)
        createDestinationStream(dest)
        try {
            addDir(source);
            if (writeMetaInfo) {
                writeJarMeta()
            }
            outputStream.flush()
        } finally {
            closeOutStream()
        }
        postChecks()
    }

    void writeJarMeta() {
        ArchiveInfo archiveInfo = new ArchiveInfo()
        archiveInfo.includeFilter = acceptRejectFilterJar.startWithClasses
        archiveInfo.ignoreFilter = acceptRejectFilterJar.startWithIgnoreClasses
        writeMetaInfo(archiveInfo)
    }


    // needed if file extension not jar
    @Override
    protected ArchiveOutputStream createArchiveOutputStream(File archiveFile) throws IOException {
        FileOutputStream outputStream3 = new FileOutputStream(archiveFile)
        return new JarArchiveOutputStream(outputStream3)
    }

}
