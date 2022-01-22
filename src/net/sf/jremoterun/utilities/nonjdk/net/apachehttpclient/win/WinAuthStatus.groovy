package net.sf.jremoterun.utilities.nonjdk.net.apachehttpclient.win

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
enum WinAuthStatus {
    notInited,
    auth1Done,
    auth3Done,

    ;

}
