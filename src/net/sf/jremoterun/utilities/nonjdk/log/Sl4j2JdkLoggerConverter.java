package net.sf.jremoterun.utilities.nonjdk.log;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef;

import org.slf4j.ILoggerFactory;
import org.slf4j.jul.JULServiceProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Sl4j2JdkLoggerConverter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static ClRef clRef1 = new ClRef("org.slf4j.impl.JDK14LoggerFactory");

    public static void setSl4jLoggerToJDK14LoggerAll() throws Exception {
        try {
            setSl4jLoggerToJDK14Logger1();
        } catch (ClassNotFoundException e) {
            log.log(Level.SEVERE, "failed set jdk logger1", e);
            throw e;
        }
        setSl4jLoggerToJDK14Logger2();
        Sl4jLoggerCommon.setStatusInited();
    }

    public static void setSl4jLoggerToJDK14Logger1() throws Exception {
        org.slf4j.ILoggerFactory loggerFactory = (ILoggerFactory) clRef1.newInstance3();
        Sl4jLoggerCommon.setLoggerImpl(loggerFactory);
    }

    public static void setSl4jLoggerToJDK14Logger2() throws NoSuchFieldException, IllegalAccessException {
        JULServiceProvider julServiceProvider = new JULServiceProvider();
        Sl4jLoggerCommon2.setLoggerImpl2(julServiceProvider);
    }

}
