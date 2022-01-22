package net.sf.jremoterun.utilities.nonjdk.javassist


import groovy.transform.CompileStatic
import javassist.ClassClassPath
import javassist.ClassPath
import javassist.ClassPool
import javassist.runtime.Desc
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.AccessibleObjectRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.BuiltinClassLoaderRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.ClassLoaderRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.HttpsClientRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.InetAddressRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.ObjectInputStreamRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.PackageRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.ReflectionRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.RuntimeRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.ServiceLoaderRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.SocketRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.SslHandshakerRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.SystemRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.URLClassLoaderRed
import net.sf.jremoterun.utilities.nonjdk.javassist.redefinition.X509TrustManagerImplRed
import org.apache.commons.lang3.JavaVersion
import org.apache.commons.lang3.SystemUtils

import java.util.logging.Logger

@CompileStatic
public class ClassRedefintions {

    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());


    private static volatile boolean initDone = false;

    static void init() throws Exception {
        if (!initDone) {
            initDone = true;
            ClassPool.doPruning = false;
            Desc.useContextClassLoader = true;
            final ClassPool pool = ClassPool.getDefault();
            final ClassPath classPath = new ClassClassPath(JrrClassUtils.getCurrentClass());
            pool.appendClassPath(classPath);
        }
    }


    static void redifinePackage() throws Exception {
        new PackageRed().redefinePackage()
    }

    static void redefineSystemExit(boolean append) throws Exception {
        new RuntimeRed().redefineSystemExit(append)
    }

    static void redifineUrlClassLoader() throws Exception {
        new URLClassLoaderRed().redefineUrlClassLoader()
    }

    static void redifineObjectInputStream() throws Exception {
        new ObjectInputStreamRed().redefineUrlClassLoader()
    }


    static void redifineSecurityManager() throws Exception {
        new SystemRed().redefineSecurityManager()
    }


    static void redefineClassloaderCertCheck() throws Exception {
        new ClassLoaderRed().redefineClassloaderCertCheck()
    }

    static void redifineAccessibleObject() throws Exception {
        new AccessibleObjectRed().redefineAccessibleObject()
    }


    static void redifineSocketClass() throws Exception {
        new SocketRed().redefineSocketClass()
    }

    static void redefineServiceLoader(String jrrServiceLoaderFactoryS) throws Exception {
        new ServiceLoaderRed().redefineServiceLoader(jrrServiceLoaderFactoryS)
    }


    static void redifineHttpsCertificateCheck1() throws Exception {
        new HttpsClientRed().redifineHttpsCertificateCheck1()
    }

    static void redefineSslHandshakerIfCan() throws Exception {
        boolean isJava9Plus = SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)
        if (isJava9Plus) {

        } else {
            redefineSslHandshaker()
        }
    }

    /**
     * Not works on java9+
     */
    static void redefineSslHandshaker() throws Exception {
        new SslHandshakerRed().redefineSslHandshaker()
    }


    static void redefineJava11BuiltinClassLoaderIfCan() throws Exception {
        boolean isJava9Plus = SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)
        if (isJava9Plus) {
            redefineJava11BuiltinClassLoader()
        } else {

        }
    }

    static void redefineJava11BuiltinClassLoader() throws Exception {
        new BuiltinClassLoaderRed().redefineJava11BuiltinClassLoader()
    }

    static void redefineX509TrustManagerImpl() throws Exception {
        new X509TrustManagerImplRed().redefineX509TrustManagerImpl()
    }

    static void redefineSunReflectionIfCan() throws Exception {
        boolean isJava9Plus = SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_9)
        if (isJava9Plus) {

        } else {
            redefineSunReflection()
        }
    }

    static void redefineSunReflection() throws Exception {
        new ReflectionRed().redefineSunReflection()
    }


    static void redifineClassLoader() throws Exception {
        new ClassLoaderRed().redefineClassLoader()
    }


    /**
     * don't work on java21
     */
    static void redefindeDnsResolving() {
        new InetAddressRed().redefindeDnsResolving()
    }


}
