package net.sf.jremoterun.utilities.nonjdk.javacompiler

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
class CompileFileLayout {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<ToFileRef2> srcDirs = []
    public ToFileRef2 outputDir ;
    public String javaVersion ;
    public List<ToFileRef2> libs = []




}
