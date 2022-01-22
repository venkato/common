package net.sf.jremoterun.utilities.nonjdk.idea.classpreloader

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide.PreLoadClassesIde;

import java.util.logging.Logger;

@CompileStatic
class PreLoadClassesIdea extends PreLoadClassesIde{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    PreLoadClassesIdea() {
    }

    @Override
    void preLoadClasses(Map<String, Collection<String>> preloadData) throws Exception {
        super.preLoadClasses(preloadData)
        Collection<String> ideaParentClasses = preloadData.get(DumpLoadedClassesIdea.ideaParentClassloaderIdS)
        if(ideaParentClasses!=null){
            preloadPlugin(DumpLoadedClassesIdea.ideaParentClassloaderIdS,ideaParentClasses)
        }

    }

    @Override
    boolean isPreloadPlugin(String pluginId, Collection<String> classes) {
        if(pluginId==DumpLoadedClassesIdea.ideaParentClassloaderIdS){
            return false
        }
        return super.isPreloadPlugin(pluginId,classes)
    }

    @Override
    ClassLoader findClassLoaderForPlugin(String pluginId) {
        return  DumpLoadedClassesIdea.findClassLoaderForPluginGeneric(pluginId)
    }
}
