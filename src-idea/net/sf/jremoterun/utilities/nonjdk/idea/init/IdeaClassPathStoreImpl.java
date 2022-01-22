package net.sf.jremoterun.utilities.nonjdk.idea.init;

import com.intellij.ide.plugins.cl.PluginClassLoader;
import com.intellij.util.lang.ClassPath;
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;

import java.util.logging.Logger;

@CompileStatic
public class IdeaClassPathStoreImpl implements IdeaClassPathStoreI, Runnable {


    //private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public ClassPath ideaClassPath;

    @Override
    public void run() {
        try {
            ideaClassPath = findClassPath1();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void useCache(boolean b) throws Exception {
        JrrClassUtils.setFieldValue(ideaClassPath, "useCache", b);
    }


    @Override
    public void reset() {
        ideaClassPath.reset();
    }

    public static com.intellij.util.lang.ClassPath findClassPath1() throws NoSuchFieldException, IllegalAccessException {
        return findClassPath2(IdeaClasspathAdd.pluginClassLoader) ;
    }

    public static com.intellij.util.lang.ClassPath findClassPath2(PluginClassLoader cl) throws NoSuchFieldException, IllegalAccessException {
        return (ClassPath) JrrClassUtils.getFieldValue(cl, "classPath");
    }

}
