package net.sf.jremoterun.utilities.nonjdk.dateutils

import groovy.transform.CompileStatic

import java.util.concurrent.TimeUnit

@CompileStatic
enum DurationConstants {


    oneSec  ( 1 , TimeUnit.SECONDS),
    oneMin  ( 60 , TimeUnit.MINUTES),
    oneHour  ( 3600, TimeUnit.HOURS),
    oneDay  ( 24 * 3600, TimeUnit.DAYS),
    oneWeek  ( 24*3600 * 7, null),
    oneMonth  ( 24*3600 * 30, null),
    oneYear  ( 24*3600 * 365, null),

    ;

    public int timeInSecInt
    public int timeInMsInt
    public long timeInMsLong;
    public TimeUnit timeUnit;

    DurationConstants(int inSec, TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        timeInSecInt = inSec
//        if(inSec>2_147_483_647){
        if(inSec>2_147_48){
            //throw new RuntimeException("value too big ${name()} ${inSec}")
            timeInMsInt = -1
        }else {
            timeInMsInt = inSec * 1000
        }
        long sec1 =1000
        long l = inSec
        timeInMsLong = l*sec1
    }

    long getTimeInSecLong(){
        return timeInSecInt
    }

    int getTimeInMsInt1(){
        if(timeInMsInt==-1){
            throw new IllegalStateException("${name()} duration too long")
        }
        return timeInMsInt
    }
}
