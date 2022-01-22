package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

/**
 *  @see org.apache.ivy.util.url.IvyAuthenticator
 */
@CompileStatic
public class ProxyAuth extends Authenticator {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean logAccess = false
    private PasswordAuthentication auth;

    public ProxyAuth(String user, String password) {
        char[] password2
        if (password == null) {
            password2 = new char[0]
        } else {
            password2 = password.toCharArray()
        }
        auth = new PasswordAuthentication(user, password2);
    }

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (logAccess) {
            String info1
            if (auth == null) {
                log.info 'no auth set'
            } else {
                log.info "return proxy password for user ${auth.getUserName()}"
            }
        }
        return auth;
    }
}