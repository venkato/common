package net.sf.jremoterun.utilities.nonjdk.archiver

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.ziputil.unzrchivecommon.AcceptRejectFilterCommon;

import java.util.logging.Logger;

@CompileStatic
class JarAcceptRejectFilter extends AcceptRejectFilterCommon implements ArchiveAcceptRejectI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JarAcceptRejectFilter(List<String> startWithClasses, List<String> startWithIgnoreClasses) {
        super(startWithClasses,startWithIgnoreClasses)
    }


    @Override
    String map(String relativePath, File file, JrrCommonsArchiver archiver) {
        if (isAccept(relativePath, file, archiver)) {
            return relativePath
        }
        return null
    }

    boolean isAccept(String relativePath, File file, JrrCommonsArchiver archiver) {
//        if( archiver.isDirectory1(file)){
//            return true
//        }
        return isAccept3(relativePath, archiver.isDirectory1(file))
    }
}
