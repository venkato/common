package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ConfigLoaderInstanceWrapperFileMapHelperMap<K,V,E> extends ConfigLoaderInstanceWrapperFileMapHelper<K,Map<V,E>> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ConfigLoaderInstanceWrapperFileMapHelperMap(File fileToLoad, ConfigLoaderInstanceWrapperFileAssotiate assotiate, K assitiatedObject) {
        super(fileToLoad, assotiate, assitiatedObject)
    }

    @Override
    Map<V, E> createNew() {
        return [:]
    }

    @Override
    Map<V, E> clone1(Map<V, E> veMap) {
        return new HashMap<V, E>(veMap)
    }
}
