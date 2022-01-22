package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.ClassPool
import javassist.CtClass
import javassist.CtMethod
import net.sf.jremoterun.SimpleJvmTiAgent;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils

import java.lang.instrument.ClassDefinition
import java.lang.reflect.AccessibleObject;
import java.util.logging.Logger;

@CompileStatic
class AccessibleObjectCheckCanSetAccessibleDisable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void disableCanSetAccessible(){
        Class clazz = AccessibleObject;
        final ClassPool cp = ClassPool.getDefault();
        CtClass ctClass = cp.get(clazz.getName())
        JrrJavassistUtils.findMethodByCount(clazz,ctClass,"checkCanSetAccessible",3)
        CtMethod[] methods = ctClass.getDeclaredMethods("checkCanSetAccessible")
        CtMethod method = methods.toList().find {it.getParameterTypes().length==3}
        method.setBody('{ return true; }')
        final ClassDefinition classDefinition = new ClassDefinition(clazz, ctClass.toBytecode());
        final ClassDefinition[] classDefinitions = [ classDefinition ];
        if (SimpleJvmTiAgent.instrumentation == null) {
            throw new NullPointerException("SimpleJvmTiAgent.instrumentation is null");
        }
        SimpleJvmTiAgent.instrumentation.redefineClasses(classDefinitions);

    }


}
