package net.sf.jremoterun.utilities.nonjdk.store.configloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2I
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderGeneric
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr

import java.util.logging.Logger

@CompileStatic
abstract class ConfigLoaderInstanceWrapperFile<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public GroovyConfigLoaderGeneric<GroovyConfigLoader2I<T>> configLoader;
    public GroovyConfigLoader2I<T> config1Latest;
    public File fileToLoad
    public T latestValue;
    public String latestDigest;

    public Map<String,T> scriptCache = [:]
    public long maxFileLength = 1_000_000

    ConfigLoaderInstanceWrapperFile(File fileToLoad ) {
        this(fileToLoad,new GroovyConfigLoaderGeneric<GroovyConfigLoader2I<T>>())
    }

    ConfigLoaderInstanceWrapperFile(File fileToLoad ,GroovyConfigLoaderGeneric<GroovyConfigLoader2I<T>> configLoader) {
        this.configLoader = configLoader
        this.fileToLoad = fileToLoad
    }


    abstract T createNew()
    abstract T clone1(T t)

    T loadSettings(){
        if(fileToLoad.length()>maxFileLength){
            throw new IOException("file too big ${fileToLoad.getAbsolutePath()} ${fileToLoad.length()}")
        }
        String digest= configLoader.calcDigest(fileToLoad.text)
        T resultFromCache = scriptCache.get(digest)
        if(resultFromCache!=null){
            return clone1(resultFromCache)
        }

        return loadNew(digest)
    }


    T loadNew(String digest){
        GroovyConfigLoader2I<T> config1 = configLoader.parseConfig(fileToLoad)
        T list= createNew()
        config1.loadConfig(list)
        scriptCache.put(digest,list)
        config1Latest = config1
        latestDigest = digest
        latestValue = list
        onNewCreated()
        return list
    }

    void onNewCreated(){

    }




}
