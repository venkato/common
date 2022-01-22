package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.WinProcessesFinder
import net.sf.jremoterun.utilities.nonjdk.linux.LinuxProcess
import org.jvnet.winp.WinProcess

import java.util.logging.Logger

@CompileStatic
class ProgramRunnerLinux extends ProgramRunnerAbstract {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List<LinuxProcess> findListOfProcesses() {
        List<LinuxProcess> listOfProcesses = LinuxProcess.getAllAccessibleProcesses()
        return listOfProcesses
    }

    void onExceptionDuringMatch(LinuxProcess process1, Throwable e) {
        log.warn("${process1.pid} ", e)
    }

    boolean isMatched(LinuxProcess linuxProcess, ProgramInfo programInfo) {
        try {
            return programInfo.matches(linuxProcess.getFullCmd().join(' '))
        } catch (Throwable e) {
            onExceptionDuringMatch(linuxProcess, e)
            return false
        }
    }

    boolean checkProcessRunning(ProgramInfo programInfo) {
        try {
            List<LinuxProcess> listOfProcesses = findListOfProcesses()
            List<LinuxProcess> processes = listOfProcesses.findAll { isMatched(it, programInfo) }
            if (programInfo.allowManyProcessesMatched() && processes.size() > 1) {
                return true;
            }
            if (processes.size() > 1) {
                List<String> procInfo2 = processes.collect { "${it.pid} ${it.getFullCmd()}".toString() }
                log.info "too many processes for ${programInfo}  ${processes.size()} ${procInfo2}"
                throw new Exception("too many processes for ${programInfo}  ${processes.size()} ${procInfo2}")
            }
            return processes.size() == 1
        } catch (Throwable e) {
            onException(programInfo, e)
        }

    }


}
