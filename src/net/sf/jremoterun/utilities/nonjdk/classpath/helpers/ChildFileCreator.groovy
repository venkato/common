package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ChildFileCreator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File parentDir
    public ChildChildPattern childPattern;

    ChildFileCreator(File parentDir, ChildChildPattern childPattern) {
        this.parentDir = parentDir
        this.childPattern = childPattern
    }

    FileChildLazyRef getRefNoChecks() {
        return new FileToFileRef(parentDir).childP(childPattern)
    }

    File getFileNoChecks() {
        return getRefNoChecks().resolveToFile()
    }

    File getFileCheckExists() {
        File file = getFileNoChecks()
        assert file.exists()
        return file
    }

    File getDirCreate() {
        File fff = getFileNoChecks()
        if (!fff.exists()) {
            assert parentDir.isChildFile(fff)
            mkDir(fff)
        }
        return fff
    }

    File getCreatedParentDir() {
        File fff = getFileNoChecks()
        File parentFile = fff.getParentFile()
        if (!parentFile.exists()) {
            assert parentDir.isChildFile(fff)
            mkDir(parentFile)
        }
        return fff
    }

    void mkDir(File childF) {
        if (parentDir == childF) {

        } else {
            File parentFile = childF.getParentFile()
            if (parentFile == null) {
                throw new NullPointerException("parent file is null for ${parentDir}   ${childPattern}")
            }
            if (parentFile.exists()) {

            } else {
                mkDir(parentFile)
            }
        }
        childF.mkdir()
        assert childF.exists()
    }


}
