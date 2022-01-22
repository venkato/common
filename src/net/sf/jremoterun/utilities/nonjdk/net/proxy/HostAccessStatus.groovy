package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum HostAccessStatus {

    proxy,
    noProxy,
    reject,
    ;

}
