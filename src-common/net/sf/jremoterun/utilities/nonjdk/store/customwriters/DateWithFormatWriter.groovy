package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger;

@CompileStatic
class DateWithFormatWriter implements CustomWriter<DateWithFormat> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, DateWithFormat obj) {
        return objectWriter.writeConstructorWithArgs( writer3, getDataClass(),[obj.sdf.format(obj.date), obj.sdf])
    }

    @Override
    Class<DateWithFormat> getDataClass() {
        return DateWithFormat
    }
}
