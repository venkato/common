package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.JrrClassUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.apache.logging.slf4j.Log4jMarkerFactory;
import org.slf4j.impl.StaticLoggerBinder;

public class Sl4jLogger {


	public static void setSl4jLoggerToLog4j2() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Log4jMarkerFactory markerFactory = new Log4jMarkerFactory();
		Sl4jLoggerCommon.setLoggerImpl(new Log4jLoggerFactory(markerFactory));

	}

}
