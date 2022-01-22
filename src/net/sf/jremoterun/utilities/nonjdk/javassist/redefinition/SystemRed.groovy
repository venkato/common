package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class SystemRed extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    SystemRed() {
        super(java.lang.System)
    }

    boolean canDo() {
        try {
            JrrClassUtils.findMethodByCount(java.lang.System, 'setSecurityManager0', 1)
            return true
        } catch (NoSuchMethodException e) {
            log.debug3("failed find setSecurityManager0", e)
            return false
        }
    }

    void redefineSecurityManagerIfCan() throws Exception {
        if (canDo()) {
            redefineSecurityManager()
        }

    }

    void redefineSecurityManager() throws Exception {
        Class clazz = java.lang.System
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "setSecurityManager", 1);

        method1.setBody("{setSecurityManager0(null);}");
        doRedefine();
    }

}
