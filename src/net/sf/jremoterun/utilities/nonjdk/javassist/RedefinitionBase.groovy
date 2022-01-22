package net.sf.jremoterun.utilities.nonjdk.javassist

import groovy.transform.CompileStatic
import javassist.CtClass;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils

import java.util.logging.Logger;

@CompileStatic
class RedefinitionBase {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Class clazz ;
    public CtClass cc

    RedefinitionBase(ClRef cr) {
        clazz = cr.loadClass2();//cr.loadClass(JrrUtilities.getCurrentClassLoader());
        doInit1()
        doInit2LoadCtClass()
    }

    RedefinitionBase(Class clazz) {
        this.clazz = clazz
        doInit1()
        doInit2LoadCtClass()

    }

    void doInit1(){
        ClassRedefintions.init();
    }


    void doInit2LoadCtClass(){
        cc = JrrJavassistUtils.getClassFromDefaultPool(clazz);
    }

    void doRedefine(){
        doRedefineImpl()
    }

    final void doRedefineImpl(){
        JrrJavassistUtils.redefineClass(cc, clazz);
    }
}
