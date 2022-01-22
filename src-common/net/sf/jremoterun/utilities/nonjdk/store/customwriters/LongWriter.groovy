package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger

@CompileStatic
class LongWriter implements CustomWriter<Long> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public NumSeparatorWriter numSeparatorWriter
    public boolean appendAs = true

    LongWriter(NumSeparatorWriter numSeparatorWriter) {
        this.numSeparatorWriter = numSeparatorWriter
    }

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, Long obj) {
        String asStr
        if (numSeparatorWriter == null) {
            asStr = obj.toString()
        } else {
            asStr = numSeparatorWriter.convert(obj)
        }
        if (appendAs) {
            return asStr + ' as long'
        }
        return asStr

    }

    @Override
    Class<Long> getDataClass() {
        return Long
    }
}
