package net.sf.jremoterun.utilities.nonjdk.store.csvstore

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class CsvDateWriter implements ObjectWriterCsvI<Date>{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SimpleDateFormat sdf = new SimpleDateFormat('yyyyMMdd-HHmm')

    CsvDateWriter() {

    }
    CsvDateWriter(SimpleDateFormat sdf) {
        this.sdf = sdf
    }

    @Override
    String writeObject(Date obj) {
        return sdf.format(obj)
    }
}
