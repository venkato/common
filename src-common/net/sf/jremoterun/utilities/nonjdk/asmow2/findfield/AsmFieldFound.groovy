package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class AsmFieldFound {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public int access
    public String name
    public String descriptor
    public String signature
    public Object value
    public File baseFile
    public ClRef clRef

    @Override
    String toString() {
        return "${clRef} - ${name} ${descriptor}"
    }
}
