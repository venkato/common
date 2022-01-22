package net.sf.jremoterun.utilities.nonjdk.io

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class JrrIoUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static {
        JrrClassUtils.ignoreClassesForCurrentClass.add JrrClassUtils.getCurrentClass().getName()
    }

    public static Level defaultLogLevel = Level.WARNING

    static void closeQuietly(Closeable out) {
        closeQuietlyImpl(out, log, defaultLogLevel)
    }

    static void closeQuietly2(AutoCloseable out, Logger logger) {
        closeQuietlyImpl(out, logger, defaultLogLevel)
    }

    static void closeQuietly2(Closeable out, Logger logger) {
        closeQuietlyImpl(out, logger, defaultLogLevel)
    }

    static void closeQuietlyImpl(AutoCloseable out, Logger logger, Level level) {
        if (out != null) {
            try {
                out.close()
            } catch (Throwable e) {
                logger.log(level, "failed close", e)
            }
        }
    }

    static void closeQuietlyImpl(Closeable out, Logger logger, Level level) {
        if (out != null) {
            try {
                out.close()
            } catch (Throwable e) {
                logger.log(level, "failed close", e)
            }
        }
    }

}
