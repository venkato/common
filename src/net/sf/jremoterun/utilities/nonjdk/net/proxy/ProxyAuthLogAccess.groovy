package net.sf.jremoterun.utilities.nonjdk.net.proxy

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger


@CompileStatic
public class ProxyAuthLogAccess extends Authenticator {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean logAccess = true

    @Override
    protected PasswordAuthentication getPasswordAuthentication() {
        if (logAccess) {
            log.info "requested auth"
        }
        return null
    }


}