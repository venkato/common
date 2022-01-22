package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
abstract class ConfigLoaderInstanceWrapperFileAssotiate<K,V> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<File,ConfigLoaderInstanceWrapperFileMapHelper<K,V>> map1 = [:];

    public MapObjectToFile<K> mapObjectToFile;

    ConfigLoaderInstanceWrapperFileAssotiate(MapObjectToFile<K> mapObjectToFile) {
        this.mapObjectToFile = mapObjectToFile
    }

    ConfigLoaderInstanceWrapperFileMapHelper<K,V> load1(K k){
        File file1 = mapToFile(k)
        assert file1!=null
        assert file1.exists()
        ConfigLoaderInstanceWrapperFileMapHelper<K,V> get11 = map1.get(file1)
        if(get11==null){
            get11 = createHelper(file1,k)
            map1.put(file1,get11)
        }
        get11.loadSettings()
        return get11
    }

    abstract ConfigLoaderInstanceWrapperFileMapHelper<K,V> createHelper(File file1,K  k)


    File mapToFile(K k){
        if(mapObjectToFile==null){
            return (File)k
        }
        return mapObjectToFile.findMapping(k).resolveToFile()
    }

}
