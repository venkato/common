package net.sf.jremoterun.utilities.nonjdk.sshsup

import com.jcraft.jsch.UserInfo
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef

import java.util.logging.Logger

@CompileStatic
class UserInfoHostKeyBypass implements UserInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //public UserInfo original;
//    public JrrJschSession session;

    UserInfoHostKeyBypass( ) {
//        this.session = session
    }

    @Override
    String getPassphrase() {
        return null
    }

    @Override
    String getPassword() {
        return null;
    }

    @Override
    boolean promptPassword(String message) {
        log.info message
        return false
    }

    @Override
    boolean promptPassphrase(String message) {
        log.info message
        return false
    }

    @Override
    boolean promptYesNo(String message) {
        log.info message
        return true
    }

    @Override
    void showMessage(String message) {
        log.info message
    }
}
