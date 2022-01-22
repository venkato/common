package net.sf.jremoterun.utilities.nonjdk.dateutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

/**
 * @see net.sf.jremoterun.utilities.nonjdk.str2obj.DateOnlyBackConverter
 */
@CompileStatic
class DateMoverInt {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static SimpleDateFormat sdfNotSync=new SimpleDateFormat('yyyyMMdd')
    public static SimpleDateFormat sdfSync=new SimpleDateFormat('yyyyMMdd')

    static int moveDaysIntNoSync(int now, int day) {
        return sdfNotSync.format( DurationConstants.oneDay.moveDate(day, sdfNotSync.parse(''+now))) as int
    }

    static int moveDaysIntSync(int now, int day) {
        synchronized (sdfSync) {
            return sdfSync.format(DurationConstants.oneDay.moveDate(day, sdfNotSync.parse('' + now))) as int
        }
    }

    static Date parseSync(int date){
        synchronized (sdfSync) {
            return sdfSync.parse(''+date)
        }
    }

    static int formatSync(Date date){
        synchronized (sdfSync) {
            return sdfSync.format(''+date) as int
        }
    }



}
