package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.apprunner.JavaProcessInfoE
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils;

import java.util.logging.Logger;

@CompileStatic
class DumpLoadedClasses {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File file;
    public long periodInSec;
    public ClassLoader classLoader1;

    public boolean sort = true;
    public volatile boolean needRun = true;
    public final Object lock = new Object();
    public Date lastDumped;
    public Thread thread;
    public long freeSpaceNeed = 1_000_000; //1mb

    DumpLoadedClasses(File dumpToFile, long periodInSec, ClassLoader classLoader1) {
        this.file = dumpToFile
        this.periodInSec = periodInSec
        this.classLoader1 = classLoader1
        if (classLoader1 == null) {
            throw new NullPointerException('Classloader is null')
        }
    }

    static DumpLoadedClasses startDumpingClassloader(File dumpToFile, long periodInSec, int rotateCount, ClassLoader classLoader1) {
        DumpLoadedClasses dumpLoadedClasses = new DumpLoadedClasses(dumpToFile, periodInSec, classLoader1);
        FileRotate.rotateFile(dumpToFile, rotateCount);
        dumpLoadedClasses.start();
        return dumpLoadedClasses

    }

    static DumpLoadedClasses startDumpingCurrentClassloader(JavaProcessInfoE dumpToFile, long periodInSec) {
        return startDumpingCurrentClassloader(dumpToFile.getClassesDumpFile(), periodInSec, dumpToFile.getClassesDumpRotateCount())
    }

    static DumpLoadedClasses startDumpingCurrentClassloader(File dumpToFile, long periodInSec, int rotateCount) {
        return startDumpingClassloader(dumpToFile, periodInSec, rotateCount, JrrClassUtils.getCurrentClassLoader())
    }

    static List<String> dumpLoadedClassesNames(ClassLoader cl) {
        return new ArrayList<Class>(dumpLoadedClasses(cl)).collect { it.getName() }
    }

    public static String classesFileName = 'classes.txt';
    public static String filesFileName = 'files.txt';

    static void dumpLoadedClassesAndJarPathsToDir(File dir) {
        dir.mkdir()
        assert dir.exists()
        assert dir.isDirectory()
        dumpLoadedClassesAndJarPaths(dir.child(classesFileName), dir.child(filesFileName))
    }

    /**
     * @see net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysisEasy#easyCalcUsed2(java.io.File, java.io.File)
     */
    static void dumpLoadedClassesAndJarPaths(File loadedClassesFile, File urlFile) {
        URLClassLoader classLoader1 = JrrClassUtils.getCurrentClassLoaderUrl()
        urlFile.text = UrlCLassLoaderUtils.getFilesFromUrlClassloader2(classLoader1).collect { it.getAbsolutePath().replace('\\', '/') }.join('\n')
        loadedClassesFile.text = DumpLoadedClasses.dumpLoadedClassesNames(classLoader1).sort().join('\n')
    }

//    static List<String> printPackages(Collection<String> classes) {
//        classes.collect{}
//    }

    static List<Class> dumpLoadedClasses(ClassLoader cl) {
        return JrrClassUtils.getFieldValue(cl, 'classes') as List;
    }

    void dumpToFile() {
        boolean continue1 = true
        if (freeSpaceNeed > 0) {
            long spaceNow = file.getParentFile().freeSpace
            if (spaceNow < freeSpaceNeed) {
                log.info("skip dump : space left on device not enough : ${spaceNow / 1000_000} mb ${file.getParentFile().getPath()}")
                continue1 = false
            }
        }
        if (continue1) {
            List<String> classes = dumpLoadedClassesNames(classLoader1)
            if (sort) {
                classes = classes.sort()
            }
            PrintWriter writer = file.newPrintWriter()
            try {
                classes.each {
                    writer.println(it)
                }
                writer.flush()
            } finally {
                JrrIoUtils.closeQuietly2(writer, log)
            }
            lastDumped = new Date();
        }
    }

    static void dumpClassesS(File f, int rotateCount,Collection<String> classes) {
        if(classes==null){
            log.info "null classes for ${f}"
        }else {
            if (rotateCount > -1) {
                FileRotate.rotateFile(f, rotateCount)
            }
            classes = classes.sort(false)
            PrintWriter writer = f.newPrintWriter()
            try {
                classes.each {
                    writer.println(it)
                }
                writer.flush()
            } finally {
                JrrIoUtils.closeQuietly2(writer, log)
            }
        }
    }

    void start() {
        Runnable r = {
            log.info "class dumper started ${file}"
            try {
                while (needRun) {
                    dumpToFile()
                    synchronized (lock) {
                        lock.wait(periodInSec * 1000)
                    }
                }
            } finally {
                log.info "stopped"
            }
        }
        thread = new Thread(r, 'Loaded classes dumper');
        thread.start()
    }


}
