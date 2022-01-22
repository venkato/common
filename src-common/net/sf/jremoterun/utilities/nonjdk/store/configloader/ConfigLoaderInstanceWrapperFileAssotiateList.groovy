package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
abstract class ConfigLoaderInstanceWrapperFileAssotiateList<K, V>
        extends ConfigLoaderInstanceWrapperFileAssotiate<K, Collection<V>> {


    ConfigLoaderInstanceWrapperFileAssotiateList() {
        this(null)
    }
    ConfigLoaderInstanceWrapperFileAssotiateList(MapObjectToFile<K> mapObjectToFile) {
        super(mapObjectToFile)
    }

    @Override
    ConfigLoaderInstanceWrapperFileMapHelper<K, Collection<V>> createHelper(File file1, K k) {
        return new ConfigLoaderInstanceWrapperFileMapHelperList(file1,this,k)
    }
}
