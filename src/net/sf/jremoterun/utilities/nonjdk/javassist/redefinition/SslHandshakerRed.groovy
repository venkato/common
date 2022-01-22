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
class SslHandshakerRed extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    SslHandshakerRed() {
        super(new ClRef('sun.security.ssl.Handshaker'))
    }
/**
 * Not works on java9+
 */
    void redefineSslHandshaker() throws Exception {
        final CtMethod method = JrrJavassistUtils.findMethodByCount(new ClRef('sun.security.ssl.Handshaker'), cc, 'fatalSE', 3)
        method.setBody("{}");
        doRedefine()
    }
}
