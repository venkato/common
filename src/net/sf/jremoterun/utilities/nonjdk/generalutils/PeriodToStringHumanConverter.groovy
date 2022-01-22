package net.sf.jremoterun.utilities.nonjdk.generalutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import org.joda.time.Period
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

import java.text.SimpleDateFormat;

@CompileStatic
public class PeriodToStringHumanConverter {

    public long minutes7 = 7 * DurationConstants.oneMin.timeInMsLong;
    public long hour4 = 4 * DurationConstants.oneHour.timeInMsLong;
    public long day1 = 23 * DurationConstants.oneDay.timeInMsLong;
    public SimpleDateFormat sdfDay1 = new SimpleDateFormat('dd HH:mm')
    public SimpleDateFormat sdfOld = new SimpleDateFormat('yyyy-MM-dd')
    public PeriodFormatter hoursAndMin = new PeriodFormatterBuilder().appendHours().appendSuffix(" hours ").appendMinutes().appendSuffix(" min").toFormatter()
    public PeriodFormatter minAndSec = new PeriodFormatterBuilder().appendMinutes().appendSuffix(" min ").appendSeconds().appendSuffix(" sec").toFormatter()

    String logDuration(long diffInMs) {
        Period period = new Period(diffInMs)
        if (diffInMs < minutes7) {
            return minAndSec.print(period)
        }
        return hoursAndMin.print(period)
    }


    String diff(Date date1, Date now) {
        long diff = Math.abs(now.getTime() - date1.getTime())
        if (diff < day1) {
            return logDuration(diff);
        }
        return sdfOld.format(date1);
    }

}
