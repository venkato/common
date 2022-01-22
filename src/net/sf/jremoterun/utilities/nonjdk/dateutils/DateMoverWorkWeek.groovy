package net.sf.jremoterun.utilities.nonjdk.dateutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

import java.util.logging.Logger

@CompileStatic
class DateMoverWorkWeek {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int maxSearch = 90

    DateMoverWorkWeek() {
    }


    Date moveDays(Date now, int day) {
        long day2 = day
        long newDate = now.getTime() + day2 * DurationConstants.oneDay.timeInMsInt
        return new Date(newDate)
    }

    Date searchForDate(Date now, boolean forward) {
        int i = 1;
        while (true) {
            assert i < maxSearch
            int k
            if (forward) {
                k = i
            } else {
                k = -i
            }
            Date day = moveDays(now, k)
            LocalDate localDate = new LocalDate(day.getTime())
            boolean goodDay = isGoodDay(day, localDate)
            if (goodDay) {
                return day
            }
            i++
        }
    }

    boolean isGoodDay(Date date, LocalDate localDate) {
        int dayOfWeek = localDate.getDayOfWeek()
        if (dayOfWeek < DateTimeConstants.SATURDAY) {
            return true
        }
        return false
    }


}
