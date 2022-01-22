package net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable.ShowAlertJmx

import java.util.logging.Logger;

@CompileStatic
class EnvOpenSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static OpenUrlHandler defaultOpenUrlHandler;
    public static OpenFileHandler defaultOpenFileHandler;
    public static ShowJavaObjectHandler defaultShowObject;
    public static ShowAlertJmx defaultShowAlert;


}
