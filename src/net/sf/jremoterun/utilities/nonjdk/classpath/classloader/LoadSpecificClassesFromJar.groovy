package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable
import net.sf.jremoterun.utilities.java11.sep1.Java11ModuleJvmtiAgent
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.apprunner.JavaProcessInfoE
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2;

import java.util.logging.Logger;

/**
 * @see net.sf.jremoterun.utilities.nonjdk.classpath.classloader.LoadClassesByClassloader
 */
@CompileStatic
class LoadSpecificClassesFromJar {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassLoader classLoader1 = JrrClassUtils.getCurrentClassLoader();
    HashSet<String> loadedBefore = new HashSet()
    List<String> loadedOk = []
    List<String> loadedFailed = []

    public static List<String> containsIgnore = ['.$Proxy', '_$$_',
                                                 Java11ModuleJvmtiAgent.getPackage().getName(),
                                                 Java11ModuleSetDisable.moduleCheckDisableImpl.className,
            'net.sf.jremoterun.swt.',
                                                 net.sf.jremoterun.callerclass.GetCallerClassS.java8ClassImpl,
                                                 net.sf.jremoterun.callerclass.GetCallerClassS.java9ClassImpl,
    ]

//    LoadSpecificClassesFromJar(ClassLoader classLoader1, File f ) {
//        this.classLoader1 = classLoader1
//        loadedBefore = f.readLines()
//    }
//
//    LoadSpecificClassesFromJar(ClassLoader classLoader1, List<String> loadedBefore) {
//        this.classLoader1 = classLoader1
//        this.loadedBefore = loadedBefore
//    }

    void loadFromFile(JavaProcessInfoE javaProcessInfoE) {
        loadFromFile(javaProcessInfoE.classesDumpFile, javaProcessInfoE.classesDumpRotateCount)
    }

    void loadFromFile(File f, int lookback) {
        if (f.exists()) {
            loadFromFileImpl(f)
        } else {
            log.info "file not found ${f}"
        }
        for (int i = 1; i <= lookback; i++) {
            File file1 = FileRotate.buildRotateFile(f, f.getParentFile(), i,false);
            if (file1.exists()) {
                loadFromFileImpl(file1)
            } else {
                log.info "file not found ${file1}"
            }
        }
    }

    void loadJrrClasses() {
        loadClassesFromJar(JrrStarterJarRefs.jrrutilitiesOneJar)
        loadClassesFromJar(JrrStarterJarRefs2.jremoterun)
        loadClassesFromJar(JrrStarterJarRefs2.jrrassist)
        loadClassesFromJar(JrrStarterJarRefs2.jrrbasetypes)
        loadClassesFromJar(JrrStarterJarRefs2.java11base)
        printStat()
    }

    void printStat() {
        String msg = "load stat : ok=${loadedOk.size()}, failed=${loadedFailed.size()}"
        if(!loadedFailed.isEmpty()){
            msg+=", failed details : ${loadedFailed.sort().join(', ')}"
        }
        log.info msg
    }

    void loadFromFileImpl(File f) {
        loadedBefore.addAll(f.readLines())
    }


    void loadClassesFromJar(ToFileRef2 f) {
        loadClassesFromJar(f.resolveToFile())
    }

    public boolean errorIfNoClassesFound = true

    void loadClassesFromJar2(File f) {
        List<String> classesThisLocation = GetClassesFromLocation.handleZipS(f)
        if(errorIfNoClassesFound) {
            if (classesThisLocation.size() == 0) {
                throw new IllegalStateException("No classes in ${f}")
            }
        }
        classesThisLocation = classesThisLocation.findAll {isNeedLoad(it)}
        classesThisLocation.each {
            load1(it)
        }
    }

    void loadClassesFromJar(File f) {
        if (loadedBefore.isEmpty()) {
            log.info "no before classes ${f}"
        } else {
            List<String> classesThisLocation = GetClassesFromLocation.handleZipS(f)
            if(errorIfNoClassesFound) {
                if (classesThisLocation.size() == 0) {
                    throw new IllegalStateException("No classes in ${f}")
                }
            }
            classesThisLocation.retainAll(loadedBefore)
            classesThisLocation = classesThisLocation.findAll {isNeedLoad(it)}
            classesThisLocation.each {
                load1(it)
            }
        }
    }


    void load1(String s) {
        try {
            classLoader1.loadClass(s)
            loadedOk.add(s)
        } catch (ClassNotFoundException e) {
            loadedFailed.add(s)
        } catch (Throwable e) {
            log.info "failed load ${s} ${e}"
            throw e
        }
    }



    boolean isNeedLoad(String className){
        if(className.startsWith('META-INF')){
            return false
        }
        String ignoreFounded = containsIgnore.find {className.contains(it)}
        if(ignoreFounded!=null){
            return false
        }
        return true
    }

}
