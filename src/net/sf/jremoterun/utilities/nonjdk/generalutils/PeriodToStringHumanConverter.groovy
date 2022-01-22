package net.sf.jremoterun.utilities.nonjdk.generalutils

import groovy.transform.CompileStatic
import org.joda.time.Period
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

import java.text.SimpleDateFormat;

@CompileStatic
public class PeriodToStringHumanConverter {

    public long minutes7 = 7*60 * 1000;
    public long hour4 = 4 * 3600 * 1000;
    public long day1 = 23 * 3600 * 1000;
    public SimpleDateFormat sdfDay1 = new SimpleDateFormat('dd HH:mm')
    public SimpleDateFormat sdfOld = new SimpleDateFormat('yyyy-MM-dd')
    public PeriodFormatter hoursAndMin = 	new PeriodFormatterBuilder().appendHours().appendSuffix(" hours ").appendMinutes().appendSuffix(" min").toFormatter()
    public PeriodFormatter minAndSec = 	new PeriodFormatterBuilder().appendMinutes().appendSuffix(" min ").appendSeconds().appendSuffix(" sec").toFormatter()

    String logDuration(long diff) {
        Period period = new Period(diff)
        if(diff<minutes7){
            return minAndSec.print(period)
        }
        return hoursAndMin.print(period)
    }


    String diff(Date date1, Date now) {
        long diff = Math.abs(now.getTime() - date1.getTime())
        if (diff < day1) {
            return logDuration(diff);
        }
//        if (diff < day1) {
//            return sdfDay1.format(date1);
//        }
        return sdfOld.format(date1);
    }

}
