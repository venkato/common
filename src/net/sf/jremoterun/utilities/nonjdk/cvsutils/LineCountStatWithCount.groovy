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
    public long divider =1;
    public StatusDelayListener statusDelayListener;
    public long startDate =System.currentTimeMillis()
//    public long stateAtDate1 =System.currentTimeMillis()
//    public long stateValue1 =0
//    public long stateValue2 =0
//    public long stateAtDate2 =System.currentTimeMillis()


    public void newCountWritten(long newCount){
        countWritten = newCount
        if(statusDelayListener!=null) {
            statusDelayListener.delayedStatusChanged()
        }
        if(isNeedPrintProgress(countWritten)){
            writeStatus()
        }
    }


    void writeStatus(){
        log.info getDelayedStatus();
    }


    public String delayStatusPrefix='';
    public String delayStatusSuffixBigger='';
    public String delayStatusSuffixSmaller='';

    @Override
    String getDelayedStatus() {
        getDelayedStatus1()
    }

    String getDelayedStatus1() {
        if(countWritten<divider){
            return "${delayStatusPrefix}${countWritten}${delayStatusSuffixSmaller}"
        }
        return "${delayStatusPrefix}${countWritten.intdiv(divider)}${delayStatusSuffixBigger}"
    }
}
