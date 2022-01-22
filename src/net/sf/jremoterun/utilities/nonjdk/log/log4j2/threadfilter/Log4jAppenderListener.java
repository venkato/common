package net.sf.jremoterun.utilities.nonjdk.log.log4j2.threadfilter;

import net.sf.jremoterun.utilities.nonjdk.log.log4j2.Log4j2PatternLayout;
import org.apache.logging.log4j.core.LogEvent;

public class Log4jAppenderListener {

	public void add(String s) {
		System.out.println(s);
	}

	public static Log4j2PatternLayout patternLayout=new Log4j2PatternLayout();

	public void add(LogEvent loggingEvent, StackTraceElement[] stackTrace) {
		StringBuilder sb = new StringBuilder();
		patternLayout.formatImpl2(loggingEvent, sb, stackTrace);
		System.out.println(sb);

	}
}
