package net.sf.jremoterun.utilities.nonjdk.langutils

import groovy.transform.CompileStatic

@CompileStatic
class JrrRuntimeException extends RuntimeException{


    JrrRuntimeException(String var1) {
        super(var1)
    }

    JrrRuntimeException(String var1, Throwable var2) {
        super(var1, var2)
    }
}
