package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableWithParamsFactory
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import net.sf.jremoterun.utilities.nonjdk.gradle.utils.GradleEnvsUnsafe
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum
import org.gradle.StartParameter
import org.gradle.api.logging.LogLevel
import org.gradle.internal.logging.services.DefaultLoggingManager
import org.gradle.internal.logging.services.DefaultLoggingManagerFactory

import java.text.SimpleDateFormat
import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
public class GradleLogAppenderSetter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static DefaultLoggingManager fetchLogManager() {
        DefaultLoggingManagerFactory get1 = GradleEnvsUnsafe.gradle.getServices().get(DefaultLoggingManagerFactory)
        DefaultLoggingManager loggingManagerInternal = get1.getRoot() as DefaultLoggingManager
        return loggingManagerInternal
    }


    static BufferedOutputStream appendLogFileInHomeDir(int rotateCount) {
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

    static BufferedOutputStream appendLogFile(File f, int rotateCount) {
        DefaultLoggingManager loggingManagerInternal = fetchLogManager()
        loggingManagerInternal.setLevelInternal(LogLevel.INFO)
        Object existedBefore = SharedObjectsUtils.getGlobalMap().get(alreadyResgistedGradle1)
        if (existedBefore==null) {
            SharedObjectsUtils.getGlobalMap().put(alreadyResgistedGradle1,true)
            FileRotate.rotateFile(f, rotateCount)
            BufferedOutputStream outputStream1 = f.newOutputStream()
            loggingManagerInternal.addStandardErrorListener(outputStream1)
            loggingManagerInternal.addStandardOutputListener(outputStream1)
            return outputStream1
        } else {
            log.info "registedred before"
            return null
        }
    }

    public static SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm')

    static void dumpProps(OutputStream ou1){
        if(ou1!=null) {
            StartParameter parameter1 = GradleEnvsUnsafe.gradle.getStartParameter()
            writeLine( ou1,"Date = ${sdf.format(new Date())}")
            writeLine(ou1, "user dir = ${PropsEnum.user_dir.getValue()}")
            writeLine(ou1, "tasks = ${parameter1.getTaskNames().join(' ')}")
            try {
                File gradleHom1 = JrrClassUtils.getFieldValue(parameter1, 'gradleHomeDir') as File;
                writeLine(ou1, "GradleHome = ${gradleHom1}")
            }catch(Throwable e){
                log.log(Level.FINE,"failed get gradle home",e)
            }
//            writeLine(ou1, "GradleHome = ${parameter1.getGradleUserHomeDir()}")
            writeLine(ou1, "project props = ${parameter1.getProjectProperties()}")
            writeLine(ou1, "pid = ${PidDetector.detectPid()}")
            try {
                RunnableWithParamsFactory.fromClass4(new ClRef('net.sf.jremoterun.utilities.nonjdk.gradle.utils.DaemonLogPrinter'), ou1)
            }catch(Throwable e){
                log.severe("failed print daemon dir",e)
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
//        ou1.write "user dir = ${parameter1.}".getBytes()
    }

    static void writeLine(OutputStream out,String msg){
        msg+=' \n'
        out.write(msg.getBytes())
    }

}
