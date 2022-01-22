package net.sf.jremoterun.utilities.nonjdk.log.tojdk;

import net.sf.jremoterun.utilities.nonjdk.log.levelmapping.Log4jOld;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Log4jOldIntoJdkConverter extends AppenderSkeleton implements Appender {


    //@Override
    public void close() {

    }

    //@Override
    public boolean requiresLayout() {
        return false;
    }

    //@Override
    protected void append(LoggingEvent logRecord) {
        Logger logger = Logger.getLogger(logRecord.getLoggerName());
        Level level = net.sf.jremoterun.utilities.nonjdk.log.tojdk.mapping.Log4jOld.log4j1ToLog4j2Map.get(logRecord.getLevel());
        if (logger.isLoggable(level)) {
            ThrowableInformation throwableInformation = logRecord.getThrowableInformation();
            Throwable th = throwableInformation == null ? null : throwableInformation.getThrowable();
            logger.log(level, String.valueOf( logRecord.getMessage()), th);
        }
        // logmana
    }


//    public void clearFilters() {
//        headFilter=null;
//        tailFilter = null;
////        super.clearFilters();
//    }
//
//
//    public ErrorHandler getErrorHandler() {
//        return errorHandler;
//    }
//
//
//    public synchronized void setErrorHandler(ErrorHandler eh) {
//        errorHandler = eh;
//    }
}
