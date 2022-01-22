package net.sf.jremoterun.utilities.nonjdk.gradle.utils


import groovy.transform.CompileStatic
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import net.sf.jremoterun.utilities.nonjdk.gradle.runner.JrrGradleEnv
import net.sf.jremoterun.utilities.nonjdk.gradle.utils.GradleEnvsUnsafe
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum
import org.gradle.StartParameter
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.LoggingManager
import org.gradle.internal.logging.services.DefaultLoggingManager
import org.gradle.internal.logging.services.DefaultLoggingManagerFactory

import java.text.SimpleDateFormat
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
public class GradleLogAppenderSetter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean failedOnException = false;
    public OutputStream writer1;

    DefaultLoggingManager fetchLogManager() {
        return GradleEnvsUnsafe.gradle.getServices().get(LoggingManager) as DefaultLoggingManager
    }

    DefaultLoggingManager fetchLogManagerWork() {
        return JrrGradleEnv.gradleEnv.initScript.getLogging() as DefaultLoggingManager
    }

    DefaultLoggingManager fetchLogManagerOld() {
        // not working on 8.14.3, works on 7
        DefaultLoggingManagerFactory get1 = GradleEnvsUnsafe.gradle.getServices().get(DefaultLoggingManagerFactory)
        DefaultLoggingManager loggingManagerInternal = get1.getRoot() as DefaultLoggingManager
        return loggingManagerInternal
    }


    static BufferedOutputStream appendLogFileInHomeDir(int rotateCount) {
        return new GradleLogAppenderSetter().appendLogFileInHomeDir2(rotateCount);
    }

    BufferedOutputStream appendLogFileInHomeDir2(int rotateCount) {
        try {
            return appendLogFileInHomeDirImpl(rotateCount)
        } catch (Exception e) {
            if (failedOnException) {
                throw e;
            }
            log.log(Level.WARNING, "failed add looging", e)
            return null
        }
    }

    BufferedOutputStream appendLogFileInHomeDirImpl(int rotateCount) {
        String userHome1 = PropsEnum.user_home.getValue()
        File f = new File(userHome1)
        assert f.exists()
        File gradle1 = new File(f, '.gradle')
        assert gradle1.exists()
        File logsDir = new File(gradle1, 'logs')
        logsDir.mkdir()
        assert logsDir.exists()
        File file23 = new File(logsDir, 'out.txt')
        return appendLogFile(file23, rotateCount)
    }

    public static String alreadyResgistedGradle1 = 'alreadyResgistedOutput'

    BufferedOutputStream appendLogFile(File f, int rotateCount) {
        DefaultLoggingManager loggingManagerInternal = fetchLogManager()
        loggingManagerInternal.setLevelInternal(LogLevel.INFO)
        Object existedBefore = SharedObjectsUtils.getGlobalMap().get(alreadyResgistedGradle1)
        if (existedBefore == null) {
            SharedObjectsUtils.getGlobalMap().put(alreadyResgistedGradle1, true)
            FileRotate.rotateFile(f, rotateCount)
            BufferedOutputStream outputStream1 = f.newOutputStream()
            loggingManagerInternal.addStandardErrorListener(outputStream1)
            loggingManagerInternal.addStandardOutputListener(outputStream1)
            writer1 = outputStream1
            return outputStream1
        } else {
            log.info "registedred before"
            return null
        }
    }

    public static SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm')

    List<String> buildFullCmd() {
        List<String> cmdFull = []
        StartParameter parameter1 = GradleEnvsUnsafe.gradle.getStartParameter()
        cmdFull.addAll(parameter1.getTaskNames())
        parameter1.getProjectProperties().each {
            cmdFull.add "-P${it.key}=${it.value}".toString()
        }
        if (parameter1.isRerunTasks()) {
            new ClRef('org.gradle.initialization.StartParameterBuildOptions.RerunTasksOption')
            cmdFull.add '--rerun-tasks'
        }
//        if(!parameter1.buildCacheEnabled){
//            new ClRef('org.gradle.initialization.StartParameterBuildOptions.BuildCacheOption')
//            cmdFull.add '--no-build-cache'
//        }
        return cmdFull
    }

    void dumpProps2() {
        if (writer1 != null) {
            dumpProps(writer1)
        }
    }

    void dumpProps(OutputStream ou1) {
        //args passed as input arg :
        new ClRef('org.gradle.launcher.daemon.bootstrap.DaemonMain')
        StartParameter parameter1 = GradleEnvsUnsafe.gradle.getStartParameter()
        writeLine(ou1, "Date = ${sdf.format(new Date())}")
        writeLine(ou1, "user dir = ${PropsEnum.user_dir.getValue()}")
        writeLine(ou1, "tasks = ${parameter1.getTaskNames().join(' ')}")
        try {
            File gradleHom1 = JrrClassUtils.getFieldValue(parameter1, 'gradleHomeDir') as File;
            writeLine(ou1, "GradleHome = ${gradleHom1}")
        } catch (Throwable e) {
            log.log(Level.FINE, "failed get gradle home", e)
        }
//            writeLine(ou1, "GradleHome = ${parameter1.getGradleUserHomeDir()}")
        writeLine(ou1, "project props = ${parameter1.getProjectProperties()}")
        writeLine(ou1, "pid = ${PidDetector.detectPid()}")
        //writeLine(ou1, "${PropsEnum.sun_java_command.customName} = ${PropsEnum.sun_java_command.getValue()} ")
        //writeLine(ou1, "jvm args = ${java.lang.management.ManagementFactory.getRuntimeMXBean().getInputArguments().join(' ')} ")
        writeLine(ou1, "full cmd = ${buildFullCmd().join(' ')} ")
        try {
            RunnableWithParamsFactory.fromClass4(new ClRef('net.sf.jremoterun.utilities.nonjdk.gradle.utils.DaemonLogPrinter'), ou1)
        } catch (Throwable e) {
            log.severe("failed print daemon dir", e)
        }
        ou1.flush()
        GradleEnvsUnsafe.gradle.buildFinished {
            try {
                ou1.flush()
            } catch (Throwable e) {
                log.log(Level.WARNING, "failed flush", e)
            }
        }
    }

    static void writeLine(OutputStream out, String msg) {
        msg += ' \n'
        out.write(msg.getBytes())
    }

}
