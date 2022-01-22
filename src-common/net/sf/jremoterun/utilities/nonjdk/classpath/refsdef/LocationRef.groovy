package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class LocationRef {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClRef clRef
    public File f;
    public int lineNumber;


}
