package net.sf.jremoterun.utilities.nonjdk.quickfixsender.artio

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class AtrioSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void enableLogging(){
        System.setProperty("fix.core.debug", "FIX_MESSAGE");
    }


}
