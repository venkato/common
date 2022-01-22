package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.MethodRedefineHelper
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class RuntimeRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    RuntimeRed() {
        super(Runtime)
    }

    void redefineSystemExit(boolean append) throws Exception {
        
        Class clazz = Runtime
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "exit", 1);
        if(append){
            method1.insertBefore("{ ${MethodRedefineHelper.dumpStackTrace} }");
        }else {
            method1.setBody("{ ${MethodRedefineHelper.dumpStackTrace} }");
        }
        doRedefine();
    }

}
