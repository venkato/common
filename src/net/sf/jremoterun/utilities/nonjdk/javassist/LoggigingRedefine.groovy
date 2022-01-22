package net.sf.jremoterun.utilities.nonjdk.javassist

import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtMethod
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.SimpleJvmTiAgent
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker
import org.apache.commons.logging.LogFactory
import org.slf4j.LoggerFactory
import org.slf4j.impl.StaticLoggerBinder

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class LoggigingRedefine {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    private static volatile boolean initDone = false;
    public static volatile boolean commonsLoggingRedefined = false
    public static volatile boolean sl4jLoggingRedefined = false

    static void init() throws Exception {
        if (!initDone) {
            initDone = true;
            ClassRedefintions.init()
        }
    }


    public static void redifineLoggingGetLog() {
        try {
            init();
        } catch (Exception e1) {
            InitLogTracker.defaultTracker.addException("failed redefine logging", e1);
            log.log(Level.WARNING, "failed redefine logging", e1);
            return;
        }

        URL location1 = JrrUtils.getClassLocation(LogFactory)
        try {
            URL location = location1
            log.info "commons log location ${location}"
            if (SimpleJvmTiAgent.instrumentation != null) {
                redifineCommonsLoggingGetLog();
                commonsLoggingRedefined = true
            }
        } catch (Throwable e) {
            log.log(Level.WARNING, "failed redine commons LogFactory from ${location1}", e);
            InitLogTracker.defaultTracker.addException("failed redine commons LogFactory from ${location1}", e);
        }
        Class clzzzz
        URL location
        try {
            clzzzz= new ClRef('org.slf4j.impl.StaticLoggerBinder').loadClass2()
        } catch (ClassNotFoundException e) {

        }
        if(clzzzz!=null) {
            location = JrrUtils.getClassLocation(clzzzz)
            log.info "sl4j log location ${location}"
            if (location != null && location.toString().contains("logback-classic")) {
                log.info "failed redefine sl4j logger : used logback ${location}"
            } else {
                try {
                    if (SimpleJvmTiAgent.instrumentation != null) {
                        redifineSl4jLoggingGetLogViaJdk();
                        sl4jLoggingRedefined = true
                        InitLogTracker.defaultTracker.addLog("slf4j redefined");
                    }
                } catch (Throwable e) {
                    log.log(Level.WARNING, "failed redefine sl4j StaticLoggerBinder from ${location}", e);
                    InitLogTracker.defaultTracker.addException("failed redefine sl4j StaticLoggerBinder from ${location}", e);
                }
            }
        }

    }

    public static void redifineCommonsLoggingGetLog() throws Exception {
        init();
        net.sf.jremoterun.utilities.nonjdk.log.tojdk.JavaCommonsLogger.setCommonsLoggerToLog4j2();
        Class class1 = LogFactory;
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1);
        final CtMethod method = JrrJavassistUtils.findMethodByCount(class1, cc, "getFactory", 0);
        method.setBody """  
                return nullClassLoaderFactory;
            """;
        JrrJavassistUtils.redefineClass(cc, class1);
        LogFactory.getLog("test");
    }

    public static void redifineSl4jLoggingGetLog() throws Exception {
        net.sf.jremoterun.utilities.nonjdk.log.tojdk.Sl4jLogger.setSl4jLoggerToLog4j2();
        Class class1 = LoggerFactory;
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1);
        final CtMethod method = JrrJavassistUtils.findMethodByCount(class1, cc, "getILoggerFactory", 0);

        method.setBody """
           return ${org.slf4j.impl.StaticLoggerBinder.getName()}.getSingleton().getLoggerFactory();

            """;
        JrrJavassistUtils.redefineClass(cc, class1);
        LoggerFactory.getLogger("test");
    }


    public static void redifineSl4jLoggingGetLogViaJdk() throws Exception {
        ClRef ref1 = new ClRef('org.slf4j.jul.JDK14LoggerFactory')
        ref1.loadClass(LoggerFactory.getClassLoader())
        Class class1 = LoggerFactory;
        final CtClass cc = JrrJavassistUtils.getClassFromDefaultPool(class1);
        final CtMethod method = JrrJavassistUtils.findMethodByCount(class1, cc, "getILoggerFactory", 0);

        method.setBody """
           return new ${ref1.className}();

            """;
        JrrJavassistUtils.redefineClass(cc, class1);
        LoggerFactory.getLogger("test");
    }

}
