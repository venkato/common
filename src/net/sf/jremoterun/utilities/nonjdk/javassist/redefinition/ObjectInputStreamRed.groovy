package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase

import java.util.logging.Logger

@CompileStatic
class ObjectInputStreamRed extends  RedefinitionBase{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ObjectInputStreamRed() {
        super(ObjectInputStream)
    }

    void redefineUrlClassLoader() throws Exception {
        Class clazz = ObjectInputStream
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "filterCheck", 2);

        method1.setBody("{}");
        doRedefine()
    }

}
