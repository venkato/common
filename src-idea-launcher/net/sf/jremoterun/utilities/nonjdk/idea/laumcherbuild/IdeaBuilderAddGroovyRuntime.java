package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.logging.Logger;

public class IdeaBuilderAddGroovyRuntime {

    private static final Logger log = Logger.getLogger(IdeaBuilderAddGroovyRuntime.class.getName());


    public static void doRun() throws Exception {

        if (IdeaBuildRunnerSettings.jrrIdeaForceUseStd) {
            log.info("force use LauncherOriginal");
            LauncherImpl.runOriginal();
        } else {
            doJobImpl();
        }
    }


    @Deprecated
    public static void doRedirect() throws Exception {
        IdeaBuilderAddGroovyRuntime1.doRedirect();
    }

    public static void doJobImpl() throws Exception {
        log.info("starting ...");
        if (IdeaBuildRunnerSettings.jrrPrintJavaHome) {
            log.info("java home = " + System.getProperty("java.home"));
        }
        IdeaBuildRunnerSettings.jrrpathF = detectJrrPath();
        log.info("jrrpath = " + IdeaBuildRunnerSettings.jrrpathF);
        if (IdeaBuildRunnerSettings.jrrpathF == null) {
            log.severe("jrrpath is null ");
            if (IdeaBuildRunnerSettings.startOriginal) {
                runOriginal();
            } else {
                throw new Exception("jrrpath is null ");
            }
        } else {
//        jrrpath = jrrPathDefault.getAbsolutePath();
            if (!IdeaBuildRunnerSettings.jrrpathF.exists()) {
                throw new FileNotFoundException(IdeaBuildRunnerSettings.jrrpathF.getAbsolutePath());
            }
            log.info("cp3");
            addJrrLibsAndRun(IdeaBuildRunnerSettings.jrrpathF);
            log.info("finished fine");
        }
    }

    @Deprecated
    public static void runOriginal() throws Exception {
        LauncherImpl.runOriginal();
    }

    public static File detectJrrPath() throws Exception {
        String jrrpath = System.getProperty(IdeaBuildRunnerSettings.jrrpathS);
        log.info("jrrpath sys prop = " + jrrpath);
        if (jrrpath != null) {
            File f = new File(jrrpath);
            if (!f.exists()) {
                throw new FileNotFoundException(f.getAbsolutePath());
            }
            return f;
        }
        boolean jrrlibpathE = IdeaBuildRunnerSettings.jrrlibpathF.exists();
        log.info("jrrlibpath exit : " + jrrlibpathE + " " + IdeaBuildRunnerSettings.jrrlibpathF);
        if (jrrlibpathE) {
            FileInputStream fis = new FileInputStream(IdeaBuildRunnerSettings.jrrlibpathF);
            try {
                byte[] buff = new byte[10000];
                int read = fis.read(buff);
                if (read < 2) {
                    throw new IOException("Failed read : " + IdeaBuildRunnerSettings.jrrlibpathF.getAbsolutePath());
                }
                String pathFromFileS = new String(buff, 0, read).trim();
                log.info("jrr path from file : " + pathFromFileS);
                File jrrLibPath2 = new File(pathFromFileS);
                if (!jrrLibPath2.exists()) {
                    throw new FileNotFoundException(jrrLibPath2.getAbsolutePath());
                }
                return jrrLibPath2;
            } finally {
                fis.close();
            }
        }
        return null;
    }

