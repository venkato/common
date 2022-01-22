package net.sf.jremoterun.utilities.nonjdk.dateutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.joda.time.DateTimeConstants
import org.joda.time.LocalDate

import java.util.logging.Logger

@CompileStatic
class DateMoverSpecificDayOfWeek extends DateMoverWorkWeek {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int dayOfWeek;

    DateMoverSpecificDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek
        assert dayOfWeek>=org.joda.time.DateTimeConstants.MONDAY
        assert dayOfWeek<= org.joda.time.DateTimeConstants.SUNDAY
    }

    @Override
    boolean isGoodDay(Date date, LocalDate localDate) {
        int dayOfWeek1 = localDate.getDayOfWeek()
        return dayOfWeek1 == dayOfWeek
    }


}
