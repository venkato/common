package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface ObjectDumperI<T> {

    Object dumpObject(T obj,ObjectDumperProvider dumperProvider);


}
