package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger

import static net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils.LogVarName2
import static net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils.createLogVar
import static net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils.findMethod

@CompileStatic
class SocketRed extends RedefinitionBase  {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    SocketRed() {
        super(Socket)
    }

     void redefineSocketClass() throws Exception {
        
        Class clazz = Socket
        CtBehavior intiMethod = JrrJavassistUtils.findConstructorByCount(clazz, cc, 2)
        intiMethod.insertBefore("\$2=446;");
        final CtBehavior connectMethod = JrrJavassistUtils.findMethodByCount(clazz, cc, "connect", 2);
        connectMethod.insertBefore("""
                ${createLogVar}
                ${LogVarName2}.info(\$1.toString());
            """);
        connectMethod.insertAfter("""
                ${createLogVar}
                ${LogVarName2}.info(\$1.toString()+" connect ok");
            """)
        doRedefine();
    }

}
