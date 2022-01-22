package net.sf.jremoterun.utilities.nonjdk.gradle.runner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.InitLogTracker
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.initialization.InitScript

@CompileStatic
class JrrGradleInit2 extends InjectedCode {
    //private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public ClRef jrrGradleInit36 = new ClRef('net.sf.jremoterun.utilities.nonjdk.gradle.runner.JrrGradleInit3')

    @Override
    Object getImpl(Object key) throws Exception {
        InitLogTracker.defaultTracker.addLog('gradle called2')
        //println "works2"
        List list1 = key as List;
        InitScript initScript1 = list1[0] as InitScript;
        JrrGradleEnv.gradleEnv.initScript = initScript1;
        String customHandelr1 = list1[1]
        if(customHandelr1!=null){
            JrrGradleEnv.gradleEnv.customHandler = new ClRef(customHandelr1)
        }

        //net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2.setConsoleOutIfNotInited()
        initScript1.initscript { ScriptHandler sh1 ->
            InitLogTracker.defaultTracker.addLog('gradle called3')
            JrrGradleEnv.gradleEnv.sh = sh1;
            //println "works3"
            ClassLoader cll1 = JrrClassUtils.getCurrentClassLoader();
            //GroovyClassLoader cll = (GroovyClassLoader)cll1;
            ClRef jrrGradleInit36 = new ClRef('net.sf.jremoterun.utilities.nonjdk.gradle.runner.JrrGradleInit3')
            ClRef jrrGradleInit34 = jrrGradleInit36
            Object r3 = jrrGradleInit34.newInstance2(cll1)
            Runnable r4 = r3 as Runnable
            ContextClassLoaderWrapper.wrap2(cll1, r4);
        }
        return null
    }
}
