package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.swing.AskUserRunTask
import net.sf.jremoterun.utilities.nonjdk.WinProcessesFinder
import org.jvnet.winp.WinProcess

import java.util.logging.Logger

@CompileStatic
class ProgramRunner {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public void startProcessIfNeeded(ProgramInfo programInfo) {
        if (checkProcessRunning(programInfo)) {
            log.info "program running : ${programInfo.name()}"
        } else {
            AskUserRunTask appRunner = new AskUserRunTask() {
                @Override
                void runProcesses() {
                    if (checkProcessRunning(programInfo)) {
                        log.info "program seems already running : ${programInfo.name()} , manually started ?"
                    } else {
                        programInfo.runProcess();
                    }
                }

                @Override
                String getTaskGroupName() {
                    return programInfo.name()
                }
            }
            //appRunner.taskGroupName = programInfo.name();
            appRunner.askToRunNewThread();
        }
    }

    boolean checkProcessRunning(ProgramInfo programInfo) {
        try {
            List<WinProcess> listOfProcesses = WinProcessesFinder.findAllProcesses()
            List<WinProcess> processes = listOfProcesses.findAll { programInfo.matches(it.commandLine) }
//        processes.findAll{it.pare}
            if (programInfo.allowManyProcessesMatched() && processes.size() > 1) {
                return true;
            }
            if (processes.size() > 1) {
                List<String> procInfo2 =processes.collect { "${it.getPid()} ${it.getCommandLine()}".toString()   }
                log.info "too many processes for ${programInfo}  ${processes.size()} ${procInfo2}"
                throw new Exception("too many processes for ${programInfo}  ${processes.size()} ${procInfo2}")
            }
            return processes.size() == 1
        }catch(Throwable e){
            onException(programInfo, e)
        }

    }

    void onException(ProgramInfo programInfo,Throwable e){
        net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed check ${programInfo}", e);
    }

}
