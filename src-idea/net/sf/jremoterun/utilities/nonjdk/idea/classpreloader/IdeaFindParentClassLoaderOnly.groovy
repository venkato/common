package net.sf.jremoterun.utilities.nonjdk.idea.classpreloader

import com.intellij.ide.plugins.IdeaPluginDescriptor
import com.intellij.ide.plugins.PluginManagerCore
import com.intellij.ide.plugins.cl.PluginClassLoader
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.util.lang.PathClassLoader
import groovy.transform.CompileStatic
import net.sf.jremoterun.ClassLoaderNotFoundException
import net.sf.jremoterun.FindParentClassLoader
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide.IdePluginsClassloaderInfo

import java.util.logging.Logger

@CompileStatic
class IdeaFindParentClassLoaderOnly implements IdePluginsClassloaderInfo {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String ideaParentClassloaderIdS = PluginManagerCore.SPECIAL_IDEA_PLUGIN_ID.getIdString();

    @Override
    ClassLoader findClassLoader(Serializable classLoaderId) throws ClassLoaderNotFoundException {
        ClassLoader pluginGeneric = findClassLoaderForPluginGeneric((String) classLoaderId, false)
        return pluginGeneric
    }

    @Override
    Collection<String> getPlugins() throws Exception {
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        return plugins.toList().collect { it.getName() } + [IdeaFindParentClassLoaderOnly.ideaParentClassloaderIdS]
    }

    static com.intellij.util.lang.PathClassLoader getIdeaParentClassLoader() {
        return AnAction.getClassLoader() as PathClassLoader
    }

    static ClassLoader findClassLoaderForPluginGeneric(String pluginId, boolean throwExceptionIfNotFound) {
        if (pluginId == ideaParentClassloaderIdS) {
            return getIdeaParentClassLoader()
        }
        return findClassLoaderForPluginS(pluginId, throwExceptionIfNotFound)
    }

    static com.intellij.ide.plugins.cl.PluginClassLoader findClassLoaderForPluginS(String pluginId, boolean throwExceptionIfNotFound) {
        return findClassLoaderForPluginSNoCast(pluginId, throwExceptionIfNotFound) as PluginClassLoader
    }

    static ClassLoader findClassLoaderForPluginSNoCast(String pluginId, boolean throwExceptionIfNotFound) {
        IdeaPluginDescriptor[] plugins = PluginManagerCore.getPlugins();
        List<IdeaPluginDescriptor> pluginsFound = plugins.toList().findAll { it.getName() == pluginId }
        if (pluginsFound == null) {
            if (throwExceptionIfNotFound) {
                throw new Exception("Failed find plugin ${pluginId}")
            } else {
                return null
            }
        }
        if (pluginsFound.size() > 1) {
            throw new Exception("finded many plugin ${pluginsFound.size()} ${pluginId} : ${pluginsFound}")
        }
        return pluginsFound[0].getPluginClassLoader();

    }

}
