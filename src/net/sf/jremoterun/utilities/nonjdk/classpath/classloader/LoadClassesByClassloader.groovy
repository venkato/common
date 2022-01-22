package net.sf.jremoterun.utilities.nonjdk.classpath.classloader


import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.apprunner.JavaProcessInfoE

import java.util.logging.Logger

/**
 * @see net.sf.jremoterun.utilities.nonjdk.classpath.classloader.LoadSpecificClassesFromJar
 */
@CompileStatic
class LoadClassesByClassloader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassLoader classLoader1

//    @Deprecated
//    public int countOk = 0

//    @Deprecated
//    public int countFailed = 0
    public HashSet<String> loadedOkDetails = new HashSet<>()
    public HashSet<String> failedLoadDetails = new HashSet<>()
    public HashSet<String> noClassFoundErrorLoadDetails = new HashSet<>()
    public HashSet<String> skippedLoadDetails = new HashSet<>()
    public HashSet<String> alreadyLoadedClasses
    public static boolean checkLoadedBefore = true

    public static List<String> containsIgnore = ['.$Proxy', '_$$_', 'Expression$','$$EnhancerBySpringCGLIB$$']
    public static List<String> startWithIgnore = ['Expression$','jdk.proxy5.$']

    LoadClassesByClassloader(ClassLoader classLoader1) {
        this.classLoader1 = classLoader1
    }

    LoadClassesByClassloader() {
        classLoader1 = JrrClassUtils.getCurrentClassLoader()
    }

    void getAlreadyLoaded() {
        if (alreadyLoadedClasses == null) {
            alreadyLoadedClasses = new HashSet(DumpLoadedClasses.dumpLoadedClassesNames(classLoader1));
        }
    }

    void loadClassesByLocationAndPrevious(JavaProcessInfoE e) {
        loadClassesByLocationAndPrevious(e.getClassesDumpFile(), e.getClassesDumpRotateCount())
    }

    void loadClassesByLocationAndPrevious(File f, int count) {
        if(f.exists()) {
            loadClassesByLocation(f)
            log.info "stat after main : ${buildStat()}"
        }else{
            log.info "file not exist ${f} , skip loading"
        }
        while (count > 0) {
            File fBefore = FileRotate.buildRotateFile(f, f.getParentFile(), count, false);
            log.info "loading ${count} ${fBefore}"
            if (fBefore.exists()) {
                loadClassesByLocation(fBefore)
                log.info "${fBefore} loaded fine"
            } else {
                log.info "${fBefore} missed, ignore"
            }
            count--
        }
        log.info "stat after main and all previous ${count} : ${buildStat()}"
    }

    void loadClassesByLocation(File f) {
        assert f.exists()
        assert f.isFile()
        ContextClassLoaderWrapper.wrap2(classLoader1, {
            f.eachLine {
                loadOneClass(it)
            }
        });
    }

    void loadClassesByLocation(Collection<String> classes3) {
        classes3.each {
            loadOneClass(it)
        }
    }

    void onAlreadyLoaded(String className) {

    }

    void loadOneClass(String className1) {
        if (checkLoadedBefore) {
            getAlreadyLoaded()
        }
        try {
            boolean classLoadedbefore = false
            if (alreadyLoadedClasses != null && alreadyLoadedClasses.contains(className1)) {
                classLoadedbefore = true
            }
            if(loadedOkDetails.contains(className1)){
                classLoadedbefore = true
            }
            if (classLoadedbefore) {
                onAlreadyLoaded(className1)
            } else {
                if (isNeedLoad(className1)) {
                    classLoader1.loadClass(className1)
                    loadedOkDetails.add(className1)
//                    countOk++
                    if (alreadyLoadedClasses != null) {
                        alreadyLoadedClasses.add(className1)
                    }
                } else {
                    skippedLoadDetails.add(className1)
                }
            }
        } catch (NoClassDefFoundError e) {
            onLoadFailed(className1, e)
        } catch (ClassNotFoundException e) {
            onLoadFailed(className1, e)
        } catch (Throwable e) {
            log.info "failed load ${className1} ${e}"
            throw e
        }

    }

    boolean isNeedLoad(String className) {
        String ignoreFounded = containsIgnore.find { className.contains(it) }
        if (ignoreFounded != null) {
            return false
        }
        String startWithFOund = startWithIgnore.find { className.startsWith(it) }
        if (startWithFOund != null) {
            return false
        }
        return true
    }

    void onLoadFailed(String className, NoClassDefFoundError e) {
        log.info("failed load ${className}", e)
        noClassFoundErrorLoadDetails.add(className)
    }

    void onLoadFailed(String className, ClassNotFoundException e) {
        log.fine "failed load ${className} ${e}"
//        countFailed++
        failedLoadDetails.add(className)
    }

    void logStat() {
        log.info buildStat()
    }

    String buildStat() {
        return "countOk=${loadedOkDetails.size()} skipped=${skippedLoadDetails.size()} ClassFoundError=${noClassFoundErrorLoadDetails} countFailed2=${failedLoadDetails.size()}: ${failedLoadDetails.sort()}"
    }

    @Override
    String toString() {
        return buildStat()
    }
}