    static void addJrrLibsAndRun(File jrrpath) throws Exception {
        ClassLoader classLoader = IdeaBuilderAddGroovyRuntime.class.getClassLoader();
        File copyDir = new File(jrrpath, "libs/copy");
        File f2 = new File(copyDir, "groovy_custom.jar");
        File f3 = new File(copyDir, "groovy.jar");
        addUrlToCl(classLoader, new File(copyDir, "java11base.jar"));
        addUrlToCl(classLoader, f2);
        addUrlToCl(classLoader, f3);
        log.info("creating groovy cl");
        Class<?> aClass = classLoader.loadClass("groovy.lang.GroovyClassLoader");
        Constructor<?> constructor = aClass.getConstructor(ClassLoader.class);
        IdeaBuildRunnerSettings.groovyCl = (URLClassLoader) constructor.newInstance(classLoader);

        addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(copyDir, "jremoterun.jar"));
        addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(copyDir, "jrrassist.jar"));
        addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(copyDir, "jrrbasetypes.jar"));
        addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(jrrpath, "JrrInit/src"));
        if (IdeaBuildRunnerSettings.useOneJar) {
            addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(jrrpath, "onejar/jrrutilities.jar"));
        } else {
            addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(jrrpath, "JrrUtilities/src"));
            addUrlToCl(IdeaBuildRunnerSettings.groovyCl, new File(jrrpath, "JrrStarter/src"));
        }
        log.info("jars added to groovy cl");
        Thread.currentThread().setContextClassLoader(IdeaBuildRunnerSettings.groovyCl);
        Class<?> aClass1 = IdeaBuildRunnerSettings.groovyCl.loadClass("net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBRunnerImpl");
        Runnable o = (Runnable) aClass1.newInstance();
        log.info("running " + aClass1);
        o.run();
    }


    public static Method addUrlM;
    public static Method addUrlMJava11;

    /**
     * @see net.sf.jremoterun.utilities.UrlCLassLoaderUtils
     * @see net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.IdeaBRunner21#usedIn
     */
    public static void addUrlToCl(ClassLoader clObject, File fileToAdd) throws Exception {
        log.info("adding to CL : " + fileToAdd);
        if (!fileToAdd.exists()) {
            throw new FileNotFoundException(fileToAdd.getAbsolutePath());
        }
        if (clObject instanceof URLClassLoader) {
            if (addUrlM == null) {
                addUrlM = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                addUrlM.setAccessible(true);
            }
            addUrlM.invoke(clObject, fileToAdd.toURL());
        } else {
            //System.out.println("java11Used = "+java11Used);
            //if (java11Used) {
//                if (addUrlMJava11 == null) {
                    addUrlToClForJava11(clObject, fileToAdd);
//                } else {
//                    Class cll = clObject.loadClass("jdk.internal.loader.BuiltinClassLoader");
//                    boolean isIna = cll.isInstance(clObject);
//                    if (isIna) {
////                    addUrlMJava11 = findAddUrlForJava17(cll);
//                        try {
//                            Method addUrlMJava11L = cll.getDeclaredMethod("appendClassPath", String.class);
//                            addUrlMJava11L.setAccessible(true);
//                            addUrlMJava11 = addUrlMJava11L;
//                        } catch (NoSuchMethodException e) {
//                            log.warning(e.toString());
//                            Method[] declaredMethods1 = cll.getDeclaredMethods();
//                            String s = "declaredMethods " + declaredMethods1.length + " : " + Arrays.toString(declaredMethods1);
//                            log.warning(s);
//                            System.err.println(IdeaBuilderAddGroovyRuntime.class.getName() + " : " + s);
//                            addUrlToClForJava11(clObject, fileToAdd);
//                            java11Used = true;
//                        }
//                    } else {
//                        throw new RuntimeException("Unknown classloader type : " + clObject.getClass().getName());
//                    }
//
//                }
//                if (java11Used) {
//                    System.out.println("do nothing "+fileToAdd);
//                } else {
//                    addUrlMJava11.invoke(clObject, fileToAdd.getAbsolutePath());
//                }
            }
        //}
    }

    //private static volatile boolean java11Used = false;

    public static void addUrlToClForJava11(ClassLoader clObject, File fileToAdd) throws Exception {
        Class cll = clObject.loadClass("jdk.internal.loader.BuiltinClassLoader");
        Field ucp = cll.getDeclaredField("ucp");
        ucp.setAccessible(true);
        Object ucpObject = ucp.get(clObject);
        Method addURLM = ucpObject.getClass().getMethod("addURL", URL.class);
        addURLM.invoke(ucpObject, fileToAdd.toURL());
    }

    public static Method findAddUrlForJava17(Class cll) throws NoSuchMethodException {
        try {
            Method addUrlMJava11 = cll.getDeclaredMethod("appendClassPath", String.class);
            addUrlMJava11.setAccessible(true);
            return addUrlMJava11;
        } catch (NoSuchMethodException e) {
            log.warning(e.toString());
            Method[] declaredMethods1 = cll.getDeclaredMethods();
            String s = "declaredMethods " + declaredMethods1.length + " : " + Arrays.toString(declaredMethods1);
            log.warning(s);
            System.err.println(IdeaBuilderAddGroovyRuntime.class.getName() + " : " + s);
            throw e;
        }
    }

}
