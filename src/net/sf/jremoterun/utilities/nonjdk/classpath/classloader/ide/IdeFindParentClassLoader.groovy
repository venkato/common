package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide;

import groovy.transform.CompileStatic
import net.sf.jremoterun.ClassLoaderNotFoundException
import net.sf.jremoterun.FindParentClassLoader
import net.sf.jremoterun.SimpleFindParentClassLoader;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;


@CompileStatic
class IdeFindParentClassLoader implements FindParentClassLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public SimpleFindParentClassLoader simpleFindParentClassLoader = new SimpleFindParentClassLoader();

    public FindParentClassLoader ideFindParentClassLoader;

    IdeFindParentClassLoader(FindParentClassLoader ideFindParentClassLoader) {
        this.ideFindParentClassLoader = ideFindParentClassLoader
    }

    @Override
    ClassLoader findClassLoader(Serializable classLoaderId) throws ClassLoaderNotFoundException {
        ClassLoader classLoaderImpl = simpleFindParentClassLoader.findClassLoaderImpl(classLoaderId, false)
        if (classLoaderImpl != null) {
            return classLoaderImpl
        }
        //if (classLoaderId instanceof String) {
        ClassLoader pluginGeneric = ideFindParentClassLoader.findClassLoader((String) classLoaderId)
        if (pluginGeneric != null) {
            return pluginGeneric;
        }
        //}

        //return (classLoaderId)
        throw new Exception("failed find classLoaderId = ${classLoaderId}")
    }


    void selfSet() {
        net.sf.jremoterun.SharedObjectsUtils.setFindParentClassLoader(this)
    }
}
