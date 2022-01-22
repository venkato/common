package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider;

import java.util.logging.Logger;

@CompileStatic
class ToStringObjectDumper implements ObjectDumperI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object dumpObject(Object obj, ObjectDumperProvider dumperProvider) {
        return obj.toString()
    }
}
