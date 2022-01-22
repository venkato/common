package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtMethod
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase

import java.util.logging.Logger

@CompileStatic
class X509TrustManagerImpl2Red extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    X509TrustManagerImpl2Red() {
        super(new ClRef('sun.security.ssl.CertificateMessage$T13CertificateConsumer'))
    }

     void redefineX509TrustManagerImpl() throws Exception {
         new ClRef('sun.security.ssl.CertificateMessage.T13CertificateConsumer')
        ClRef cr = new ClRef('sun.security.ssl.CertificateMessage$T13CertificateConsumer')
        final Collection<CtMethod> methods = cc.getDeclaredMethods().findAll { it.getName() == 'checkServerCerts' };
        methods.each { it.setBody("{ return null; }") };
        doRedefine()
    }

}
