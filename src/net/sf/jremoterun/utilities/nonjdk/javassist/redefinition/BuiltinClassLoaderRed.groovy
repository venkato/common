package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefintions
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils;

import java.util.logging.Logger

import static net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils.findMethod
import static net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils.findMethodByCount

@CompileStatic
class BuiltinClassLoaderRed  extends RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    BuiltinClassLoaderRed() {
        super(new ClRef('jdk.internal.loader.BuiltinClassLoader'))
    }

    void redefineJava11BuiltinClassLoaderIfCan() throws Exception {
        boolean isJava9Plus = SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)
        if(isJava9Plus){
            redefineJava11BuiltinClassLoader()
        }else {

        }
    }


    void redefineJava11BuiltinClassLoader() throws Exception {
        
        ClRef cr = new ClRef('jdk.internal.loader.BuiltinClassLoader')
        final CtBehavior isSealedMethod = findMethodByCount(cr, cc, "isSealed", 2);

        isSealedMethod.setBody("{return false;}") ;
        doRedefine()
    }

}
