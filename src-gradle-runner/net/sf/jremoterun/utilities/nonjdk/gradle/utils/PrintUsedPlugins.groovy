package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.gradle.api.Plugin
import org.gradle.api.plugins.PluginAware
import org.gradle.api.plugins.PluginContainer;

import java.util.logging.Logger;

@groovy.transform.TupleConstructor
@CompileStatic
class PrintUsedPlugins {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean printClassPath = false

    void printPlugins(PluginAware pluginAware){
        if(pluginAware==null){
            throw new NullPointerException("Project is null")
        }

        PluginContainer plugins = pluginAware.getPlugins()
        log.warning "${pluginAware} used plugins ${plugins.size()}"
        plugins.each {
            printPlugin(it)
        }
    }

    void printPlugin(Plugin plugin){
        if(printClassPath){
            URLClassLoader cl = plugin.getClass().getClassLoader() as URLClassLoader
            log.warning "${plugin.getClass().getName()} ${cl.getURLs()}"
        }else {
            log.warning "${plugin.getClass().getName()}"
        }
    }



}
