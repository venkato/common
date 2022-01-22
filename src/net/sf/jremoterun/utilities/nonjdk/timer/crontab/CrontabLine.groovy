package net.sf.jremoterun.utilities.nonjdk.timer.crontab

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger;

@CompileStatic
class CrontabLine implements Comparable<CrontabLine>{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    Date nextRun
    List<String> cronExp


    CrontabLine(Date nextRun, List<String> cronExp) {
        this.nextRun = nextRun
        this.cronExp = cronExp
    }

    @Override
    int compareTo(CrontabLine o) {
        return nextRun.compareTo(o.nextRun)
    }
}
