package net.sf.jremoterun.utilities.nonjdk.apprunner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants;
import net.sf.jremoterun.utilities.nonjdk.swing.AskUserRunTask
import net.sf.jremoterun.utilities.nonjdk.timer.AdjustPeriodTimer
import net.sf.jremoterun.utilities.nonjdk.timer.TimerStyle

import javax.management.ObjectName
import java.util.logging.Logger;

@CompileStatic
abstract class RunProgramOnStart2 {


    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AdjustPeriodTimer adjustPeriodTimer

    ProgramRunnerAbstract programRunner = createProgramRunner();

    public static volatile long logProcessRunningInSec = DurationConstants.oneHour.timeInSecInt*6;
    /**
     * delay in ms
     */
    int runProcessedOnStartDelay = 60_000

    ProgramRunnerAbstract createProgramRunner(){
        return new ProgramRunner();
    }

    void runProgramsFromTimer() {
        log.info("start checking from timer");
        ProgramRunnerAbstract programRunner = createProgramRunner();
        for (ProgramInfo homeProgram : getMonitoredProcesses()) {
            programRunner.startProcessIfNeeded(homeProgram);
        }
        log.info("finish check from timer");
    }

    /**
     * @param period in milli seconds
     * @throws Exception
     */
    void runProgramOnStart3(long period) throws Exception {
        runProgramOnStart()
        adjustPeriodTimer = new AdjustPeriodTimer(period, TimerStyle.Consecutive, {
            runProgramsFromTimer();
        })
        adjustPeriodTimer.start2()
        MBeanFromJavaBean.registerMBean(adjustPeriodTimer, new ObjectName("jrrutils:timer=apprunner"))
    }


    void runProgramOnStart() throws Exception {
        if(net.sf.jremoterun.utilities.nonjdk.swing.AskUserRunTask.isGcOk()) {
            AskUserRunTask appRunner = new AskUserRunTask() {
                @Override
                void runProcesses() {
                    for (ProgramInfo programInfo : getMonitoredProcesses()) {
                        checkProgramOnStart(programInfo)
                    }
                }

                @Override
                String getTaskGroupName() {
                    return "Run process on start"
                }
            };
            appRunner.sleepTime = runProcessedOnStartDelay;
            appRunner.askToRunNewThread();
        }else{
            log.info "skip running ${getMonitoredProcesses()}  as no screen"
        }

    }

    void checkProgramOnStart(ProgramInfo programInfo) {
        if (programRunner.checkProcessRunning(programInfo)) {
            log.info("program running : " + programInfo);
        } else {
            programInfo.runProcess();
        }

    }

    static void defaultProcessRunnerInNewThread(ProgramInfo programInfo, File genericLogDir, int rotationDepth) {
        new RunProgramOnStart3(programInfo,genericLogDir,rotationDepth).runCmd()
    }



    abstract List<ProgramInfo> getMonitoredProcesses();

}
