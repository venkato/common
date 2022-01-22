package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide

import groovy.json.JsonOutput
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.apprunner.JavaProcessInfoE
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.DumpLoadedClasses
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.LoadClassesByClassloader
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import net.sf.jremoterun.utilities.nonjdk.file.FileLengthCheck
import net.sf.jremoterun.utilities.nonjdk.sshsup.BytesDivider

import java.util.logging.Logger;

@CompileStatic
class DumpLoadedClassesIde {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public boolean failedOnException = false;
    public StoreStat storeStatLatest
    public int minClassesToStore = 5;
    public List<String> skipDumpingPlugins = [];

    public File file;
    public long periodInSec;

    public boolean prettyJson = true


    public boolean sort = true;
    public volatile boolean needRun = true;
    public final Object lock = new Object();
    //public Date lastDumped;
    public long startUpDelayInSec = 1
    public Thread workingThread;
    public long freeSpaceNeed = BytesDivider.mb.multBy(10); //10mb
    public IdePluginsClassloaderInfo idePluginsClassloaderInfo

    public static List<String> containsIgnoreDumperS = ['$']

    DumpLoadedClassesIde(File file, long periodInSec, IdePluginsClassloaderInfo idePluginsClassloaderInfo) {
        this.file = file
        this.periodInSec = periodInSec
        this.idePluginsClassloaderInfo = idePluginsClassloaderInfo
    }


    void startClassDumping(int rotateCount) {
        FileRotate.rotateFile(file, rotateCount);
        Runnable r = { dumperLoop() }
        workingThread = new Thread(r, 'Loaded classes dumper');
        workingThread.setDaemon(true)
        workingThread.start()
    }

    void dumperLoop() {
        log.info "class dumper started ${file}"

        try {
            synchronized (lock) {
                log.info "initial sleep for ${startUpDelayInSec} sec"
                long deration2=startUpDelayInSec * DurationConstants.oneSec.timeInMsLong
                lock.wait(deration2)
            }
            while (needRun) {
                dumpToFile()
                log.info "sleep for ${periodInSec} sec"
                synchronized (lock) {
                    long deration2=periodInSec * DurationConstants.oneSec.timeInMsLong
                    lock.wait(deration2)
                }
            }
        } finally {
            log.info "stopped"
        }
    }

    boolean isStorePlugin(String pluginId, Collection<String> classes) {
        return classes.findAll { isNeedCountForStat(it) }.size() > minClassesToStore
    }



    boolean isNeedCountForStat(String className1) {
        String ignoreFounded33 = containsIgnoreDumperS.find { className1.contains(it) }
        if (ignoreFounded33 != null) {
            return false
        }
        String ignoreFounded = LoadClassesByClassloader.containsIgnore.find { className1.contains(it) }
        if (ignoreFounded != null) {
            return false
        }
        String startWithFOund = LoadClassesByClassloader.startWithIgnore.find { className1.startsWith(it) }
        if (startWithFOund != null) {
            return false
        }
        return true
    }

    public StoreStat dumpLoadedClassesNames() throws Exception {
        StoreStat storeStat1 = new StoreStat()
        storeStatLatest = storeStat1
        Collection<String> pluginSymbolicNames
        pluginSymbolicNames = idePluginsClassloaderInfo.getPlugins()

        Map<String, Collection<String>> result = [:];
        for (String pluginId : pluginSymbolicNames) {
            try {
                if (!skipDumpingPlugins.contains(pluginId)) {
                    List<String> classesInPlugin = dumpClassesInPlugin(pluginId);
                    if (classesInPlugin != null && classesInPlugin.size() > 0) {
                        if (isStorePlugin(pluginId, classesInPlugin)) {
                            if (sort) {
                                classesInPlugin = classesInPlugin.sort()
                            }
                            result.put(pluginId, classesInPlugin);
                        } else {
                            storeStat1.skippedPlugins.add(pluginId)
                        }
                    } else {
                        storeStat1.emptyPlugins.add(pluginId);
                    }
                }
            } catch (Throwable e) {
                onException(pluginId, e, storeStat1);
            }
        }
        storeStat1.data = result
        storeStat1.duration = System.currentTimeMillis() - storeStat1.startDate.getTime()
        return storeStat1;
    }

    public List<String> dumpClassesInPlugin(String pluginId) {
        ClassLoader findPluginModuleClassLoader
        findPluginModuleClassLoader = idePluginsClassloaderInfo.findClassLoader(pluginId)

        if (findPluginModuleClassLoader == null) {
            return null;
        }
        return DumpLoadedClasses.dumpLoadedClassesNames(findPluginModuleClassLoader);

    }

    public void onException(String pluginId, Throwable e, StoreStat storeStat) throws Exception {
        if (!storeStat.failedPlugins.containsKey(pluginId)) {
            storeStat.failedPlugins.put(pluginId, e);
        }
        if (failedOnException) {
            log.info("failed on " + pluginId);
            throw e;
        } else {
            log.warn("failed on " + pluginId, e);
        }
    }

    void dumpToFile() {
        boolean continue1 = true
        if (freeSpaceNeed > 0) {
            long spaceNow = file.getParentFile().getFreeSpace()
            if (spaceNow < freeSpaceNeed) {
                log.info("skip dump : space left on device not enough : ${spaceNow / 1000_000} mb ${file.getParentFile().getPath()}")
                continue1 = false
            }
        }
        if (continue1) {
            log.info "dumping loaded class .."
            StoreStat loadedStat = dumpLoadedClassesNames()
            Map<String, Collection<String>> loadedClassesNames = loadedStat.data
            if (sort) {
                loadedClassesNames = loadedClassesNames.sort()
            }
            String json = JsonOutput.toJson(loadedClassesNames);
            if (prettyJson) {
                json = JsonOutput.prettyPrint(json);
            }
            file.text = json
            loadedStat.failedPlugins.each {
                JrrUtilitiesShowE.showException("failed ${it.key}", it.value)
            }
            log.info "dumped loaded class within ${loadedStat.duration / 1000} sec"
        }
    }

}
