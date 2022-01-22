package net.sf.jremoterun.utilities.nonjdk.cvsutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.rstarunner.StatusDelayListener
import net.sf.jremoterun.utilities.nonjdk.rstarunner.StatusDelayedFetcher;

import java.util.logging.Logger;

@CompileStatic
class LineCountStatWithCount extends LineCountStat implements StatusDelayedFetcher {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public long countWritten;
    public long divider = 1;
    public StatusDelayListener statusDelayListener;
    public long startDate = System.currentTimeMillis()

    public String delayStatusPrefix = '';
    public String delayStatusSuffixBigger = '';
    public String delayStatusSuffixSmaller = '';

    LineCountStatWithCount() {
    }

    public void newCountWritten(long newCount) {
        countWritten = newCount
        if (statusDelayListener != null) {
            statusDelayListener.delayedStatusChanged()
        }
        if (isNeedPrintProgress(countWritten)) {
            writeStatus()
        }
    }


    void writeStatus() {
        log.info getDelayedStatus();
    }


    @Override
    String getDelayedStatus() {
        getDelayedStatus1()
    }

    String getDelayedStatus1() {
        if (countWritten < divider) {
            return "${delayStatusPrefix}${countWritten}${delayStatusSuffixSmaller}"
        }
        return "${delayStatusPrefix}${countWritten.intdiv(divider)}${delayStatusSuffixBigger}"
    }
}
