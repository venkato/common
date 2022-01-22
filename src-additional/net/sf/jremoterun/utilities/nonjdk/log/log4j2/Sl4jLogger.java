package net.sf.jremoterun.utilities.nonjdk.log.log4j2;

import net.sf.jremoterun.utilities.nonjdk.log.Sl4jLoggerCommon;
import org.apache.logging.slf4j.Log4jLoggerFactory;
import org.apache.logging.slf4j.Log4jMarkerFactory;

public class Sl4jLogger {


	public static void setSl4jLoggerToLog4j2() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Log4jMarkerFactory markerFactory = new Log4jMarkerFactory();
		Sl4jLoggerCommon.setLoggerImpl(new Log4jLoggerFactory(markerFactory));

	}

}
