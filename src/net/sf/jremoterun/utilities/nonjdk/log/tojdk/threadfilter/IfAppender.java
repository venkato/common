package net.sf.jremoterun.utilities.nonjdk.log.tojdk.threadfilter;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter;
//import net.sf.jremoterun.utilities.nonjdk.log.log4j2.threadfilter.Log4jAppenderListener;
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLoggerSetter;
import timmoson.common.sertcp.TcpSession;

import java.util.HashSet;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class IfAppender extends Handler {
	private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


	public TcpSession session;

	public Log4jAppenderListener appenderListener;

	public Thread loggingThread;

	public HashSet<String> loggingLogger = new HashSet<String>();

	public boolean translateAllLoggers = false;
	public boolean translateAllLoggersForExecuterThread = false;

	public IfAppender(TcpSession session) throws Exception {
		this.session = session;
		appenderListener = session.makeClient(Log4jAppenderListener.class, Log4jAppenderListener.class.getName());
		loggingThread = Thread.currentThread();
	}

	public void addClassToLog4jTranslate(Class clazz) {
		loggingLogger.add(clazz.getName());
	}

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }

    @Override
    public void publish(LogRecord loggingEvent) {
		if (session.isClosed()) {
			log.fine("session is closed");
		} else {
			boolean isLog = translateAllLoggers
					|| (Thread.currentThread() == loggingThread && translateAllLoggersForExecuterThread)
					|| loggingLogger.contains(loggingEvent.getLoggerName());
			if (IfAppender.class.getName().equals(loggingEvent.getLoggerName())) {
				isLog = false;
			}
			if (isLog) {
				long millis = System.currentTimeMillis();
				StringBuilder sb = new StringBuilder();
				JdkLoggerSetter.formatter.formatImpl3(loggingEvent, sb, new Throwable().getStackTrace());
				sb.setLength(sb.length() - JdkLogFormatter.sep.length());
				try {
					appenderListener.add(sb.toString());
					long diff = System.currentTimeMillis() - millis;
					diff = diff / 1000;
					if (diff > 1) {
						String msg = "too long " + diff + " sec " + loggingEvent.getMessage();
						Level level = loggingEvent.getLevel();
						if (level.intValue() > Level.SEVERE.intValue()) {
							level = Level.SEVERE;
						}

						Logger.getLogger(loggingEvent.getLoggerName()).log(level, msg, loggingEvent.getThrown());

					}
				} catch (Exception e) {
					log.log(Level.INFO, ""+loggingEvent.getMessage(), e);
				}
			}
			// }
		}
	}


}
