package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs

import java.util.logging.Logger

@CompileStatic
class CreateGroovyClassLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ClRef groovyClassloaderClRef = new ClRef('groovy.lang.GroovyClassLoader')
    public static ClRef sunClassLoader = new ClRef('sun.misc.Launcher$ExtClassLoader')
    public static ClRef jdk11InternalClassLoaderApp = new ClRef('jdk.internal.loader.ClassLoaders$AppClassLoader')
    public static ClRef jdk11InternalClassLoader = new ClRef('jdk.internal.loader.ClassLoaders$PlatformClassLoader')


    static URLClassLoader createGroovyClassLoader(ClassLoader parent) {
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[0], parent)
        AddFilesToUrlClassLoaderGroovy adder1 = new AddFilesToUrlClassLoaderGroovy(urlClassLoader)
        adder1.add( JrrClassLocationRefs.GroovyObject1)
        return createGroovyClassLoader2(urlClassLoader)
    }

    static URLClassLoader createGroovyClassLoader2(ClassLoader urlClassLoader) {
        URLClassLoader urlClassLoader2 = urlClassLoader.loadClass(groovyClassloaderClRef.className).newInstance(urlClassLoader) as URLClassLoader;
        assert urlClassLoader2.getClass().getName() == groovyClassloaderClRef.className
        assert !urlClassLoader2.getClass().is(GroovyClassLoader)
        return urlClassLoader2

    }


    static ClassLoader findExtClassLoader() {
        ClassLoader loader = JrrClassUtils.getCurrentClassLoader()
        List<String> usedClassloadersClassNames= []
        ClassLoader classLoader = findExtClassLoaderImpl(loader,usedClassloadersClassNames)
        if (classLoader == null) {
            throw new Exception("failed find ext classloader for ${loader}, used classLoaders : ${usedClassloadersClassNames.join(',')}")
        }
        return classLoader
    }

    static ClassLoader findExtClassLoaderImpl(ClassLoader loader,List<String> usedClassloadersClassNames) {
        if (loader == null) {
            return null
        }
        String className1 = loader.getClass().getName()
        usedClassloadersClassNames.add(className1)
        if (className1 == sunClassLoader.className) {
            return loader
        }
        if (className1 == jdk11InternalClassLoader.className) {
//            ClassLoader classLoaderParent =  loader.getParent()
//            log.info "classLoaderParent = ${classLoaderParent}"
//            return classLoaderParent
            return loader;
        }
        return findExtClassLoaderImpl(loader.getParent(),usedClassloadersClassNames);
    }


}
