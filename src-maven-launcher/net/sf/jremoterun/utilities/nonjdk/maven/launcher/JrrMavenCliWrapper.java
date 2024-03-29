package net.sf.jremoterun.utilities.nonjdk.maven.launcher;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.FileOutputStream2;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy;
import net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2;
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory;
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2;
import net.sf.jremoterun.utilities.java11.sep1.Java11ModuleAccessF;
import net.sf.jremoterun.utilities.nonjdk.RedirectOutStream;
import org.apache.maven.shared.utils.logging.MessageUtils;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;

// set main is net.sf.jremoterun.utilities.nonjdk.maven.launcher.JrrMavenCliWrapper from plexus.core
// in bin/m2.conf
// and add : load start jars, onejar and ifframework jar . Accept dirs
@CompileStatic
public class JrrMavenCliWrapper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static int cnt1 = 15;
    public static List<String> args1;
    public static org.codehaus.plexus.classworlds.realm.ClassRealm classLoaderRealmThis;
    public static ClassWorld classWorld1;
    public static JrrMavenCli cli;
    public static Runnable afterToolChainSet;
//    public static CliRequestJrr cliRequestPublic;
    //public static ExecutionListenerMavenProxy executionListenerProxy = new ExecutionListenerMavenProxy();

    public static AddFilesToUrlClassLoaderGroovy adder;
    public static List<Runnable> runBefore = new Vector<>();
    public static List<Runnable> runAfter = new Vector<>();
    public static List<Runnable> runAfterUsingFinally = new Vector<>();
    public static Date startDate = new Date();
    public static volatile int result;
    public static String coreRealm = "plexus.core";
    public static String mavenRunnerProp = "jrr.maven.runner";
    public static String mavenHomeProp = "maven.home";
    public static String mavenRunnerFileName = "mavenLauncher.groovy";
    public static volatile JrrMavenCliWrapper jrrMavenCliWrapper = new JrrMavenCliWrapper();
    public static volatile Throwable exception;
    public static volatile FileOutputStream2 outFileStream;


//    public static void main(String[] args) {
//        log.info("wrong method used !");
//        org.apache.maven.cli.MavenCli.main(args);
//    }


    public static int main(String[] args, ClassWorld classWorld) throws Exception {
        cnt1 = 19;
        args1 = new ArrayList<>(Arrays.asList(args));
        classWorld1 = classWorld;
        disableJavaModuleCheck();
        classLoaderRealmThis = (ClassRealm) JrrClassUtils.getCurrentClassLoader();
        setAdder2();
        f1();
        return result;
    }

    public static void disableJavaModuleCheck() throws NoSuchFieldException, InvocationTargetException, IllegalAccessException {
        Java11ModuleAccessF.disableJavaModuleCheckIfNeeded();
    }


    public static File getMavenHome() throws IOException {
        String mavenHome = System.getProperty(mavenHomeProp);
        if (mavenHome == null) {
            throw new IllegalStateException(mavenHomeProp);
        }
        File mavenHome2 = new File(mavenHome);
        if (!mavenHome2.exists()) {
            throw new FileNotFoundException(mavenHome);
        }
        return mavenHome2.getAbsoluteFile().getCanonicalFile();
    }

    public static FileOutputStream2 redirectMavenLogs(int maxCount) throws Exception {
        File mavenHome = getMavenHome();
        File logDir = new File(mavenHome, "logs");
        if (!logDir.exists()) {
            log.info("creating dir : " + logDir.getAbsolutePath());
            if (!logDir.mkdir()) {
                throw new IOException("Failed create dir : " + logDir.getAbsolutePath());
            }
        }
        File outFile = new File(logDir, "out.txt");
        outFileStream =  RedirectOutStream.setOutStreamWithRotation(outFile, maxCount);
        return outFileStream;
    }


    public static File calcRunnerFile() {
        String mavenRunner = System.getProperty(mavenRunnerProp);
        if (mavenRunner != null) {
            File file = new File(mavenRunner);
            if (!file.exists()) {
                throw new RuntimeException("file not found : " + file.getAbsolutePath());
            }
            return file;
        }
        File configRaw = new File(JrrStarterVariables2.getInstance().filesDir, mavenRunnerFileName);
        if (configRaw.exists()) {
            return configRaw;
        } else {
            log.info(" skip jrr init as file not found : " + configRaw.getAbsolutePath());
        }
        return null;
    }

    public static void setAdder2() {
        adder = new AddFilesToUrlClassLoaderGroovy(findCoreRealm());
        if (JrrStarterVariables2.getInstance().classesDir != null) {
            if (JrrStarterVariables2.getInstance().classesDir.exists()) {
                adder.add(JrrStarterVariables2.getInstance().classesDir);
            } else {
                log.info("skip add dir : " + JrrStarterVariables2.getInstance().classesDir);
            }
        }
    }

    public static org.codehaus.plexus.classworlds.realm.ClassRealm findCoreRealm() {
        ClassRealm classRealm = classWorld1.getClassRealm(coreRealm);
        if (classRealm == null) {
            throw new RuntimeException("Realm not found : " + coreRealm);
        }
        return classRealm;
    }


    public static void f1() throws Exception {
        SetConsoleOut2.setConsoleOutIfNotInited();
        File calcRunnerFile = calcRunnerFile();
        if (calcRunnerFile != null) {
            RunnableFactory.createRunner(calcRunnerFile).run();
//            new JrrGroovyScriptRunner(JrrClassUtils.getCurrentClassLoader()).loadSettingsNoParam(file);
        }
        jrrMavenCliWrapper.mainRunner();
    }

    public void mainRunner() throws Exception {
        try {
            jrrMavenCliWrapper.createCli();
            jrrMavenCliWrapper.jrrSystemInstall();
            jrrMavenCliWrapper.createCliRequest();
            jrrMavenCliWrapper.doRunRunnable(runBefore);
            jrrMavenCliWrapper.mainRunnerImpl();
            jrrMavenCliWrapper.doRunRunnable(runAfter);
            jrrMavenCliWrapper.jrrSystemUninstall();
        } catch (Throwable e) {
            exception = e;
            JrrUtils.throwThrowable(e);
        } finally {
            jrrMavenCliWrapper.doRunRunnable(runAfterUsingFinally);
        }
    }

    public void doRunRunnable(Runnable r) {
        if (r != null) {
            r.run();
        }
    }

    public void doRunRunnable(List<Runnable> rList) {
        for (Runnable r : rList) {
            r.run();
        }
    }

    public void createCli() {
        if (cli == null) {
            cli = new JrrMavenCli();
        }
    }


    public void createCliRequest() {
        cli.cliRequestPublic = new CliRequestJrr(JrrMavenCliWrapper.args1.toArray(new String[0]), JrrMavenCliWrapper.classWorld1);
        cli.cliRequestPublic.setRequest(cli.mavenExecutionRequestJrr);

        //cli.mavenExecutionRequestJrr.setExecutionListener(executionListenerProxy);
    }

    public void jrrSystemInstall() {
        MessageUtils.systemInstall();
        MessageUtils.registerShutdownHook();
    }

    public void mainRunnerImpl() {
        JrrMavenCliWrapper.result = cli.doMain(cli.cliRequestPublic);
    }

    public void jrrSystemUninstall() {
        MessageUtils.systemUninstall();
    }
}
