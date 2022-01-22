package net.sf.jremoterun.utilities.nonjdk.net.ssl

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.security.cert.CertificateException;
import java.util.logging.Logger;

@CompileStatic
class CertificateExceptionJrr extends CertificateException{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


}
