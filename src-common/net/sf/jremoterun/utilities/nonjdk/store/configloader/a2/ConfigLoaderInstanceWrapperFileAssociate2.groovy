package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.configloader.MapObjectToFile
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.InstanceCreationMethodsI

import java.util.logging.Logger

@CompileStatic
class ConfigLoaderInstanceWrapperFileAssociate2<K,V> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<File,ConfigLoaderInstanceWrapperFileAssociateHelper2<K,V>> map1 = [:];

    public MapObjectToFile<K> mapObjectToFile;
    public InstanceCreationMethodsI<V> creationMethods


    ConfigLoaderInstanceWrapperFileAssociate2(MapObjectToFile<K> mapObjectToFile, InstanceCreationMethodsI<V> creationMethods) {
        this.mapObjectToFile = mapObjectToFile
        this.creationMethods = creationMethods
        assert mapObjectToFile!=null
        assert creationMethods!=null
    }

    ConfigLoaderInstanceWrapperFileAssociateHelper2<K,V> load1(K k){
        File file1 = mapObjectToFile.findMapping(k).resolveToFile()
        assert file1!=null
        assert file1.exists()
        ConfigLoaderInstanceWrapperFileAssociateHelper2<K,V> get11 = map1.get(file1)
        if(get11==null){
            get11 = createHelper(file1,k)
            map1.put(file1,get11)
        }
        get11.loadSettings()
        return get11
    }

    ConfigLoaderInstanceWrapperFileAssociateHelper2<K,V> createHelper(File file1, K  k){
        return new ConfigLoaderInstanceWrapperFileAssociateHelper2<K, V>(file1,this,k,creationMethods)
    }



}
