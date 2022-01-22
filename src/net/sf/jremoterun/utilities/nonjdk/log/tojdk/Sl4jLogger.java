package net.sf.jremoterun.utilities.nonjdk.log.tojdk;

import net.sf.jremoterun.utilities.nonjdk.log.Sl4jLoggerCommon;
import org.slf4j.jul.JDK14LoggerFactory;

public class Sl4jLogger {


	public static void setSl4jLoggerToLog4j2() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		//Log4jMarkerFactory markerFactory = new Log4jMarkerFactory();
		Sl4jLoggerCommon.setLoggerImpl(new JDK14LoggerFactory());

	}

}
