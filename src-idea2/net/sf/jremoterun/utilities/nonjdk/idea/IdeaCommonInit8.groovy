package net.sf.jremoterun.utilities.nonjdk.idea

import com.intellij.openapi.diagnostic.LogLevel
import com.intellij.openapi.extensions.PluginId
import groovy.transform.CompileStatic
import net.sf.jremoterun.SimpleJvmTiAgent
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.javaonly.InitInfo
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.InitGeneral
import net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyExtensionMethodsReloader
import net.sf.jremoterun.utilities.nonjdk.groovy.ExtentionMethodChecker2
import net.sf.jremoterun.utilities.nonjdk.idea.classpathhook.JavaClassPathHook
import net.sf.jremoterun.utilities.nonjdk.idea.init.ideainittracker.IdeaInitLogTracker
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkCustomLayout
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLogFormatter2
import net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLoggerSwitch

import java.util.logging.Logger

@CompileStatic
class IdeaCommonInit8 implements Runnable {
    //-Dintellij.log.stdout=false
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static InitInfo initInfo = new InitInfo(IdeaCommonInit8)

    static volatile boolean inInit = false;

    public static JdkCustomLayout proxyLogLayout = new JdkCustomLayout();
    public
    static List<String> ignoreClasses = ['java.', 'sun.', com.intellij.util.net.HttpConfigurable.getPackage().name, com.intellij.util.io.HttpRequests.name,];

    @Override
    void run() {
        init1()
    }

    static void init1() {
        log.info "in IdeaCommonInit init1 ${initInfo.isInited()} inInit=${inInit}"
        if (!initInfo.isInited() && !inInit) {
            try {
                inInit = true;
                init1Impl()
            } catch (Throwable e) {
                log.info("${e}");
                e.printStackTrace();
                throw e;
            } finally {
                inInit = false;
            }
            initInfo.setInited()
        }

    }

    public static boolean setLog4jFactory = false

    static void init1Impl() {
        SetConsoleOut2.setConsoleOutIfNotInited();
        log.info "in IdeaCommonInit.init1Impl"
        ClassLoader log4jClassLoader = new ClRef('org.apache.log4j.Level').loadClass2().getClassLoader();
        log.info "log4jClassLoader = ${log4jClassLoader}"
        if (log4jClassLoader instanceof com.intellij.ide.plugins.cl.PluginClassLoader) {
            com.intellij.ide.plugins.cl.PluginClassLoader clll = (com.intellij.ide.plugins.cl.PluginClassLoader) log4jClassLoader;
            PluginId pluginId = clll.getPluginId();
            log.info "log4jClassLoader classloader for log4j class pluin id ${pluginId} ";
        }
        if (log4jClassLoader == JrrClassUtils.getCurrentClassLoader()) {
            new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.IdeaRedefineClassloader')
            throw new Exception("Wrong classloader for log4j class ${log4jClassLoader}")
        }


        String ideaProxyLoggerName = "#" + new ClRef('com.intellij.util.proxy.CommonProxy').className;
        setIdeaLogLevel(ideaProxyLoggerName, java.util.logging.Level.FINE)

        setIdeaLogLevel("#" + new ClRef('com.intellij.compiler.server.BuildManager').className, java.util.logging.Level.WARNING);
        log.info "cp1"
        InitGeneral.init1()

        net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLoggerSetter.formatter.isLogExceptionStackTrace = new IsLogExceptionStackTraceIdea();

        GroovyExtensionMethodsReloader.rescanExtensionMethodsAll()
        LogExitTimeHook.addShutDownHook()
        if (true) {
            try {
                RedefineIdeaClassUtils.ideaLoggerTurnOff()
            } catch (NoSuchMethodException e) {
                log.info("method not found, seem idea is EE")
            }
        }

        GeneralUtils.startLogTimer()
        JrrClassUtils.ignoreClassesForCurrentClass.add(com.intellij.util.proxy.CommonProxy.getName())
        //JrrClassUtils.ignoreClassesForCurrentClass.add(net.sf.jremoterun.utilities.nonjdk.net.proxy.ProxySetter.getName())
        IdeaSetDependencyResolver3.setDepResolver()
        proxyLogLayout.additionalIgnore.addAll(ignoreClasses)
        JdkLogFormatter2.customLayouts.put(ideaProxyLoggerName, proxyLogLayout)
        String javaHome = System.getProperty("java.home")

        log.info("java home : ${javaHome}")
        String tmpDir = System.getProperty("java.io.tmpdir")
        log.info("tmp dir : ${tmpDir}")
        if (SimpleJvmTiAgent.instrumentation == null) {
            log.info("jvm instrumentation is null")
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("SimpleJvmTiAgent.instrumentation is null", new Exception("SimpleJvmTiAgent.instrumentation is null"))
        } else {
            JavaClassPathHook.installBothHooks()
            log.info "jvm hook redefined"
        }
        try {
            log.info "about to redefine jedi terminal"
            com.jpto.redefine.Terminal3Redefine.redefine3()
            log.info "jedi terminal redefined"
        } catch (Throwable e) {
            log.info("failed redefine terminal ${e}")
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed redefine terminal ${e}", e);
        }
        try {
            ExtentionMethodChecker2.check()
        } catch (Throwable e) {
            log.info("ExtentionMethodChecker2 failed ", e)
            JrrUtilitiesShowE.showException("ExtentionMethodChecker2 failed ", e)
        }


    }

    static void setIdeaLogLevel(Class clazz, java.util.logging.Level level) {
        setIdeaLogLevel("#${clazz.name}", level)
    }

    static void setIdeaLogLevel2(String loggerName, com.intellij.openapi.diagnostic.LogLevel level) {

    }

    static void setIdeaLogLevel(String loggerName, java.util.logging.Level level) {
//    static void setIdeaLogLevel(String loggerName, org.apache.log4j.Level level) {
        try {
            setIdeaLogLevelImpl(loggerName, level)
        } catch (Throwable e) {
            log.log(java.util.logging.Level.WARNING, "Failed set logger ${loggerName}", e)
            JrrUtilitiesShowE.showException("Failed set logger ${loggerName}", e)
            IdeaInitLogTracker.defaultTracker.addException("Failed set logger ${loggerName}", e)
        }
    }

    static void setIdeaLogLevelImpl(String loggerName, java.util.logging.Level level) {
//    static void setIdeaLogLevelImpl(String loggerName, org.apache.log4j.Level level) {

//        org.apache.logging.log4j.Level log4j2Leve =  Log4jOld.log4j1ToLog4j2Map.get(level)

        Logger.getLogger(loggerName).setLevel(level)

        com.intellij.openapi.diagnostic.Logger ll = com.intellij.openapi.diagnostic.Logger.getInstance(loggerName);
        LogLevel ideaLevel = Log4jLevelToIdeaLevel.jdk2idea.get(level)
        ll.setLevel(ideaLevel)
        //Log4jLevelToIdeaLevel.jdk2idea
        //Log4jLevelToIdeaLevel.jdk2idea
        //ll.setLevel(level)

        //Log4jLevelToIdeaLevel
    }

}
