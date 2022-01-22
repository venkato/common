package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtClass
import javassist.CtMethod;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger


@CompileStatic
class X509TrustManagerImplRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    X509TrustManagerImplRed() {
        super(new ClRef('sun.security.ssl.X509TrustManagerImpl'))
    }

     void redefineX509TrustManagerImpl() throws Exception {
        ClRef cr = new ClRef('sun.security.ssl.X509TrustManagerImpl')
        final Collection<CtMethod> methods = cc.getDeclaredMethods().findAll { it.getName() == 'checkTrusted' };
        assert methods.size() == 2
        methods.each { it.setBody("{}") };
        doRedefine()
    }

}
