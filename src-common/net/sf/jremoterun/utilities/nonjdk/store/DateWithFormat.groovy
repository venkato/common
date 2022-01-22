package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class DateWithFormat {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static SimpleDateFormat fullDateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss Z')

    public Date date;
    public SimpleDateFormat sdf;

    DateWithFormat(Date dateD, SimpleDateFormat sdf) {
        this.date = dateD
        this.sdf = sdf
    }

    DateWithFormat(String dateS, SimpleDateFormat sdf) {
        date = sdf.parse(dateS)
    }

    static DateWithFormat createCurrentDate(){
        return new DateWithFormat(new Date(), DateWithFormat.fullDateFormat)
    }

}
