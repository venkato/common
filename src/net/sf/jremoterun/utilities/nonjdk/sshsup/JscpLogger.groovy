package net.sf.jremoterun.utilities.nonjdk.sshsup;

import org.apache.logging.log4j.Logger;
import groovy.transform.CompileStatic;

import net.sf.jremoterun.utilities.JrrClassUtils;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

@CompileStatic
public class JscpLogger implements com.jcraft.jsch.Logger {

	public int enabledLogger;

	public static Map<Integer,Level> levelsMapping = [:];

	public Logger loggerUsed = LogManager.getLogger(JrrClassUtils.getCurrentClass());

	static {
		levelsMapping.put(DEBUG,Level.DEBUG);
		levelsMapping.put(INFO, Level.INFO);
		levelsMapping.put(WARN, Level.WARN);
		levelsMapping.put(ERROR, Level.ERROR);
		levelsMapping.put(FATAL, Level.FATAL);
	}

	public JscpLogger(int enabledLogger) {
		this.enabledLogger = enabledLogger;
	}

	@Override
	public boolean isEnabled(int level) {
		return level >= enabledLogger;
	}

	@Override
	public void log(int level, String message) {
		Level level2 = levelsMapping.get(level);
		loggerUsed.log(level2, message);
	}

}
