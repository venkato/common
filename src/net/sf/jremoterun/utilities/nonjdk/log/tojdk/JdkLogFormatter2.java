package net.sf.jremoterun.utilities.nonjdk.log.tojdk;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.log.IsLogExceptionStackTrace;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;


@CompileStatic
public class JdkLogFormatter2 extends Formatter {


    private static SimpleDateFormat sdfDayHourMin = new SimpleDateFormat("dd HH:mm:ss");

    public boolean logTime = false;

    public static byte[] emptyBytes = "".getBytes();

    public static String sep = "\n";


    public volatile IsLogExceptionStackTrace isLogExceptionStackTrace = new IsLogExceptionStackTrace();



    public static Collection<String> ignoreClassesForCurrentClass = JrrClassUtils.ignoreClassesForCurrentClass;

    public static Map<String, JdkCustomLayout> customLayouts = new HashMap();


    @Override
    public String format(LogRecord record) {
        Formatter custom = customLayouts.get(record.getLoggerName());
        if (custom != null && custom != this) {
            return custom.format(record);
        }
        return formatImpl(record);
    }

    public String formatImpl(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
        formatImpl3(record, sb,stackTraces);
        return sb.toString();
    }

    public void formatImpl3(LogRecord logRecord, StringBuilder sb, StackTraceElement[] stackTraces) {
        StackTraceElement location = getStacktraceELement(stackTraces);
        logStackElement(location, sb);
        sb.append(" - ");
        logTime(logRecord, sb);

        logMessage(logRecord, sb);

        logStackTraceIfNeeded(logRecord, sb, stackTraces, location);
        sb.append(sep);
    }

    public void logMessage(LogRecord logRecord, StringBuilder sb) {
        sb.append(logRecord.getMessage());
    }

    public void logStackElement(StackTraceElement se, StringBuilder sb) {
        sb.append(se);
//        String className = se.getClassName();
    }

    public StackTraceElement getStacktraceELement(StackTraceElement[] stackTraces) {
        int k = 0;
        for (StackTraceElement stackTraceElement : stackTraces) {
            k++;
            if (k < 2) {
                continue;
            }
            if (acceptStackTraceElement(stackTraceElement)) {
                return stackTraceElement;
            }
        }
        return null;
    }

    public boolean acceptStackTraceElement(StackTraceElement stackTraceElement) {
        String lcassName = stackTraceElement.getClassName();
        for (String ignore : ignoreClassesForCurrentClass) {
            boolean res = lcassName.startsWith(ignore);
            if (res) {
                return false;
            } else {

            }
        }
        return true;

    }

    public String getTime() {
        String time;
        synchronized (sdfDayHourMin) {
            time = sdfDayHourMin.format(new Date());
        }
        return time;
    }


    public void logTime(LogRecord logRecord, StringBuilder sb) {
        if (logRecord.getLevel().intValue() >= Level.INFO.intValue()) {
            sb.append(getTime());
            sb.append(" ");
            sb.append(logRecord.getLevel().getName());
            sb.append(" ");
        } else {
            if (logTime) {
                sb.append(" ");
                sb.append(getTime());
                sb.append(" - ");
            }
        }
    }

    public void onEmptyStackTrace(LogRecord logRecord, StringBuilder sb, StackTraceElement[] stackTraces, StackTraceElement location, Throwable ti) {

    }

    public boolean isNeedPrintStackTrace(LogRecord logRecord, StringBuilder sb, StackTraceElement[] stackTraces, StackTraceElement location, Throwable ti) {
        if (logRecord.getLevel().intValue() < Level.WARNING.intValue()) {
            return false;
        }
        boolean isLogStackTrace = isLogExceptionStackTrace.isLogStackTrace(logRecord, sb, stackTraces, location, ti);
        return isLogStackTrace;
    }

    public void logStackTraceIfNeeded(LogRecord logRecord, StringBuilder sb, StackTraceElement[] stackTraces, StackTraceElement location) {
        Throwable ti = logRecord.getThrown();
        final boolean error = isNeedPrintStackTrace(logRecord, sb, stackTraces, location, ti);
        if (ti == null) {
            if (error) {
                writeStackTrace(sb, stackTraces);
            }
        } else {
            sb.append(" ");
            Throwable rootException = JrrUtils.getRootException(ti);
            if (error) {
                StackTraceElement[] stackTraces3 = rootException.getStackTrace();
                if (stackTraces3 == null || stackTraces3.length == 0) {
                    onEmptyStackTrace(logRecord, sb, stackTraces, location, ti);
                    writeStackTrace(sb, stackTraces);
                } else {
                    final StringWriter stringWriter = new StringWriter();
                    rootException.printStackTrace(new PrintWriter(stringWriter));
                    sb.append(stringWriter.getBuffer());
                }
            } else {
                sb.append(rootException);
            }

        }
    }

    public void writeStackTrace(StringBuilder sb, StackTraceElement[] stackTraces) {
        int i = 0;
        for (StackTraceElement stackTraceElement : stackTraces) {
            i++;
            if (i < 5) {
                continue;
            }
            if (acceptStackTraceElement(stackTraceElement)) {
                sb.append(sep);
                sb.append("  ");
                sb.append(stackTraceElement.toString());
            }
        }

    }

}
