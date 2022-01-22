package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.compress.archivers.ArchiveOutputStream

import java.util.logging.Logger

@CompileStatic
class GenericAcceptRejectFilter implements ArchiveAcceptRejectI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    // path separator is /
    public List<File> ignoreFilesFiles = []

    public List<String> ignoreFilesStartWithPattern = []

    public List<String> ignoreFilesPattern = []

    public List<File> ignoreFiles = []

    @Override
    String map(String relativePath, File file, JrrCommonsArchiver archiver) {
        if (isAccept(file,relativePath, archiver)) {
            return relativePath
        }
        return null
    }

    boolean isAccept(File file, String relativePath, JrrCommonsArchiver archive) {
        boolean needAccept = true
        if (ignoreFilesFiles.contains(file)) {
            needAccept = false
        }
        if (needAccept) {
            String findIgnore1 = ignoreFilesPattern.find { relativePath.contains(it) }
            needAccept = findIgnore1 == null;
            if (needAccept) {
                String findIgnore2 = ignoreFilesStartWithPattern.find { relativePath.startsWith(it) }
                needAccept = findIgnore2 == null
            }
        }
        if (!needAccept) {
            ignoreFiles.add(file);
            log.info "ignoring : ${relativePath} ${file}"
        }
        return needAccept;
    }

}
