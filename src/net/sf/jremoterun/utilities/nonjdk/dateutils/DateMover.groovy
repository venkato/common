package net.sf.jremoterun.utilities.nonjdk.dateutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.joda.time.LocalDate

import java.time.DayOfWeek;
import java.util.logging.Logger;

@Deprecated
@CompileStatic
class DateMover {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Date now;
    public int maxSearch = 20

    DateMover() {
        this(new Date())
    }

    DateMover(Date now) {
        this.now = now
    }

    Date moveDays(int daysToMove) {
        return DurationConstants.oneDay.moveDate(daysToMove,now)
    }

    Date searchForDate(boolean forward) {
        int i = 1;
        while (true) {
            assert i < maxSearch
            int k
            if (forward) {
                k = i
            } else {
                k = -i
            }
            Date day = moveDays(k)
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
        if (dayOfWeek < org.joda.time.DateTimeConstants.SATURDAY) {
            return true
        }
        return false
    }


}
