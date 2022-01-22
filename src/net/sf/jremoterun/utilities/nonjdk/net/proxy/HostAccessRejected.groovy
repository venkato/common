package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class HostAccessRejected extends RuntimeException {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    HostAccessRejected(String var1) {
        super(var1)
    }
}
