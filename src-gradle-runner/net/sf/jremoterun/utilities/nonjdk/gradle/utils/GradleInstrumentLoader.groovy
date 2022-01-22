package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic
import net.sf.jremoterun.SimpleJvmTiAgent;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef

import java.lang.instrument.Instrumentation;
import java.util.logging.Logger;

@CompileStatic
class GradleInstrumentLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void doStuff(){
        if(SimpleJvmTiAgent.instrumentation==null){
            doStuffImpl()
        }
    }

    static void doStuffImpl(){
        // not supported class redefinition
        // https://github.com/gradle/gradle/blob/master/platforms/core-runtime/instrumentation-agent/src/main/java/org/gradle/instrumentation/agent/Agent.java
        ClRef clRef = new ClRef('org.gradle.instrumentation.agent.Agent')
        Class clazz1 = clRef.loadClass(ClassLoader.getSystemClassLoader())
        java.lang.instrument.Instrumentation i = JrrClassUtils.getFieldValueR(clRef,clazz1,'instrumentation') as Instrumentation
        SimpleJvmTiAgent.instrumentation = i
    }

}
