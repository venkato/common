package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers.JavaObjectFieldsDumper;

import java.util.logging.Logger;

@Deprecated
@CompileStatic
class JavaObjectPropsDumper extends JavaObjectFieldsDumper{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


}
