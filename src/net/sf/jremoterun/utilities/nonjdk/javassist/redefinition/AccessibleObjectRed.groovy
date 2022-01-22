package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class AccessibleObjectRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    AccessibleObjectRed() {
        super(java.lang.reflect.AccessibleObject)
    }

    /**
     *
     * @since till java8. Not work in java11+
     */
    void redefineAccessibleObject() throws Exception {
        Class clazz = java.lang.reflect.AccessibleObject
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "slowCheckMemberAccess", 5);
        method1.setBody("{}");
        doRedefine();
    }


    /**
     *
     * @since  java11+
     */
    void redefineCheckCanSetAccessible() throws Exception {
        // see also
        new ClRef('net.sf.jremoterun.utilities.java11.ModuleCheckDisable')
        new ClRef('net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable')
        Class clazz = java.lang.reflect.AccessibleObject
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "checkCanSetAccessible", 3);
        method1.setBody("{return true ; }");
        doRedefine();
    }

}
