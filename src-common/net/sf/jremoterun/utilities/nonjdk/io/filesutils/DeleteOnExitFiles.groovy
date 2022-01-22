package net.sf.jremoterun.utilities.nonjdk.io.filesutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class DeleteOnExitFiles {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static LinkedHashSet<String> receiveDeleteOnexitFiles() {
        return (LinkedHashSet) JrrClassUtils.getFieldValue(new ClRef('java.io.DeleteOnExitHook'), 'files')
    }

}
