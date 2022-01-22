package net.sf.jremoterun.utilities.nonjdk.log.tojdk;

import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.ClRef;
import net.sf.jremoterun.utilities.javaonly.InitInfo;
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker;
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.ClassPathTesterHelper2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.DefaultThreadContextMap;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.spi.ThreadContextMapFactory;

import java.util.logging.Logger;


public class Log4j2Utils implements Runnable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static String sep = "\n";

    //public static org.apache.logging.log4j.Logger rootLogger;

    public static InitInfo initInfo = new InitInfo(Log4j2Utils.class);

    public static void setLog4jAppender() throws Exception {
        if(initInfo.isInited()){
            log.info(Log4j2Utils.class.getSimpleName()+" already called before");
        }else {
            try {
                setLog4jAppenderImpl();
                initInfo.setInited();
            } catch (Throwable e) {
                InitLogTracker.defaultTracker.addException("failed set appender for log4j1", e);
                throw e;
            }
        }
    }


    @Override
    public void run() {
        try {
            setLog4jAppender();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLog4jAppenderImpl() throws Exception {
        checkAndFixFactory();
        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j2Utils.class.getPackage().getName());
        checkLogger2();
        org.apache.logging.log4j.Logger rootLogger3 = LogManager.getRootLogger();
        ClassPathTesterHelper2.createClassPathTesterHelper2().checkClassInstanceOf5(rootLogger3, new ClRef("org.apache.logging.log4j.tojul.JULLogger").loadClass2());
      //  rootLogger = rootLogger3;
   }

    public static void checkAndFixFactory() throws Exception {
        System.setProperty(LogManager.FACTORY_PROPERTY_NAME, JULLoggerContextFactory2 .class.getName());
        System.setProperty((String) JrrClassUtils.getFieldValue(ThreadContextMapFactory.class, "THREAD_CONTEXT_KEY"), DefaultThreadContextMap.class.getName());
        boolean needSetFactory = false;
        LoggerContextFactory factory = (LoggerContextFactory) JrrClassUtils.getFieldValue(LogManager.class, "factory");
        if (factory == null) {
            log.info("factory is null");
            needSetFactory = true;
        } else if (factory.getClass() != JULLoggerContextFactory2.class) {
            log.info("factory strange : " + factory.getClass().getName());
            needSetFactory = true;
        } else {

        }
        log.info("needSetFactory = " +needSetFactory);
        if (needSetFactory) {
            factory = new JULLoggerContextFactory2();
            JrrClassUtils.setFieldValue(LogManager.class, "factory", factory);
        }
    }



    static void checkLogger2() {
        LoggerContext context = LogManager.getContext(false);
        if(context==null){
            throw new NullPointerException("conetxt is null");
        }
    }


}
