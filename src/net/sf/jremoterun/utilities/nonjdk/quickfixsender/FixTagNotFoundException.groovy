package net.sf.jremoterun.utilities.nonjdk.quickfixsender

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class FixTagNotFoundException extends Exception{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    FixTagNotFoundException(String msg) {
        super(msg)
    }
}
