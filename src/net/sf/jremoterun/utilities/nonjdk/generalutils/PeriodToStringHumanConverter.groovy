package net.sf.jremoterun.utilities.nonjdk.generalutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import org.joda.time.Period
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

import java.text.SimpleDateFormat
import java.util.logging.Logger;

@CompileStatic
public class PeriodToStringHumanConverter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public long secondsLess = 2 * DurationConstants.oneSec.timeInMsLong;
    public long seconds2Less =15 * DurationConstants.oneSec.timeInMsLong;
    public long minutesAndSecLess = 15 * DurationConstants.oneMin.timeInMsLong;
    public long hour4 = 4 * DurationConstants.oneHour.timeInMsLong;
    public long days23 = 23 * DurationConstants.oneDay.timeInMsLong;
    public long days1 = 1 * DurationConstants.oneDay.timeInMsLong;
    public long hoursAndMinLess = 2 * DurationConstants.oneDay.timeInMsLong;
    public long days3 = 3 * DurationConstants.oneDay.timeInMsLong;
    public long days70 = 70 * DurationConstants.oneDay.timeInMsLong;
    public long days400 = 399 * DurationConstants.oneYear.timeInMsLong;
    public SimpleDateFormat sdfDay1 = new SimpleDateFormat('dd HH:mm')
    public SimpleDateFormat sdfOld = new SimpleDateFormat('yyyy-MM-dd')
    public PeriodFormatter secAndMs = new PeriodFormatterBuilder().appendSecondsWithOptionalMillis().appendSuffix(" sec").toFormatter()
    public PeriodFormatter minAndSec = new PeriodFormatterBuilder().appendMinutes().appendSuffix(" min ").appendSeconds().appendSuffix(" sec").toFormatter()
    public PeriodFormatter hoursAndMin = new PeriodFormatterBuilder().appendHours().appendSuffix(" hours ").appendMinutes().appendSuffix(" min").toFormatter()
    public PeriodFormatter hours = new PeriodFormatterBuilder().appendHours().appendSuffix(" hours").toFormatter()


    public PeriodFormatter daysAndHours = new PeriodFormatterBuilder().appendDays().appendSuffix(" days ").appendHours().appendSuffix(" hours").toFormatter()
    public PeriodFormatter monthAndDays = new PeriodFormatterBuilder().appendMonths().appendSuffix(" months ").appendDays().appendSuffix(" days ").toFormatter()
    public PeriodFormatter yearsAndMonth = new PeriodFormatterBuilder().appendYears().appendSuffix(" years ").appendMonths().appendSuffix(" months ").toFormatter()

    public static String sameS = 'same'

    PeriodFormatter yearsAndMonths = new PeriodFormatterBuilder()
            .printZeroAlways()
            .appendYears()
            .appendSuffix(" year", " years")
            .appendSeparator(" and ")
            .printZeroRarelyLast()
            .appendMonths()
            .appendSuffix(" month", " months")
            .toFormatter();

    String logDuration(long diffInMs) {
        if(diffInMs==0){
            return sameS
        }
        if(diffInMs<secondsLess){
            return "${diffInMs} ms"
        }
        Period period = new Period(diffInMs)
        PeriodFormatter formatterToUse = getFormatterToUse(diffInMs)

        String r=  formatterToUse.print(period)
        //log.info "formatterToUse = ${formatterToUse.getPrinter()} for ${r}"
        //log.info "${diffInMs}  r=${r}"
        return r
    }

    PeriodFormatter getFormatterToUse(long diffInMs){
        assert diffInMs>0
        if (diffInMs < seconds2Less) {
            return secAndMs
        }
        if (diffInMs < minutesAndSecLess) {
            return minAndSec
        }
        if (diffInMs < hoursAndMinLess) {
            return hoursAndMin
        }
        if(diffInMs < days3){
            return hours
        }
        // does it works for days ??
//        if (diffInMs < days70) {
//            return daysAndHours
//        }
//        if (diffInMs < days400) {
//            return monthAndDays
//        }
        return hours
    }


    String diffFromNow(Date date1) {
        return diff(date1,new Date())
    }

    String diff(Date date1, Date now) {
        long diff = Math.abs(now.getTime() - date1.getTime())
        if (diff < hoursAndMinLess) {
            return logDuration(diff);
        }
        return sdfOld.format(date1);
    }

}
