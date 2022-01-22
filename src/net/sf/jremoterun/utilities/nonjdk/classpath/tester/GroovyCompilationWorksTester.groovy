package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic
import net.sf.jremoterun.URLClassLoaderExt
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.javassist.codeinjector.InjectedCode
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.compiler3.CreateGroovyClassLoader
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorIThrowImmediate;

import java.util.logging.Logger;

@CompileStatic
class GroovyCompilationWorksTester {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AddFilesToUrlClassLoaderGroovy adder
    public URLClassLoader classLoader1;
    public ClassPathTesterHelper2 helper2 = new ClassPathTesterHelper2(new ProblemCollectorIThrowImmediate())

    void prepare() {
        createClassLoader()
        adder = new AddFilesToUrlClassLoaderGroovy(classLoader1);
        addFiles()
    }

    void createClassLoader(){
        ClassLoader extClassLoader = CreateGroovyClassLoader.findExtClassLoader()
        log.info "aa = ${extClassLoader.getClass().getName()}"
        classLoader1 = new URLClassLoaderExt(new URL[0], extClassLoader)
    }

    void addFiles(){
        adder.addAll JrrStarterJarRefs2.values().toList()
        adder.add JrrStarterJarRefs.jrrutilitiesOneJar
    }


    void doTest(Class testClazz, String resturnValue) {
        ContextClassLoaderWrapper.wrap2(classLoader1, {
            doTestImpl(testClazz, resturnValue)
        });
    }

    void doChecksImpl1(){
        helper2.checkTheSameClassLoader5(new ClRef(GroovyObject), classLoader1)
        helper2.checkTheSameClassLoader5(new ClRef(InjectedCode), classLoader1)

    }

    void doTestImpl(Class testClazz, String returnValue) {
        Class<?> clazz1 = classLoader1.loadClass(testClazz.getName())
        helper2.checkTheSameClassLoader5(clazz1, classLoader1)
        doChecksImpl1()
        helper2.checkInstanceOf(clazz1, new ClRef(GroovyObject), classLoader1)
        assert clazz1.getClassLoader() == classLoader1
        Map mm = clazz1.newInstance() as Map
        Object res = mm.get(null)
        assert res == returnValue

    }


}
