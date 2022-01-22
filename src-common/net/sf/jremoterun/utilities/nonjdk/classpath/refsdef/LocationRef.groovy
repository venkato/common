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

    LocationRef(ClRef clRef, int lineNumber) {
        this.clRef = clRef
        this.lineNumber = lineNumber
    }

    LocationRef(File f, int lineNumber) {
        this.f = f
        this.lineNumber = lineNumber
    }

    @Override
    String toString() {
        if(clRef==null){
            return "${f}:${lineNumber}"
        }

        return "${clRef}:${lineNumber}"
    }
}
