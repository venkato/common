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
class HttpsClientRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    HttpsClientRed() {
        super(new ClRef('sun.net.www.protocol.https.HttpsClient'))
    }

    void redifineHttpsCertificateCheck1() throws Exception {
        
        ClRef cr = new ClRef('sun.net.www.protocol.https.HttpsClient')
        final CtMethod method=JrrJavassistUtils.findMethodByCount(cr,cc,"checkURLSpoofing",1)
        method.setBody("{}");
        doRedefine()
    }

}
