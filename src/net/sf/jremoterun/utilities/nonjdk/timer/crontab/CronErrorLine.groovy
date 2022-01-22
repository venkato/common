package net.sf.jremoterun.utilities.nonjdk.timer.crontab

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class CronErrorLine {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String line;
    Exception exception;

    CronErrorLine(String line, Exception exception) {
        this.line = line
        this.exception = exception
    }
}
