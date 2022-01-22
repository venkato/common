package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2I
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderGeneric
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr

import java.util.logging.Logger

@CompileStatic
class ConfigLoaderInstanceMap<K,V> extends ConfigLoaderInstanceWrapperFile<Map<K,V>>{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ConfigLoaderInstanceMap(File fileToLoad) {
        super(fileToLoad)
    }

    ConfigLoaderInstanceMap(File fileToLoad, GroovyConfigLoaderGeneric<GroovyConfigLoader2I<Map<K, V>>> configLoader) {
        super(fileToLoad, configLoader)
    }

    @Override
    Map<K,V> createNew() {
        return [:]
    }

    @Override
    Map<K,V> clone1(Map<K,V> collection) {
        return new HashMap(collection)
    }
}
