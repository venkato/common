package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JarAcceptRejectFilter implements ArchiveAcceptRejectI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<String> startWithClasses;
    public List<String> startWithIgnoreClasses;
    public HashSet<String> acceptedStartWith = new HashSet<>();

    JarAcceptRejectFilter(List<String> startWithClasses, List<String> startWithIgnoreClasses) {
        this.startWithClasses = normalizePaths(startWithClasses)
        if (startWithIgnoreClasses != null) {
            this.startWithIgnoreClasses = normalizePaths(startWithIgnoreClasses)
        }
    }

    List<String> normalizePaths(Collection<String> path) {
        if(path==null){
            return null
        }

        return path.collect { normalizePath(it) }
    }

    String normalizePath(String path) {
        path = path.trim()
        if (path.contains(' ')) {
            throw new Exception("path has space : ${path}")
        }
        path = path.replace('//', '/')
        path = path.replace('.', '/')

        return path
    }

    @Override
    String map(String relativePath, File file, JrrCommonsArchiver archiver) {
        if (isAccept(relativePath, file, archiver)) {
            return relativePath
        }
        return null
    }

    boolean isAccept(String relativePath, File file, JrrCommonsArchiver archiver) {
        if (!file.isFile()) {
            return true
        }
        if(startWithClasses==null){
            throw new NullPointerException('startWithClasses is null')
        }
        boolean acceptF
        if (startWithClasses.size() == 0) {
            acceptF = true
        }else {
            String acceptFound = startWithClasses.find { relativePath.startsWith(it) }
            acceptedStartWith.add(acceptFound)
            acceptF = acceptFound != null
        }
        if (acceptF && startWithIgnoreClasses != null && startWithIgnoreClasses.size()>0) {
            String ignoreFound = startWithIgnoreClasses.find { relativePath.startsWith(it) }
            acceptF = ignoreFound == null
        }
        return acceptF
    }
}
