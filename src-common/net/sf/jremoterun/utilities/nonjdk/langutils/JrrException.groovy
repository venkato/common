package net.sf.jremoterun.utilities.nonjdk.langutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class JrrException extends Exception{


    JrrException(String var1) {
        super(var1)
    }

    JrrException(String var1, Throwable var2) {
        super(var1, var2)
    }
}
