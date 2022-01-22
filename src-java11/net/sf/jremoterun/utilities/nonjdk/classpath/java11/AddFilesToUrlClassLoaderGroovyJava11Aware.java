package net.sf.jremoterun.utilities.nonjdk.classpath.java11;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy;
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.net.URLClassLoader;

@CompileStatic
public class AddFilesToUrlClassLoaderGroovyJava11Aware {

    public static ClRef java11Classloader = new ClRef("jdk.internal.loader.BuiltinClassLoader");
    public static boolean triedLoader = false;
    public static Class java11ClassloaderClazz;


    public static AddFilesToUrlClassLoaderGroovy create1(ClassLoader cl1) throws NoSuchFieldException, IllegalAccessException {
        if (cl1 == null) {
            throw new NullPointerException("classloader is null");
        }
        if (cl1 instanceof URLClassLoader) {
            URLClassLoader urlClassLoader = (URLClassLoader) cl1;
            return new AddFilesToUrlClassLoaderGroovy(urlClassLoader);
        }
        if (!triedLoader) {
            try {
                java11ClassloaderClazz = cl1.loadClass(java11Classloader.className);
                triedLoader = true;
            } catch (ClassNotFoundException e) {
                triedLoader = true;
            }
        }
        if (java11ClassloaderClazz != null) {
            if (java11ClassloaderClazz.isInstance(cl1)) {
                return FetchJava11UrlClasspath.createAdderForJava11ClassloaderNotPublic(cl1);
            }
        }
        throw new IllegalArgumentException("Strange classloader : " + cl1.getClass().getName());
    }


}
