package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger

@CompileStatic
class IntegerWriter implements CustomWriter<Integer> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public NumSeparatorWriter numSeparatorWriter

    IntegerWriter(NumSeparatorWriter numSeparatorWriter) {
        this.numSeparatorWriter = numSeparatorWriter
    }

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, Integer obj) {
        if (numSeparatorWriter==null) {
            return obj.toString()
        }
        return numSeparatorWriter.convert(obj)
    }

    @Override
    Class<Integer> getDataClass() {
        return Integer
    }
}
