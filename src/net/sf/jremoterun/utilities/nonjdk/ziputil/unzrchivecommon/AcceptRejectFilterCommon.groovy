package net.sf.jremoterun.utilities.nonjdk.ziputil.unzrchivecommon

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.archiver.ArchiveAcceptRejectI
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrCommonsArchiver
import org.apache.commons.compress.archivers.ArchiveEntry

import java.util.logging.Logger

@CompileStatic
class AcceptRejectFilterCommon {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<String> startWithClasses;
    public List<String> startWithIgnoreClasses;
    public List<String> endWithClasses = [];
    public List<String> endWithIgnoreClasses = [];
    public HashSet<String> acceptedStartWith = new HashSet<>();
    /**
     * convert . to / in path
     */
    public boolean isConvertJavaClassPath = true
    public boolean trimSpaces = true
    public boolean checkForSpaceInPath = true

    AcceptRejectFilterCommon(List<String> startWithClasses, List<String> startWithIgnoreClasses) {
        this.startWithClasses = normalizePaths(startWithClasses)
        if (startWithIgnoreClasses != null) {
            this.startWithIgnoreClasses = normalizePaths(startWithIgnoreClasses)
        }
    }

    List<String> normalizePaths(Collection<String> path) {
        if (path == null) {
            return null
        }

        return path.collect { normalizePath(it) }
    }

    String normalizePath(String path) {
        if(trimSpaces) {
            path = path.trim()
        }
        if(checkForSpaceInPath) {
            if (path.contains(' ')) {
                throw new Exception("path has space : ${path}")
            }
        }
        path = path.replace('//', '/')
        if(isConvertJavaClassPath) {
            path = path.replace('.', '/')
        }
        return path
    }



    boolean isAccept3(String relativePath) {
//        if (!file.isFile()) {
//            log.info "not file and not dir : ${file}"
//            return true
//        }
        if (startWithClasses == null) {
            throw new NullPointerException('startWithClasses is null')
        }
        boolean acceptF
        if (startWithClasses.size() == 0) {
            acceptF = true
        } else {
            String acceptFound = startWithClasses.find { relativePath.startsWith(it) }
            acceptedStartWith.add(acceptFound)
            acceptF = acceptFound != null
        }
        if (acceptF && startWithIgnoreClasses != null && startWithIgnoreClasses.size() > 0) {
            String ignoreFound = startWithIgnoreClasses.find { relativePath.startsWith(it) }
            acceptF = ignoreFound == null
        }
        if (acceptF && endWithClasses != null && endWithClasses.size() > 0) {
            String acceptFound = endWithClasses.find { relativePath.startsWith(it, relativePath.length() - it.length()) }
            acceptF = acceptFound != null
        }
        if (acceptF && endWithIgnoreClasses != null && endWithIgnoreClasses.size() > 0) {
            String ignoreFound = endWithIgnoreClasses.find { relativePath.startsWith(it, relativePath.length() - it.length()) }
            acceptF = ignoreFound == null
        }
        return acceptF
    }
}
