package net.sf.jremoterun.utilities.nonjdk.log.tojdk

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JdkLoggerSwitch {


    @Deprecated
    public static boolean useJdkLogger = true

}
