package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@Deprecated
@CompileStatic
class BytesToHumanConverter extends net.sf.jremoterun.utilities.nonjdk.io.byteshuman.BytesToHumanConverter{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    BytesToHumanConverter() {
    }

    BytesToHumanConverter(int roundProgressSpeed) {
        super(roundProgressSpeed)
    }
}
