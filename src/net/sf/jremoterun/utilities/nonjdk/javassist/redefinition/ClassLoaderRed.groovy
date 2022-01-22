package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger

import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.findMethod
import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.getClassFromDefaultPool
import static net.sf.jremoterun.utilities.javassist.JrrJavassistUtils.redefineClass;

@CompileStatic
class ClassLoaderRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassLoaderRed() {
        super(ClassLoader)
    }

    void redefineClassLoader() throws Exception {
        Class class1 = ClassLoader
        CtBehavior method1;
        try {
            method1 = findMethod(class1, cc, "checkCerts", 2);
        } catch (NoSuchMethodException e) {
            log.info("failed find checkCerts method ${e}");
            return;
        }
        method1.setBody("{}");
        doRedefine()
    }


    void redefineClassloaderCertCheck() throws Exception {
        Class clazz = ClassLoader
        CtBehavior method1 = JrrJavassistUtils.findMethod(clazz, cc, "checkCerts", 2);
        method1.setBody("{}");
        doRedefine();
    }

}
