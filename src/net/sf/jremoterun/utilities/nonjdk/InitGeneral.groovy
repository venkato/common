package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.SimpleJvmTiAgent
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable
import net.sf.jremoterun.utilities.javaonly.InitInfo
import net.sf.jremoterun.utilities.log4j.Log4jConfigurator
import net.sf.jremoterun.utilities.nonjdk.classpath.CheckNonCache2
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.compile.auxh.AddGroovyToParentClResolver
import net.sf.jremoterun.utilities.nonjdk.consoleprograms.SetConsoleColoring
import net.sf.jremoterun.utilities.nonjdk.ivy.JrrDependecyAmenderDefault
import net.sf.jremoterun.utilities.nonjdk.javassist.LoggigingRedefine
import net.sf.jremoterun.utilities.nonjdk.log.AddDefaultIgnoreClasses
import net.sf.jremoterun.utilities.nonjdk.settings.JrrUtilitiesSettings
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class InitGeneral {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean inInit = false;
    //public static volatile boolean inited = false;
    public static InitInfo initInfo = new InitInfo(InitGeneral);
    public static volatile boolean checkJavassistClassloader = false;
    public static volatile boolean useColorOutput = true;
    public static volatile boolean doRetransform = true;
    public static volatile ClRef helfyInit = new ClRef('net.sf.jremoterun.utilities.nonjdk.helfyutils.HelpfyRegister')
    public static volatile ClRef helfyCore = new ClRef('one.helfy.JVM')
    public static volatile InitGeneral initGeneral1 = new InitGeneral();

//    public static net.sf.jremoterun.utilities.nonjdk.javassist.retransform.JrrReTransform reTransform;

    static void init1() {
        // add charset : com.intellij.lang.properties.charset.Native2AsciiCharset
        if (!initInfo.isInited() && !inInit) {
            try {
                InitLogTracker.defaultTracker.addLog("start init at ${new Date()}");
                inInit = true;
                initGeneral1.init1impl()
                InitLogTracker.defaultTracker.addLog("finished init at ${new Date()}");
            } catch (Throwable e) {
                try {
//                log.log(Level.SEVERE, "failed init generic",e);
//                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed init", e)
                    InitLogTracker.defaultTracker.addException("failed init", e);
                } catch (Throwable e5) {
                    log.log(Level.SEVERE, "failed init suppress", e5);
                    //throw e;
                }
                throw e;
            } finally {
                inInit = false;
            }
            initInfo.setInited();
        }
    }

    static void checkJavassistClassloaderCorrectly() {
        String className = javassist.runtime.Desc.getName()
        ClassLoader javassistClassLoader = JrrClassUtils.currentClassLoader.loadClass(className).classLoader
        if (javassistClassLoader != null) {
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("javassist ClassLoader", new Exception("Class  ${className} loaded by not boot classloader : ${javassistClassLoader}"))
        }
    }

    void init1impl() {
        firstAction();
        InitLogTracker.defaultTracker.addLog("initing coloring")
        Java11ModuleSetDisable.doIfNeeded();
        InitLogTracker.defaultTracker.addLog("java11 module check disabled")
        SetConsoleOut2.setConsoleOutIfNotInited()
        if (useColorOutput) {
            try {
                getClass().getClassLoader().loadClass("org.fusesource.jansi.AnsiConsole")
                SetConsoleColoring.installAnsible()
            } catch (ClassNotFoundException e) {
                log.info("org.fusesource.jansi.AnsiConsole", e)
                InitLogTracker.defaultTracker.addLog("org.fusesource.jansi.AnsiConsole class not found")
            }
        }
        if (checkJavassistClassloader) {
            checkJavassistClassloaderCorrectly();
        }
//                ProxySelectorLogger.setProxySelectorWithJustLogging();
        AddDefaultIgnoreClasses.addIgnoreClasses()
//        JrrClassUtils.ignoreClassesForCurrentClass.add(Log4j1Utils.getPackage().getName());
        SettingsChecker.showAsSwing = true
        InitLogTracker.defaultTracker.addLog("setting log4j configurator")
        boolean registerLog4j1 = true
        try {
            new ClRef('org.apache.log4j.jmx.HierarchyDynamicMBean').loadClass2()
            Log4jConfigurator.registerLog4jConfiguratorAndMbeans()
        } catch (ClassNotFoundException e) {
            registerLog4j1 = false;
            log.log(Level.FINE, "failed load log4j", e)
        } catch (Exception e) {
            registerLog4j1 = false;
            InitLogTracker.defaultTracker.addException("failed log4j mbean register", e)
            log.log(Level.WARNING, "failed regiter log4j", e)
        }
        InitLogTracker.defaultTracker.addLog("register log4j1 = " + registerLog4j1);
        if (registerLog4j1) {
            try {
                Log4jConfigurator.registerLog4jConfiguratorAndMbeans()
            } catch (Exception e) {
                InitLogTracker.defaultTracker.addException("failed log4j mbean register", e)
                Throwable e2 = JrrUtils.getRootException(e);
                log.log(Level.WARNING, "failed regiter log4j", e2)
            }
        }
        InitLogTracker.defaultTracker.addLog("setting logging")
        setLogging()
        InitLogTracker.defaultTracker.addLog("exception handler setting");
        SimpleUncaughtExceptionHandler.setDefaultUncaughtExceptionHandler()
        if (!SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)) {
            if (SimpleJvmTiAgent.instrumentation != null) {
                net.sf.jremoterun.utilities.nonjdk.serviceloader.ServiceLoaderStorage.initIfCan()
            }
        }
        int pid = PidDetector.detectPid();
        log.info "pid = ${pid}"
//                if (MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver == null) {
        InitLogTracker.defaultTracker.addLog("setting resolver");
        JrrDependecyAmenderDefault.setResolverAmender();
        net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl.setManyRepoLoader();
        if (net.sf.jremoterun.utilities.nonjdk.ivy.JrrIvyURLHandler.isStdDefaultClient()) {
            net.sf.jremoterun.utilities.nonjdk.ivy.JrrIvyURLHandler.setHandler()
        }
        //IvyDepResolver2.setDepResolver()
//                }
        //SourceCompletionProvider.loadPrivateMemberAlways = true
        InitLogTracker.defaultTracker.addLog("setting default classloader for remote code execution");
        SharedObjectsUtils.getClassLoaders().put(JrrUtilitiesSettings.generalInitCLassLoaderId, InitGeneral.classLoader);
        if (SimpleFindParentClassLoader.getDefaultClassLoader() == ClassLoader.getSystemClassLoader()) {
            SimpleFindParentClassLoader.setDefaultClassLoader(InitGeneral.classLoader)
        }
        checkMavenUrlInDepResolver()
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_8)) {
            try {
                helfyCore.loadClass2()
                RunnableFactory.runRunner helfyInit
            } catch (ClassNotFoundException e) {
                log.info("failed load class ${helfyCore} ${e}")
            }
        }
        afterDependencySet()
        initifFrameworkDir()
        if (doRetransform) {
            boolean doRetransform2 = true
            try {
                new ClRef('org.objectweb.asm.ClassWriter').loadClass2()
                new ClRef('org.objectweb.asm.tree.analysis.SimpleVerifier').loadClass2()
            } catch (ClassNotFoundException e) {
                log.info("failed find asm lib, skip re transform : ${e}")
                doRetransform2 = false
                InitLogTracker.defaultTracker.addException("failed find asm lib, skip re transform", e)
            }
            if (doRetransform2) {
                net.sf.jremoterun.utilities.nonjdk.javassist.retransform.JrrReTransform.addSelfIfCanS()
            }
        }
        if (SimpleJvmTiAgent.instrumentation == null) {
            log.info("SimpleJvmTiAgent.instrumentation is null, skip redefine Jsch")
        } else {
            try {
                new net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.JschRequestRed().redefineRequest();
            } catch (ClassNotFoundException e) {
                log.info("failed load JschRequest class", e)
            }
        }
        new net.sf.jremoterun.utilities.nonjdk.str2obj.StringToEnumConverter2().register()
        InitLogTracker.defaultTracker.addLog("init general done fine");
        lastAction()
    }


    void initifFrameworkDir() {
        if (InfocationFrameworkStructure.ifDir == null) {
            InfocationFrameworkStructure.ifDir = GitSomeRefs.commonUtil.resolveToFile()
            InfocationFrameworkStructure.ifCommonDir = GitSomeRefs.commonUtil.resolveToFile()
        }
    }

    void firstAction() {

    }

    void setLogging() {
        InitLogTracker.defaultTracker.addLog("setting log4j2 appender")

        net.sf.jremoterun.utilities.nonjdk.log.tojdk.JdkLoggerSetter.setLogFormatter()
        Class classLog4j2
        try {
            classLog4j2 = new ClRef('org.apache.logging.log4j.Logger').loadClass2()

        } catch (ClassNotFoundException e) {
            log.info('', e)
            InitLogTracker.defaultTracker.addLog("setting log4j2 not found " + e)
        }
        if (classLog4j2 != null) {
            RunnableFactory.runRunner new ClRef('net.sf.jremoterun.utilities.nonjdk.log.tojdk.Log4j2Utils')
        }

        InitLogTracker.defaultTracker.addLog("setting log4j1 appender")
        Class classLog4j1
        try {
            classLog4j1 = new ClRef('org.apache.log4j.jmx.HierarchyDynamicMBean').loadClass2()
        } catch (ClassNotFoundException e) {
            log.info('', e)
            InitLogTracker.defaultTracker.addLog("setting log4j1 not found " + e)
        }
        if (classLog4j1 != null) {
            net.sf.jremoterun.utilities.nonjdk.log.tojdk.Log4jMigrateUtils.setLog4jAppender()
        }
        InitLogTracker.defaultTracker.addLog("setting jdk log appender")

        CheckNonCache2.check();
        CheckNonCache2.check(JrrUtilitiesFile);

        LoggigingRedefine.redifineLoggingGetLog();
        GeneralUtils.startLogTimer()
        InitLogTracker.defaultTracker.addLog("logging set fine");
    }

    void afterDependencySet() {
        AddGroovyToParentClResolver.setRef();
    }

    void lastAction() {

    }

    void checkMavenUrlInDepResolver() {
        MavenDefaultSettings mds = MavenDefaultSettings.mavenDefaultSettings;
        MavenDependenciesResolver resolver = mds.mavenDependenciesResolver;
        if (resolver == null) {
            String msg = "mavenDependenciesResolver undefined"
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(msg, new Exception("${msg}"))
        } else {
            String urlInDepResolver = resolver.getMavenRepoUrl().toString()
            if (!urlInDepResolver.endsWith('/')) {
                urlInDepResolver += '/'
            }
            String urlExpected = mds.mavenServer;
            if (!urlExpected.endsWith('/')) {
                urlExpected += '/'
            }
            if (urlInDepResolver != urlExpected) {
                String msg = "Wrong maven server in dep resolver : ${urlInDepResolver}"
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException(msg, new Exception("${msg} , expected = ${urlExpected}"))
            }
        }

    }


}
