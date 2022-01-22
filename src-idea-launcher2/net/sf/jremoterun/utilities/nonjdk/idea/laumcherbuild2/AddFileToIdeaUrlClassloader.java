package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2;

import groovy.transform.CompileStatic;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;

/**
 * @see org.jetbrains.jps.incremental.groovy.JointCompilationClassLoader
 * @see net.sf.jremoterun.utilities.nonjdk.idea.AddJrrLibToCommonIdeaClassloader2
 */
@CompileStatic
class AddFileToIdeaUrlClassloader {


    /**
     * @see com.intellij.util.lang.UrlClassLoader#addURL
     */
    public static void addFile(ClassLoader cl, File f) throws Exception {
        Method method1 = cl.getClass().getMethod("addURL", URL.class);
        method1.setAccessible(true);
        method1.invoke(cl, f.toURI().toURL());

    }



}
