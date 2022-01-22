package net.sf.jremoterun.utilities.nonjdk.compile.stddirs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern;

import java.util.logging.Logger;

@CompileStatic
class SrcDirs {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ExactChildPattern srcJust = new ExactChildPattern('src')
    public static ExactChildPattern srcMainJava = new ExactChildPattern('src/main/java')

    public static ExactChildPattern outputBin = new ExactChildPattern('bin')
    public static ExactChildPattern outputTargetClasses = new ExactChildPattern('target/classes')
}
