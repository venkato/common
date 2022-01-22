package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.Writer3Import

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class DateWithFormatWriter implements CustomWriter<DateWithFormat> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    String save(Writer3Import writer3, ObjectWriter objectWriter, DateWithFormat obj) {
        return objectWriter.writeConstructorWithArgs(getDataClass(), writer3, [obj.sdf.format(obj.date), obj.sdf])
    }

    @Override
    Class<DateWithFormat> getDataClass() {
        return DateWithFormat
    }
}
