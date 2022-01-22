package net.sf.jremoterun.utilities.nonjdk.log.log4j2;

import net.sf.jremoterun.utilities.nonjdk.log.levelmapping.Log4jOld;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.log4j.spi.ThrowableInformation;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

public class Log4jIntoLog4j2Converter extends AppenderSkeleton implements Appender {


    @Deprecated
    public static Map<org.apache.log4j.Level, Level> log4j1ToLog4j2Map = Log4jOld.log4j1ToLog4j2Map;


    @Deprecated
    public static Map<Level, org.apache.log4j.Level> log4j2ToLog4j1Map =  Log4jOld.log4j2ToLog4j1Map;

    //@Override
    public void close() {

    }

    //@Override
    public boolean requiresLayout() {
        return false;
    }

    //@Override
    protected void append(LoggingEvent logRecord) {
        Logger logger = LogManager.getLogger(logRecord.getLoggerName());
        Level level = log4j1ToLog4j2Map.get(logRecord.getLevel());
        if (logger.isEnabled(level)) {
            ThrowableInformation throwableInformation = logRecord.getThrowableInformation();
            Throwable th = throwableInformation == null ? null : throwableInformation.getThrowable();
            logger.log(log4j1ToLog4j2Map.get(logRecord.getLevel()), logRecord.getMessage(), th);
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
