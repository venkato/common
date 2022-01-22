package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jediterm.terminal.TtyConnector
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class MyPasswordEnter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String readMyPassword(TtyConnector ttyConnector ){
        return net.sf.jremoterun.utilities.nonjdk.sshsup.SshConSet2.defaultPassword
    }


}
