package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger


@CompileStatic
public abstract class ProxyAuthDynamic extends Authenticator {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean logAccess = false
    public String user

    public ProxyAuthDynamic(String user) {
        this.user = user
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (logAccess) {
            log.info "return proxy password for user ${user}"
        }
        char[] password2
        String password = readPassword()
        if (password == null) {
            password2 = new char[0]
        } else {
            password2 = password.toCharArray()
        }
        PasswordAuthentication auth = new PasswordAuthentication(user, password2);
        return auth;
    }

    abstract String readPassword()
}