package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class ExtClassloaderFinder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile ExtClassloaderFinder extClassloaderFinder = new ExtClassloaderFinder();

    public ClassLoader cachedClassloder;

    ClassLoader findExtClassLoader() {
        if (cachedClassloder != null) {
            return cachedClassloder
        }
        cachedClassloder = CreateGroovyClassLoader.findExtClassLoader()
        return cachedClassloder;
    }

}
