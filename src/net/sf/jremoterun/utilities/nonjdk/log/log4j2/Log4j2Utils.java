package net.sf.jremoterun.utilities.nonjdk.log.log4j2;

import javassist.CtClass;
import javassist.CtMethod;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.javaonly.InitInfo;
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils;
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker;
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClassPathTesterHelper2;
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLoggerSwitch;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.AppenderControl;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.filter.CompositeFilter;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.ThreadContextMapFactory;

import java.util.Collection;
import java.util.logging.Logger;


public class Log4j2Utils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static String sep = "\n";

    public static boolean suppressChecks = false;
    public static boolean doChecksJdk = true;
    public static Level logRootLevel = Level.INFO;


    public static volatile CompositeFilter compositeFilter = CompositeFilter.createFilters(null);
    public static volatile Filter filterOrigin;

    public static org.apache.logging.log4j.core.Logger rootLogger;
    public static volatile boolean isTryUseColorLogger = true;

    public static volatile Log4j2PatternLayout pl;
    public static volatile boolean calledBefore = false;
    public static InitInfo initInfo = new InitInfo(Log4j2Utils.class);

    public static void setLog4jAppender() throws Exception {
        if(initInfo.isInited()){
            log.info(Log4j2Utils.class.getSimpleName()+" already called before");
        }else {
            try {
                setLog4jAppenderImpl();
                //calledBefore = true;
                initInfo.setInited();
            } catch (Throwable e) {
                InitLogTracker.defaultTracker.addException("failed set appender for log4j1", e);
                throw e;
            }
        }
    }






    public static void setLog4jAppenderImpl() throws Exception {
        if(doChecksJdk){
            if(JdkLoggerSwitch.useJdkLogger){
                throw new Exception("log4j2 logger not allowed");
            }
        }
        if (suppressChecks) {
            suppressIsAppenderCalled();
        }
        checkAndFixFactory();
        initLogPatternLayout();
        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
        ConsoleAppender ca = ConsoleAppender.createDefaultAppenderForLayout(pl);
        org.apache.logging.log4j.Logger rootLogger3 = LogManager.getRootLogger();
        ClassPathTesterHelper2.createClassPathTesterHelper2().checkClassInstanceOf5(rootLogger3, org.apache.logging.log4j.core.Logger.class);
        rootLogger = (org.apache.logging.log4j.core.Logger) rootLogger3;
        LoggerConfig loggerConfig = rootLogger.get();
        loggerConfig.addFilter(compositeFilter);
        loggerConfig.setLevel(logRootLevel);
        Collection<Appender> appenders = rootLogger.getAppenders().values();
        for (Appender appender : appenders) {
            rootLogger.removeAppender(appender);
        }
        ca.start();
        rootLogger.addAppender(ca);
    }

    public static void checkAndFixFactory() throws Exception {
        System.setProperty(LogManager.FACTORY_PROPERTY_NAME, Log4jContextFactory.class.getName());
        System.setProperty((String) JrrClassUtils.getFieldValue(ThreadContextMapFactory.class, "THREAD_CONTEXT_KEY"), DefaultThreadContextMap.class.getName());
        boolean needSetFactory = false;
        LoggerContextFactory factory = (LoggerContextFactory) JrrClassUtils.getFieldValue(LogManager.class, "factory");
        if (factory == null) {
            log.info("factory is null");
            needSetFactory = true;
        } else if (factory.getClass() != Log4jContextFactory.class) {
            log.info("factory strange : " + factory.getClass().getName());
            needSetFactory = true;
        } else {

        }
        if (needSetFactory) {
            factory = new org.apache.logging.log4j.core.impl.Log4jContextFactory();
            JrrClassUtils.setFieldValue(LogManager.class, "factory", factory);
        }
    }

    public static void initLogPatternLayout(){
        if(pl==null) {
            if (isTryUseColorLogger) {
                try {
                    Log4j2Utils.class.getClassLoader().loadClass("org.fusesource.jansi.Ansi$Color");
                    pl = new Log4j2ColorPatternLayout();
                } catch (ClassNotFoundException e) {
                    log.log(java.util.logging.Level.INFO, "failed find ansi color", e);
                    pl = new Log4j2PatternLayout();
                }
            } else {
                pl = new Log4j2PatternLayout();
            }
        }else{
            log.info("pattern logger already set");
        }
    }


    public static void suppressIsAppenderCalled() throws Exception {
        InitLogTracker.defaultTracker.addLog("suppressing isRecursiveCall");
        Class clazz = AppenderControl.class;
        log.info("log4j class location = " + JrrUtils.getClassLocation(clazz) + " " + clazz.getClassLoader());
        CtClass ctClazz = JrrJavassistUtils.getClassFromDefaultPool(clazz);
        CtMethod method = JrrJavassistUtils.findMethodByCount(AppenderControl.class, ctClazz, "isRecursiveCall", 0);
        method.setBody("return false;");
        JrrJavassistUtils.redefineClass(ctClazz, clazz);
        log.info("class redefined : " + clazz.getName());
    }

    public static void setLogLevelLog4j2(Class clazz, org.apache.logging.log4j.Level level2) {
        Configurator.setLevel(clazz.getName(), level2);
    }

    public static void setLogLevel(String loggerName, org.apache.log4j.Level level) {
        org.apache.log4j.Logger.getLogger(loggerName).setLevel(level);
        Level level2 = Log4jIntoLog4j2Converter.log4j1ToLog4j2Map.get(level);
        Configurator.setLevel(loggerName, level2);
    }

    public static void setLogLevel(String loggerName, Level level2) {
        Configurator.setLevel(loggerName, level2);
    }


    static void checkLogger2() {
        LoggerContext context = LogManager.getContext(false);

    }

    public static void setLogLevel(Class clazz, org.apache.log4j.Level level) {
        setLogLevel(clazz.getName(), level);
    }

}
