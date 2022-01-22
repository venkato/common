package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class AccessibleObjectRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AccessibleObjectRed() {
        super(java.lang.reflect.AccessibleObject)
    }

    void redefineAccessibleObject() throws Exception {
        Class clazz = java.lang.reflect.AccessibleObject
        CtBehavior method1 = JrrJavassistUtils.findMethod(clazz, cc, "slowCheckMemberAccess", 5);
        method1.setBody("{}");
        doRedefine();
    }

}
