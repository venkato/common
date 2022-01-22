package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic

@CompileStatic
abstract class ConfigLoaderInstanceWrapperFileAssotiateMap<K, V,E>
        extends ConfigLoaderInstanceWrapperFileAssotiate<K, Map<V,E>> {


    ConfigLoaderInstanceWrapperFileAssotiateMap() {
        this(null)
    }
    ConfigLoaderInstanceWrapperFileAssotiateMap(MapObjectToFile<K> mapObjectToFile) {
        super(mapObjectToFile)
    }


    @Override
    ConfigLoaderInstanceWrapperFileMapHelper<K, Map<V,E>> createHelper(File file1, K k) {
        return new ConfigLoaderInstanceWrapperFileMapHelperMap(file1,this,k)
    }


}
