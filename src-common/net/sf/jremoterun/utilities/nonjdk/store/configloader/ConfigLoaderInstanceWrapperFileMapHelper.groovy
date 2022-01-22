package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
abstract class ConfigLoaderInstanceWrapperFileMapHelper<K,V> extends ConfigLoaderInstanceWrapperFile<V> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ConfigLoaderInstanceWrapperFileAssotiate assotiate;
    public K assitiatedObject

    ConfigLoaderInstanceWrapperFileMapHelper(File fileToLoad, ConfigLoaderInstanceWrapperFileAssotiate assotiate, K assitiatedObject) {
        super(fileToLoad)
        this.assotiate = assotiate
        this.assitiatedObject = assitiatedObject
    }

}
