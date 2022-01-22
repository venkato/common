package net.sf.jremoterun.utilities.nonjdk.log.tojdk.threadfilter;

import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLoggerSetter;

import java.util.logging.LogRecord;

public class Log4jAppenderListener {

	public void add(String s) {
		System.out.println(s);
	}

	//public static Log4j2PatternLayout patternLayout=new Log4j2PatternLayout();

	public void add(LogRecord loggingEvent, StackTraceElement[] stackTrace) {
		StringBuilder sb = new StringBuilder();
		JdkLoggerSetter.formatter.formatImpl3(loggingEvent, sb, stackTrace);
		System.out.println(sb);

	}
}
