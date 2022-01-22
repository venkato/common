package net.sf.jremoterun.utilities.nonjdk.javassist.redefinition

import groovy.transform.CompileStatic
import javassist.CtBehavior
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class PackageRed  extends  RedefinitionBase{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    PackageRed() {
        super(java.lang.Package)
    }

    void redefinePackage() throws Exception {
        Class clazz = java.lang.Package
        if (true) {
            try {
                CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "isSealed", 1);
                method1.setBody("{return false;}");
            }catch(NoSuchMethodException e){
                log.info( "failed find isSealed method with 1 param ",e)
            }
        }
        if (true) {
            CtBehavior method1 = JrrJavassistUtils.findMethodByCount(clazz, cc, "isSealed", 0);
            method1.setBody("{return false;}");
        }
        doRedefine()
    }

}
