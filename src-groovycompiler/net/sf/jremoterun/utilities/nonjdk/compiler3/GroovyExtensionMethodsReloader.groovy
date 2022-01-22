package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.transform.stc.ExtensionMethodCache;

import java.util.logging.Logger;

@CompileStatic
class GroovyExtensionMethodsReloader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void rescanExtensionMethodsAll() {
        //getCache().clear()
        org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport.clearExtensionMethodCache()
    }

    static  Map<String, List>  rescanExtensionMethodsCurrentClassloader() {
        return rescanExtensionMethods(JrrClassUtils.getCurrentClassLoader());
    }

    static Map<ClassLoader, Map<String, List>> getCache() {
        ExtensionMethodCache instance1 = ExtensionMethodCache.INSTANCE
        Map<ClassLoader, Map> mmm = JrrClassUtils.getFieldValue(instance1, 'cache') as Map;
        return mmm
    }

    static Map<String, List> removeExtensionMethods(ClassLoader loader1) {
        Map<ClassLoader, Map<String, List>> mmm = getCache()
        Map<String, List> ooo = mmm.remove(loader1)
        log.info "removed ? ${ooo != null}"
//        JrrClassUtils.invokeJavaMethod2(instance1,'getMethodsFromClassLoader', loader1);
        return ooo;
    }

    /**
     * @see org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport#clearExtensionMethodCache(java.lang.ClassLoader)
     */
    static Map<String, List> rescanExtensionMethods(ClassLoader loader1) {
        Map<String, List> ooo = removeExtensionMethods(loader1)
        ExtensionMethodCache instance1 = ExtensionMethodCache.INSTANCE
        instance1.get(loader1)
        return ooo;
    }

}
