package net.sf.jremoterun.utilities.nonjdk.idea.classpreloader

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.plugins.cl.PluginClassLoader
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.util.lang.PathClassLoader
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide.DumpLoadedClassesIde;

import java.util.logging.Logger;

@CompileStatic
class DumpLoadedClassesIdea extends DumpLoadedClassesIde{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String ideaParentClassloaderIdS = PluginManagerCore.SPECIAL_IDEA_PLUGIN_ID.getIdString();

    DumpLoadedClassesIdea(File file, long periodInSec) {
        super(file, periodInSec)
    }

    @Override
    Collection<String> getPlugins() {
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        return plugins.toList().collect {it.getName()}+[ideaParentClassloaderIdS]
    }

    @Override
    ClassLoader findClassLoaderForPlugin(String pluginId) {
        return findClassLoaderForPluginGeneric(pluginId)
    }

    static com.intellij.util.lang.PathClassLoader getIdeaParentClassLoader() {
        return AnAction.getClassLoader() as PathClassLoader
    }

    static ClassLoader findClassLoaderForPluginGeneric(String pluginId) {
        if(pluginId == ideaParentClassloaderIdS){
            return getIdeaParentClassLoader()
        }
        return findClassLoaderForPluginS(pluginId)
    }

    static com.intellij.ide.plugins.cl.PluginClassLoader findClassLoaderForPluginS(String pluginId) {
        return findClassLoaderForPluginSNoCast(pluginId)  as PluginClassLoader
    }

    static ClassLoader findClassLoaderForPluginSNoCast(String pluginId) {
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        List<IdeaPluginDescriptor> pluginsFound = plugins.toList().findAll { it.getName() == pluginId }
        if(pluginsFound==null){
            throw new Exception("Failed find plugin ${pluginId}")
        }
        if(pluginsFound.size()>1){
            throw new Exception("finded many plugin ${pluginsFound.size()} ${pluginId} : ${pluginsFound}")
        }
        return pluginsFound[0].getPluginClassLoader();

    }

}
