package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtMethod;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils;

import java.util.logging.Logger

@CompileStatic
class ReflectionRed extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ReflectionRed() {
        super(new ClRef('sun.reflect.Reflection'))
    }

    void redefineSunReflectionIfCan() throws Exception {
        boolean isJava9Plus = SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)
        if(isJava9Plus){

        }else {
            redefineSunReflection()
        }
    }

    void redefineSunReflection() throws Exception {
        ClRef clRef = new ClRef('sun.reflect.Reflection')
        final CtMethod method1 = JrrJavassistUtils.findMethodByCount(clRef,cc,'ensureMemberAccess',4)
        method1.setBody("{}")
        final CtMethod method2 = JrrJavassistUtils.findMethodByCount(clRef, cc,'verifyMemberAccess',4)
        method2.setBody("{return true;}")
        doRedefine()
    }

}
