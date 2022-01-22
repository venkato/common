package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix2;

import java.util.logging.Logger;

@EqualsAndHashCode
@CompileStatic
class ClassLocaltionInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File f;
    public boolean isMultiReleaseFile = false;
    public ClassSlashNoSuffix2 fullPathInJarSlash;

//    ClassLocaltionInfo(File f) {
//        this.f = f
//    }

    ClassLocaltionInfo(File f, ClassSlashNoSuffix2 fullPathInJarSlash) {
        this.f = f
        this.fullPathInJarSlash = fullPathInJarSlash
//        assert !fullPathInJarSlash.contains('.')
    }

    @Override
    String toString() {
        return "${f} sub=${fullPathInJarSlash}"
    }
}
