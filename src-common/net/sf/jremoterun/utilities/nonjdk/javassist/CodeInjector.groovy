package net.sf.jremoterun.utilities.nonjdk.javassist

import groovy.transform.CompileStatic
import net.sf.jremoterun.RemoteRunner
import net.sf.jremoterun.SharedObjectsUtils;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesMapBuilder
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode

import javax.management.ObjectName
import java.lang.management.ManagementFactory;
import java.util.logging.Logger;

@CompileStatic
class CodeInjector {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static String codeModificationHooks = "codeModificationHooks";
    public static String myHookVar  = '_myHook'
    public static String sharedObjectsVar  = '_sharedObjects'
    public static String injectorVar  = '_injector'

    public static String sharedObjectsVars =
            """
            java.util.Map ${sharedObjectsVar} = 
            (java.util.Map) ${ManagementFactory.name}.getPlatformMBeanServer().getAttribute(new ${ObjectName.getName()}("${
                RemoteRunner.runner
            }"), "SharedObjects");
			if(${sharedObjectsVar} == null ){
				throw new NullPointerException("Failed received SharedObjects");
			}		
            java.util.Map ${injectorVar} =  (java.util.Map) ${sharedObjectsVar}.get("${codeModificationHooks}");
			if(${injectorVar} == null ){
				throw new NullPointerException("Failed received hook service ${codeModificationHooks}");
			}
        """

    static String createHookVar(String name) {
        return """
            java.util.Map ${myHookVar} = ${injectorVar}.get("${name}");
			if(${myHookVar} == null ){
				throw new NullPointerException("Failed received hook : ${name}");
			}
        """;
    }

    static String createSharedObjectsHookVar(String name) {
        return """
            ${sharedObjectsVars}
            ${createHookVar(name)};
        """;
    }

    static String createSharedObjectsHookVar2(Class modifClass) {
        return createSharedObjectsHookVar(modifClass.getName());
    }


    static void putInector1(String name, InjectedCode code) {
        Map globalMap = SharedObjectsUtils.getGlobalMap();
        Map buildObject = (Map) JrrUtilitiesMapBuilder.buildObject(globalMap, codeModificationHooks, JrrUtilitiesMapBuilder.constructorConcurrentHashMap);
        buildObject.put(name, code);
    }


    static void putInector2(Class modifClass, InjectedCode code) {
        putInector1(modifClass.name,code);
    }



}
