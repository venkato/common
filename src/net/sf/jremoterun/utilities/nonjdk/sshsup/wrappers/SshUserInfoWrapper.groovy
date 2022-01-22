package net.sf.jremoterun.utilities.nonjdk.sshsup.wrappers

import com.jcraft.jsch.UserInfo
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class SshUserInfoWrapper implements UserInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public UserInfo userInfoNested;

    public HashSet<String> ignoreMsgContains = new HashSet<>()

    SshUserInfoWrapper(UserInfo userInfoNested) {
        this.userInfoNested = userInfoNested
    }

    static SshUserInfoWrapper createWrapper(UserInfo userInfo){
        if (userInfo instanceof SshUserInfoWrapper) {
            SshUserInfoWrapper infoWrapper = (SshUserInfoWrapper) userInfo;
            return infoWrapper
        }
        if(userInfo==null){
            return null
        }
        return new SshUserInfoWrapper(userInfo)
    }

    @Override
    String getPassphrase() {
        return userInfoNested.getPassphrase()
    }

    @Override
    String getPassword() {
        return userInfoNested.getPassword()
    }

    @Override
    boolean promptPassword(String message) {
        return userInfoNested.promptPassword(message)
    }

    @Override
    boolean promptPassphrase(String message) {
        return userInfoNested.promptPassphrase(message)
    }

    @Override
    boolean promptYesNo(String message) {
        return userInfoNested.promptYesNo(message)
    }



    @Override
    void showMessage(String message) {
        if(message==null){
            userInfoNested.showMessage(null)
        }else {
            String find1 = ignoreMsgContains.find { message.contains(it) }
            if(find1==null) {
                userInfoNested.showMessage(message)
            }else{
                log.info("skip show msg to user = "+message)
            }
        }
    }
}
