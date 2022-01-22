package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide;

import groovy.transform.CompileStatic
import net.sf.jremoterun.SimpleFindParentClassLoader;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.apprunner.JavaProcessInfoE
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.LoadClassesByClassloader
import net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog.EnvOpenSettings

import java.util.logging.Logger;

@CompileStatic
class IdePreloadAndDumper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public DumpLoadedClassesIde dumpLoadedClassesIde;
    public PreLoadClassesIde preLoadClassesIde;
    public JavaProcessInfoE javaProcessInfoE
    public IdePluginsClassloaderInfo idePluginsClassloaderInfo
    public int periodInSec;
    public IdeFindParentClassLoader ideFindParentClassLoader

    public Date startTime = new Date();
    public Thread threadLoader = Thread.currentThread()
    public Thread threadMonitor
    public long sleepMonitorTime = 180_000
    public volatile boolean finishedInit = false

//    IdePreloadAndDumper() {
//    }

    public IdePreloadAndDumper(IdePluginsClassloaderInfo idePluginsClassloaderInfo, JavaProcessInfoE e, int periodInSec) {
        this.idePluginsClassloaderInfo = idePluginsClassloaderInfo
        this.periodInSec = periodInSec
        this.javaProcessInfoE = e
        ideFindParentClassLoader = new IdeFindParentClassLoader(idePluginsClassloaderInfo)
        assert javaProcessInfoE != null
        assert idePluginsClassloaderInfo != null
    }


    void init() {
        assert dumpLoadedClassesIde==null
        dumpLoadedClassesIde = new DumpLoadedClassesIde(javaProcessInfoE.getClassesDumpFile(), periodInSec, idePluginsClassloaderInfo)
        preLoadClassesIde = new PreLoadClassesIde(idePluginsClassloaderInfo){
            @Override
            void loadClass2(ClassLoader classLoader2, String className1) {
                loadClass3(classLoader2, className1)
            }
        }
        startMonitroThread()

    }



    void loadClass3(ClassLoader classLoader2, String className1){
        classLoader2.loadClass(className1)
    }

    void startMonitroThread() {
        failedInit(false)
        Runnable r = {
            monitorEl()
        }
        threadMonitor = new Thread(r, "${IdePreloadAndDumper.getSimpleName()} monitor")
    }

    void monitorEl() {
        failedInit(false)
        Thread.sleep(sleepMonitorTime)
        if (finishedInit) {
            log.info "monitor: finished fine"
        }else{
            failedInit(true)
        }
    }

    String buildMsg(){
        String msg
        LoadClassesByClassloader loaderCurrent = preLoadClassesIde.currentLoader
        if (loaderCurrent == null) {
            msg = 'no loaderCurrent'
        } else {
            String classCurrent = loaderCurrent.loadingClassCurrent
            if (classCurrent == null) {
                msg = 'no classCurrent'
            } else {
                msg = "${classCurrent}"
            }
        }
        return msg
    }

    void failedInit(boolean realAlert) {
        String msg43 = "long loading : ${buildMsg()}"
        StackTraceElement[] trace1 = threadLoader.getStackTrace()
        Exception e2 = new Exception(msg43)
        e2.setStackTrace(trace1)
        if(realAlert) {
            log.warn(msg43, e2)
        }

        if (EnvOpenSettings.defaultShowAlert != null) {
            try {
                if(realAlert) {
                    EnvOpenSettings.defaultShowAlert.addAlert6(msg43, e2);
                }
            } catch (Throwable e333) {
                log.warn('', e333)
            }
        }
        if(realAlert) {
            JrrUtilitiesShowE.showException(msg43, e2)
        }
    }


    void start() {
        init()
        try {
            preLoadClassesIde.loadClassesByLocationAndPrevious(javaProcessInfoE)
        } catch (Exception e) {
            log.warn("failed load previous classes", e)
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed load previous classes", e)
        }
        dumpLoadedClassesIde.startClassDumping(javaProcessInfoE.getClassesDumpRotateCount())
        if (ideFindParentClassLoader != null) {
            ideFindParentClassLoader.selfSet()
            SimpleFindParentClassLoader.setDefaultClassLoader(getClass().getClassLoader());
        }
        finishedInit = true
    }
}
