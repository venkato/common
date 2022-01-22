package net.sf.jremoterun.utilities.nonjdk.sshsup;


import groovy.transform.CompileStatic;

import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Level
import java.util.logging.Logger;

@CompileStatic
public class JscpLogger implements com.jcraft.jsch.Logger {

	public int enabledLogger;

    private static final Logger loggerUsed = JrrClassUtils.getJdkLogForCurrentClass();


//	public static Map<Integer,Level> levelsMapping = [:];



//	static {
//		levelsMapping.put(com.jcraft.jsch.Logger.DEBUG,Level.DEBUG);
//		levelsMapping.put(com.jcraft.jsch.Logger.INFO, Level.INFO);
//		levelsMapping.put(com.jcraft.jsch.Logger.WARN, Level.WARN);
//		levelsMapping.put(com.jcraft.jsch.Logger.ERROR, Level.ERROR);
//		levelsMapping.put(com.jcraft.jsch.Logger.FATAL, Level.FATAL);
//	}

	public JscpLogger(int enabledLogger) {
		this.enabledLogger = enabledLogger;
	}

	@Override
	public boolean isEnabled(int level) {
		return level >= enabledLogger;
	}

	@Override
	public void log(int level, String message) {
		Level level2 = net.sf.jremoterun.utilities.nonjdk.sshsup.JscpLoggerMapper.levelsMapping.get(level);
		loggerUsed.log(level2, message);
	}

}
