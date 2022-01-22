package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class DateWithFormatDumper implements ObjectDumperI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static SimpleDateFormat fullDateFormat = new SimpleDateFormat('yyyy-MM-dd HH:mm:ss Z')


    public SimpleDateFormat sdf =fullDateFormat;


    @Override
    Object dumpObject(Object obj, ObjectDumperProvider dumperProvider) {
        return sdf.format((Date)obj)
    }
}
