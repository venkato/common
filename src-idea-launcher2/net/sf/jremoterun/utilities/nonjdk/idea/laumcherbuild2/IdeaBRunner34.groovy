package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable
import net.sf.jremoterun.utilities.javaservice.CallProxy
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBuildRunnerSettings
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.LauncherImpl
import net.sf.jremoterun.utilities.nonjdk.log.AddDefaultIgnoreClasses
import org.jetbrains.jps.cmdline.LauncherOriginal

import java.lang.management.ManagementFactory
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class IdeaBRunner34 implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void f1() {
        try {
            f2()
        } catch (Throwable e) {
            log.info("exc during compile : ${e}")
            throw e;
        }
    }

    static void f2() {
        String[] args = IdeaBuildRunnerSettings.argsPv2.toArray(new String[0])
        int pid = PidDetector.detectPid()
        log.info "running, pid =  ${pid} , ${new Date()}, "
        log.info "running args : ${args} "
        List<String> inputArguments = ManagementFactory.getRuntimeMXBean().getInputArguments()
        log.info "inputArguments = ${inputArguments}"
        long delay = System.currentTimeMillis() - IdeaBuildRunnerSettings.startDate.getTime()
        log.info "startup delay : ${delay / 1000} s"
        if (IdeaBuildRunnerSettings.beforeMainOriginalRun != null) {
            IdeaBuildRunnerSettings.beforeMainOriginalRun.run()
        }
        if (IdeaBuildRunnerSettings2.launcherOriginal) {
            LauncherOriginal.main(args)
        } else {
            f3(args)
        }
        //IdeaBuildRunnerSettings.afterMainOriginalRun.run()

//        mainMethod.invoke(null, new Object[] {jpsArgs});
    }

    static void allowDebugAll(){
        AddDefaultIgnoreClasses.addIgnoreClasses();
        net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter.findConsoleHandler().setLevel(Level.FINEST)
        Logger.getLogger('org.jetbrains.jps').setLevel(Level.ALL);
        Logger.getLogger('#org.jetbrains.jps').setLevel(Level.ALL);
//        Logger.getLogger('').setLevel(Level.FINEST);
    }

    static void allowDebugAll2(){
        AddDefaultIgnoreClasses.addIgnoreClasses();
        net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter.findConsoleHandler().setLevel(Level.FINEST)
        Logger.getLogger('').setLevel(Level.FINEST);
    }

    static void f3(String[] args) {
        Java11ModuleSetDisable.doIfNeeded()
        IdeaBuildRunnerSettings.originalTried = true;
        final String jpsClasspath = args[0];
        final String mainClassName = args[1];
        final String[] jpsArgs = new String[args.length - 2];
        System.arraycopy(args, 2, jpsArgs, 0, jpsArgs.length);

        final StringTokenizer tokenizer = new StringTokenizer(jpsClasspath, File.pathSeparator, false);
        final List<URL> urls = new ArrayList<>();
        IdeaBuildRunnerSettings2.prependFilesClassLoader.each {
            urls.add(it.toURI().toURL());
        }
        while (tokenizer.hasMoreTokens()) {
            final String path = tokenizer.nextToken();
            urls.add(new File(path).toURI().toURL());
        }
        IdeaBuildRunnerSettings2.appendFilesClassLoader.each {
            urls.add(it.toURI().toURL());
        }
        URL[] urlsArray = urls.toArray(new URL[urls.size()]) as URL[];
        ClassLoader parenetCl;
        if (IdeaBuildRunnerSettings2.useJrrClassloader) {
            parenetCl = LauncherImpl.getClassLoader()
        } else {
            parenetCl = LauncherImpl.getClassLoader().getParent()
            assert parenetCl != null
        }
        final URLClassLoader jpsLoader = new URLClassLoader(urlsArray, parenetCl);
        IdeaBuildRunnerSettings2.jpsLoader = jpsLoader
//        final GroovyClassLoader jpsLoader = JrrClassUtils.currentClassLoader as GroovyClassLoader;
//        urls.each {
//            jpsLoader.addURL(it)
//        }

        // IDEA-120811; speeding up DefaultChannelIDd calculation for netty
        //if (Boolean.parseBoolean(System.getProperty("io.netty.random.id"))) {
        System.setProperty("io.netty.machineId", "28:f0:76:ff:fe:16:65:0e");
        System.setProperty("io.netty.processId", Integer.toString(new Random().nextInt(65535)));
        System.setProperty("io.netty.serviceThreadPrefix", "Netty");
        //}
        IdeaBuiderSettings3 buiderSettings3 = IdeaBuildRunnerSettings2.buiderSettings3;
        buiderSettings3.args = jpsArgs;


        if (IdeaBuildRunnerSettings2.justBeforeOrigRun != null) {
            IdeaBuildRunnerSettings2.justBeforeOrigRun.run()
        }

//        final Class<?> mainClass = jpsLoader.loadClass(mainClassName);
        Thread.currentThread().setContextClassLoader(jpsLoader);
        IdeaMainBuilderI service = createIdeService(jpsLoader)
        service.created(buiderSettings3);
        //JrrClassUtils.runMainMethod(mainClass, jpsArgs)

    }


    public static IdeaMainBuilderI createIdeService(ClassLoader loader) throws Exception {
        ClRef clRef1 = new ClRef("net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild3.IdeaMainBuilder");
        Class compileRequestRemoteClass = loader.loadClass(clRef1.className);
//            assert compileRequestRemoteClass.classLoader == loader
        Object service = compileRequestRemoteClass.newInstance();
        IdeaMainBuilderI service2 = (IdeaMainBuilderI) CallProxy.makeProxy2(IdeaMainBuilderI.class, service);
        return service2;
    }

    @Override
    void run() {
        f1();
    }
}
