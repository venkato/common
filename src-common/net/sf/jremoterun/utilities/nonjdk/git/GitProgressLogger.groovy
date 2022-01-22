package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class GitProgressLogger {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


     void send(StringBuilder s) {
        log.info(s.toString())
    }

}
