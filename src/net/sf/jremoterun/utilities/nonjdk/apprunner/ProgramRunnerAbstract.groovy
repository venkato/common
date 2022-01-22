package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.WinProcessesFinder
import net.sf.jremoterun.utilities.nonjdk.swing.AskUserRunTask
import org.jvnet.winp.WinProcess

import java.util.logging.Logger

@CompileStatic
abstract class ProgramRunnerAbstract {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public void startProcessIfNeeded(ProgramInfo programInfo) {
        if (checkProcessRunning(programInfo)) {
            log.info "program running : ${programInfo.name()}"
        } else {
            if(net.sf.jremoterun.utilities.nonjdk.swing.AskUserRunTask.isGcOk()) {
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
            }else {
                log.info "skip running ${programInfo.name()} , as no screen"
            }
        }
    }

    abstract boolean checkProcessRunning(ProgramInfo programInfo)

    void onException(ProgramInfo programInfo,Throwable e){
        net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed check ${programInfo}", e);
    }

}
