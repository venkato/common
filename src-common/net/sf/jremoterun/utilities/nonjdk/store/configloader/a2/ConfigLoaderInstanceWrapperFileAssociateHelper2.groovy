package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.InstanceCreationMethodsI

import java.util.logging.Logger

@CompileStatic
 class ConfigLoaderInstanceWrapperFileAssociateHelper2<K,V> extends ConfigLoaderInstanceWrapperFile2<V> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ConfigLoaderInstanceWrapperFileAssociate2 assotiate;
    public K assitiatedObject

    ConfigLoaderInstanceWrapperFileAssociateHelper2(File fileToLoad, ConfigLoaderInstanceWrapperFileAssociate2 assotiate, K assitiatedObject, InstanceCreationMethodsI<V> creationMethodsI) {
        super(fileToLoad,creationMethodsI)
        this.assotiate = assotiate
        this.assitiatedObject = assitiatedObject
    }

}
