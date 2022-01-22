package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.PidDetector
import org.gradle.launcher.daemon.registry.DaemonDir;

import java.util.logging.Logger;


@CompileStatic
class DaemonLogPrinter extends InjectedCode {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object get(Object key) {
        printStat(key as OutputStream)
    }

/**
 * @see org.gradle.launcher.daemon.server.DaemonServices#getDaemonLogFile
 */
    static void printStat(OutputStream out1) {
        DaemonDir daemonDir = GradleEnvsUnsafe.gradle.getServices().get(DaemonDir)
        if (daemonDir == null) {
            GradleLogAppenderSetter.writeLine(out1, 'daemon object is null')
        } else {
            File versionedDir = daemonDir.getVersionedDir()
            if (versionedDir == null) {
                GradleLogAppenderSetter.writeLine(out1, 'daemon versionedDir is null')
            } else {
                int pid = PidDetector.detectPid()
                String fileName = "daemon-" + pid + ".out.log";
                File daemonLogFile = new File(versionedDir, fileName);
                if (daemonLogFile.exists()) {
                    GradleLogAppenderSetter.writeLine(out1, 'daemonLogFile = ' + daemonLogFile)
                } else {
                    GradleLogAppenderSetter.writeLine(out1, 'daemonLogFile not exist = ' + daemonLogFile)
                }
            }
        }

    }
}
