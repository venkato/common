package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.ConsoleRedirect
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import net.sf.jremoterun.utilities.nonjdk.apprunner.JavaProcessInfoE
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants

import java.text.SimpleDateFormat
import java.util.logging.Logger;

@CompileStatic
class JavaProgramWrapper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public LoadClassesByClassloader loadClassesByClassloader = new LoadClassesByClassloader()
    public JavaProcessInfoE javaProcessInfoE;

    public long dumpClassesIntervalInSec = DurationConstants.oneHour.timeInSecLong;
    public DumpLoadedClasses dumpingCurrentClassloader
    public int logRotateCount = 30

    public SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MMM-dd HH:mm')

    JavaProgramWrapper(JavaProcessInfoE javaProcessInfoE) {
        this.javaProcessInfoE = javaProcessInfoE
    }

    void preStart() {
        if (javaProcessInfoE.getJmxPort() != null) {
            JrrUtils.creatJMXConnectorAndRMIRegistry(null, javaProcessInfoE.getJmxPort().getPort(), null, null);
        }
        File logPath = javaProcessInfoE.getLogPath()
        if (logPath != null) {
            logPath.getParentFile().mkdir()
            assert logPath.getParentFile().exists()
            ConsoleRedirect.setOutputWithRotationAndFormatter(logPath, logRotateCount)
        }
        dumpPid()
    }


    void postStart() {
        if(javaProcessInfoE.getClassesDumpFile().getParentFile().exists()) {
            loadClasses()
        }
        dumpClassesRegularly()
        addLogTimer()
        net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook.addShutDownHook()
    }

    void addLogTimer(){
        net.sf.jremoterun.utilities.nonjdk.GeneralUtils.startLogTimer()
    }

    void createDumpClassesDir() {
        File parentFile1 = javaProcessInfoE.getClassesDumpFile().getParentFile()
        parentFile1.mkdir()
        assert parentFile1.exists()
    }

    void loadClasses() {

        loadClassesByClassloader.loadClassesByLocationAndPrevious(javaProcessInfoE)
        log.info "${loadClassesByClassloader.buildStat()}"
    }


    void dumpPid() {
        int pid = PidDetector.detectPid()
        String msg = "${sdf.format(new Date())} pid = ${pid} "
        log.info msg
        println msg
    }


    void dumpClassesRegularly() {
        createDumpClassesDir()
        dumpingCurrentClassloader = DumpLoadedClasses.startDumpingCurrentClassloader(javaProcessInfoE, dumpClassesIntervalInSec)
    }


}
