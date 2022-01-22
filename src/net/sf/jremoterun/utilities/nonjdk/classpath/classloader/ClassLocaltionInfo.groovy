package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic
import groovy.transform.EqualsAndHashCode;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@EqualsAndHashCode
@CompileStatic
class ClassLocaltionInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File f;
    public boolean isMultiReleaseFile = false;
    public String fullPathInJarSlash;

    ClassLocaltionInfo(File f) {
        this.f = f
    }

    ClassLocaltionInfo(File f, String fullPathInJarSlash) {
        this.f = f
        isMultiReleaseFile=true
        this.fullPathInJarSlash = fullPathInJarSlash
        assert !fullPathInJarSlash.contains('.')
    }


}
