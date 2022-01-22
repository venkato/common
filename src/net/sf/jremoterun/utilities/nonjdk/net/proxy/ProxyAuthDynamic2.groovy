package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.ObjectHolder

import java.util.logging.Logger


@CompileStatic
public class ProxyAuthDynamic2 extends Authenticator {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean logAccess = false
    public String user;
    public ObjectHolder<String> passwordHolder ;

    ProxyAuthDynamic2(String user, ObjectHolder<String> passwordHolder) {
        this.user = user
        this.passwordHolder = passwordHolder
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (logAccess) {
            log.info "return proxy password for user ${user}"
        }
        char[] password2
        String password = passwordHolder.getObject()
        if (password == null) {
            password2 = new char[0]
        } else {
            password2 = password.toCharArray()
        }
        PasswordAuthentication auth = new PasswordAuthentication(user, password2);
        return auth;
    }

}