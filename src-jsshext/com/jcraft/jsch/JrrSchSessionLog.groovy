package com.jcraft.jsch

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrSchSessionLog {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean doLogging = true;
    public volatile boolean doLoggingThisInstance = doLogging;


    void logMsg(String msg) {
        if (doLoggingThisInstance) {
            log.info(msg);
        }
    }


}
