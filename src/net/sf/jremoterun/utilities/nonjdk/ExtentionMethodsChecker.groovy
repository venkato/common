package net.sf.jremoterun.utilities.nonjdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import org.codehaus.groovy.ast.MethodNode

import java.util.logging.Logger

@CompileStatic
class ExtentionMethodsChecker implements Runnable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Map<String, List<MethodNode>> cachedMethods

    @Override
    void run() {
        check1()
    }

    static void findCachedMethods() {
        Object obj = JrrClassUtils.getFieldValue(org.codehaus.groovy.transform.stc.StaticTypeCheckingSupport, "EXTENSION_METHOD_CACHE")
        try {
            cachedMethods = (Map) JrrClassUtils.getFieldValueR(new ClRef('org.codehaus.groovy.transform.stc.ExtensionMethodCache'),obj, "cachedMethods") //NOFIELDCHECK
        }catch(NoSuchFieldException e){
            log.info("seems new groovy, as failed find cachedMethods field for class ${obj.getClass().getName()} : ${e}")
            cachedMethods = (Map) JrrClassUtils.getFieldValueR(new ClRef('org.codehaus.groovy.transform.stc.ExtensionMethodCache'), obj, "cache")
        }
        if (cachedMethods == null) {
            throw new IllegalStateException("cachedMethods was not initialized")
        }
    }

    static void check1() {
        List<MethodNode> methodsLogger = getMethods(Logger)
        if (methodsLogger.find { it.name == 'loge' } == null) {
            throw new Exception("failed find extension method loge for class Logger")
        }
        List<MethodNode> methodsFile = getMethods(File)
        if (methodsFile.find { it.name == 'child' } == null) {
            throw new Exception("failed find extension method child for class File")
        }
    }

    static List<MethodNode> getMethods(Class clazz) {
        if (cachedMethods == null) {
            findCachedMethods()
        }
        List<MethodNode> methodNodes = cachedMethods.get(clazz.name)
        if (methodNodes == null) {
            throw new IllegalStateException("Failed find extention methods for class : ${clazz.getName()}")
        }
        return methodNodes

    }

}
