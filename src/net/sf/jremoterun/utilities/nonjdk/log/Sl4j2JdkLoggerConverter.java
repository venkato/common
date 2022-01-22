package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.classpath.ClRef;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.ILoggerFactory;
import org.slf4j.jul.JULServiceProvider;

public class Sl4j2JdkLoggerConverter {
    private static final Logger log = LogManager.getLogger();

    public static ClRef clRef1 = new ClRef("org.slf4j.impl.JDK14LoggerFactory");

    public static void setSl4jLoggerToJDK14LoggerAll() throws NoSuchFieldException, IllegalAccessException, ClassNotFoundException {
        try {
            setSl4jLoggerToJDK14Logger1();
        } catch (ClassNotFoundException e) {
            log.log(Level.ERROR, "failed set jdk logger1", e);
            throw e;
        }
        setSl4jLoggerToJDK14Logger2();
        Sl4jLoggerCommon.setStatusInited();
    }

    public static void setSl4jLoggerToJDK14Logger1() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ClassNotFoundException {
        org.slf4j.ILoggerFactory loggerFactory = (ILoggerFactory) clRef1.newInstance3();
        Sl4jLoggerCommon.setLoggerImpl(loggerFactory);
    }

    public static void setSl4jLoggerToJDK14Logger2() throws NoSuchFieldException, IllegalAccessException {
        JULServiceProvider julServiceProvider = new JULServiceProvider();
        Sl4jLoggerCommon2.setLoggerImpl2(julServiceProvider);
    }

}
