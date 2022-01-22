package net.sf.jremoterun.utilities.nonjdk.javassist;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.AccessibleObjectRed
import org.junit.Test;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
class ClassRedefinitionTester implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Test
    @Override
    void run() {
        ClassRedefintions.redefineSunReflectionIfCan()
        if(net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable.isJava11()){
            new AccessibleObjectRed().redefineCheckCanSetAccessible()
        }else {
            ClassRedefintions.redifineAccessibleObject()
        }
        ClassRedefintions.redefineClassloaderCertCheck()
        ClassRedefintions.redefineX509TrustManagerImpl()
        ClassRedefintions.redifinePackage()
        ClassRedefintions.redifineUrlClassLoader()
        ClassRedefintions.redifineSecurityManager()
        ClassRedefintions.redifineSocketClass()
        ClassRedefintions.redifineHttpsCertificateCheck1()
        ClassRedefintions.redifineClassLoader()
        //ClassRedefintions.redefindeDnsResolving()
        ClassRedefintions.redefineSystemExit(true)

        LoggigingRedefine.redifineCommonsLoggingGetLog()
//        LoggigingRedefine.redifineSl4jLoggingGetLog()

    }
}
