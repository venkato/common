package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
class JavaFieldDumper implements ObjectDumperI<Field>{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object dumpObject(Field obj, ObjectDumperProvider dumperProvider) {
        return "${dumpClass(obj.getDeclaringClass())}.${obj.getName()}"
    }

    String dumpClass(Class clazz){
        return clazz.getName()
    }
}
