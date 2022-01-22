package net.sf.jremoterun.utilities.nonjdk.classpath.java11;

import jdk.internal.loader.BuiltinClassLoader;
import jdk.internal.loader.URLClassPath;
import net.sf.jremoterun.utilities.JrrClassUtils;
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy;

import java.util.Arrays;

public class FetchJava11UrlClasspath {

    public static URLClassPath fetchUrlClassPath(BuiltinClassLoader  builtinClassLoader) throws NoSuchFieldException, IllegalAccessException {
        return (URLClassPath) JrrClassUtils.getFieldValue(builtinClassLoader, "ucp");
    }


    static AddFilesToUrlClassLoaderGroovy createAdderForJava11ClassloaderNotPublic(ClassLoader urlClassLoader2) throws NoSuchFieldException, IllegalAccessException {
        return createAdderForJava11Classloader((BuiltinClassLoader) urlClassLoader2);
    }

    public static AddFilesToUrlClassLoaderGroovy createAdderForJava11Classloader(BuiltinClassLoader urlClassLoader2) throws NoSuchFieldException, IllegalAccessException {
        jdk.internal.loader.URLClassPath urlClassPath = FetchJava11UrlClasspath.fetchUrlClassPath(urlClassLoader2);
        AddFilesToUrlClassLoaderGroovy aa = new AddFilesToUrlClassLoaderGroovy(new AddClassPathEl(urlClassPath));
        aa.getUrls().addAll(Arrays.asList(urlClassPath.getURLs()));
//            aa.urls.addAll(  Arrays.asList(urlClassPath.getURLs()));
        return aa;
    }

}
