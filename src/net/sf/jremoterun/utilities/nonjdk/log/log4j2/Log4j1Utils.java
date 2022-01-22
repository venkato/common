package net.sf.jremoterun.utilities.nonjdk.log.log4j2;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker;
import org.apache.log4j.Level;


public class Log4j1Utils {



    public static String sep = "\n";

    public static boolean printMsgOnError = true;

    public static Level logRootLevel = Level.INFO;

    public static Log4j1PatternLayout pl = new Log4j1PatternLayout();

    public static void setLog4jAppender() throws Exception {
        try {
            setLog4jAppenderImpl();
        } catch (Throwable e) {
            if (printMsgOnError) {
                System.err.println("Failed configure log4j2 : " + e);
                e.printStackTrace();
            }
            InitLogTracker.defaultTracker.addException("failed set appender for log4j1", e);
            throw e;
        }
    }

    public static void setLog4jAppenderImpl() throws Exception {
        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
        Log4jMigrateUtils.setLog4jAppender();
    }


}
