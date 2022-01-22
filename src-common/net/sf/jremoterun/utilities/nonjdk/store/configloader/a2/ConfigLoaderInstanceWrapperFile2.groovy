package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2I
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderGeneric
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.CreatedAtInfoI
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.InstanceCreationMethodsI

import java.util.logging.Logger

@CompileStatic
class ConfigLoaderInstanceWrapperFile2<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public GroovyConfigLoaderGeneric<GroovyConfigLoader2I<T>> configLoader;
    public GroovyConfigLoader2I<T> config1Latest;
    public File fileToLoad
    public T latestValue;
    public String latestDigest;

    public Map<String, T> scriptCache = [:]
    public long maxFileLength = 1_000_000
    public boolean cloneFromCache = false
    public boolean useLastModified = true
    public Date lastCreateNewInstance
    public long lastModified1
    public long lastFileSize
    public InstanceCreationMethodsI<T> creationMethods

    ConfigLoaderInstanceWrapperFile2(File fileToLoad, InstanceCreationMethodsI<T> creationMethodsI3) {
        this(fileToLoad, new GroovyConfigLoaderGeneric<GroovyConfigLoader2I<T>>(),creationMethodsI3)
    }

    ConfigLoaderInstanceWrapperFile2(File fileToLoad, GroovyConfigLoaderGeneric<GroovyConfigLoader2I<T>> configLoader, InstanceCreationMethodsI<T> creationMethodsI2) {
        this.configLoader = configLoader
        this.fileToLoad = fileToLoad
        this.creationMethods = creationMethodsI2
        assert creationMethods!=null
    }

    T loadSettings() {
        long lengthfile = fileToLoad.length()
        if (lengthfile > maxFileLength) {
            throw new IOException("file too big ${fileToLoad.getAbsolutePath()} ${lengthfile}")
        }
        if(useLastModified) {
            if (lengthfile == lastFileSize && lastModified1 == fileToLoad.lastModified()) {
                return onObjectFromCache(latestValue)
            }
        }
        String digest = configLoader.calcDigest(fileToLoad.text)
        T resultFromCache = scriptCache.get(digest)
        if (resultFromCache != null) {
            return onObjectFromCache(resultFromCache)
        }

        return loadNew(digest)
    }

    T onObjectFromCache(T t) {
        if (cloneFromCache) {
            return creationMethods.clone1(t)
        }
        return t;
    }


    T loadNew(String digest) {
        GroovyConfigLoader2I<T> config1 = configLoader.parseConfig(fileToLoad)
        T list1 =  creationMethods.createNew()
        config1.loadConfig(list1)
        scriptCache.put(digest, list1)
        config1Latest = config1
        latestDigest = digest
        latestValue = list1
        lastCreateNewInstance = new Date()
        onNewCreated()
        lastModified1 = fileToLoad.lastModified()
        lastFileSize = fileToLoad.length()
        return list1
    }

    void onNewCreated() {

    }

    Date getFileCreationDate(){
        if(config1Latest==null){
            return null
        }
        CreatedAtInfoI createdAtInfoI = config1Latest as CreatedAtInfoI
        return createdAtInfoI.getCreatedDate().date
    }


}
