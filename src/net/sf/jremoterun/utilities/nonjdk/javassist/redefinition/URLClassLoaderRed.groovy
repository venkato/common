package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class URLClassLoaderRed extends  RedefinitionBase{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    URLClassLoaderRed() {
        super(URLClassLoader)
    }

    void redefineUrlClassLoader() throws Exception {
        Class clazz = URLClassLoader
        CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "isSealed", 2);

        method1.setBody("{return false;}");
        doRedefine()
    }

}
