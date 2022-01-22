package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ConfigLoaderInstanceWrapperFileMapHelperList<K,V> extends ConfigLoaderInstanceWrapperFileMapHelper<K,Collection<V>> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ConfigLoaderInstanceWrapperFileMapHelperList(File fileToLoad, ConfigLoaderInstanceWrapperFileAssotiate assotiate, K assitiatedObject) {
        super(fileToLoad, assotiate, assitiatedObject)
    }

    @Override
    Collection<V> createNew() {
        return []
    }

    @Override
    Collection<V> clone1(Collection<V> vs) {
        return new ArrayList<V>(vs)
    }
}
