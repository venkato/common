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


    JrrJarArchiver(boolean convertJavaClassPath) {
        this(ArchiveFormat.JAR)
        acceptRejectFilterJar.isConvertJavaClassPath = convertJavaClassPath
    }

    @Deprecated
    JrrJarArchiver() {
        this(ArchiveFormat.JAR)
    }

    JrrJarArchiver(ArchiveFormat archiveFormat) {
        super(archiveFormat)
        archiveAcceptRejectI = acceptRejectFilterJar
        checkOnZeroFiles = true
        createEntryForDir = false
    }

    void createSimple(File dest,File source){
        createSimple2(dest,source,[],[])
    }

    void createSimple2(File dest,File source, List<String> startWithClasses, List<String> startWithIgnoreClasses){
        try {
            step1(dest, false)
            step2(source, startWithClasses, startWithIgnoreClasses)
            stepFinish()
        }finally{
            stepFinalAlways()
        }
    }


    void step1(OutputStream outputStream1, boolean writeMetaInfo) {
        if(outputStream!=null){
            throw new IllegalAccessException("output stream already set")
        }
        if (writeMetaInfo) {
            digest = DigestUtils.getSha256Digest()
        }
        outputStream = createArchiveOutputStream1(outputStream1)
    }

    void step1(File dest, boolean writeMetaInfo) {
        if (writeMetaInfo) {
            digest = DigestUtils.getSha256Digest()
        }
        createDestinationStream(dest)
    }


    /**
     * Can be called many times
     */
    void step2(File source) {
        long fileWrittenOnStart = processedFiles
        addDir(source);
        outputStream.flush()
        if (checkOnZeroFiles && processedFiles == fileWrittenOnStart) {
            throw new Exception("No files in archive for ${source}")
        }
    }

    /**
     * Can be called many times
     * @param startWithClasses if null or empty : means accept all
     * @param startWithIgnoreClasses  if null or empty : means accept all ( nothing to ignore)
     */
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


}
