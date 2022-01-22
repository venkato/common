package net.sf.jremoterun.utilities.nonjdk.sshsup;


import groovy.transform.CompileStatic;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Level
import java.util.logging.Logger;

@CompileStatic
public class JscpLogger implements com.jcraft.jsch.Logger {

    public int enabledLogger;
    public boolean logDebugAsInfo = false;

    private static final Logger loggerUsed = JrrClassUtils.getJdkLogForCurrentClass();


    public JscpLogger(int enabledLogger) {
        this.enabledLogger = enabledLogger;
    }

    @Override
    public boolean isEnabled(int level) {
        return level >= enabledLogger;
    }

    @Override
    public void log(int level, String message) {
        Level level2 = JscpLoggerMapper.levelsMapping.get(level);
        if (logDebugAsInfo && level == DEBUG) {
            level2 = Level.INFO
        }
        loggerUsed.log(level2, message);

    }

    @Override
    public void log(int level, String message, Throwable cause) {
        Level level2 = JscpLoggerMapper.levelsMapping.get(level);
        if (logDebugAsInfo && level == DEBUG) {
            level2 = Level.INFO
        }
        loggerUsed.log(level2, message, cause);
    }

}
