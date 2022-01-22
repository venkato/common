package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.ListStore2
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.MapStore2
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.StoreComplex
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.InstanceCreationMethodsI
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.ListInstanceCreationMethods
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.MapInstanceCreationMethods

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class GroovyStoreLoad<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String latestC = 'latest.groovy'

//    public File saveStatDir;
    public T newObject
    public boolean updateLatest = true
    public File latestFile
    public StoreComplex<T> storeComplex
    public ConfigLoaderInstanceWrapperFile2 configLoaderInstanceWrapperFile
    public BackupLocation backupLocation = new BackupLocation()
//    public SimpleDateFormat backupDateFormat = new SimpleDateFormat('yyyy-MM-dd--HH-mm')


    GroovyStoreLoad(T newObject, File latestFile4) {
        assert newObject != null
        this.newObject = newObject
        latestFile = latestFile4
        backupLocation.fileSuffix = '.groovy'
        backupLocation.saveStatDir = new FileToFileRef( latestFile4.getParentFile());
//        backupLocation.createDir()
    }

//    @Deprecated
//    GroovyStoreLoad(T newObject, File saveStatDir) {
//        this(newObject,new File(saveStatDir,latestC),true)
//    }

    void defaultListMap() {
        InstanceCreationMethodsI creationMethods
        if (newObject instanceof Collection) {
            storeComplex = new ListStore2()
            creationMethods = new ListInstanceCreationMethods()
        } else if (newObject instanceof Map) {
            storeComplex = new MapStore2()
            creationMethods = new MapInstanceCreationMethods()
        } else {
            throw new UnsupportedOperationException("Not list or map : ${newObject.getClass().getName()}")
        }
        configLoaderInstanceWrapperFile = new ConfigLoaderInstanceWrapperFile2(latestFile, GroovyConfigLoaderJrr.configLoader, creationMethods)
    }

    NewValueListener<Throwable> onExceptionNewValue

    void onException(Throwable e){
        if(onExceptionNewValue==null){
            log.warn("failed load previous settings",e)
        }else {
            onExceptionNewValue.newValue(e)
        }
    }

    void checkIfNeedISave() {
        boolean continueee= true
        T before
        try {
            before = configLoaderInstanceWrapperFile.loadSettings()
        }catch (Throwable e){
            continueee = false
            updateLatest = true
            onException(e)
        }
        if(continueee) {
            T beforeC = configLoaderInstanceWrapperFile.creationMethods.clone1(before)
            T newC = configLoaderInstanceWrapperFile.creationMethods.clone1(newObject)
            if (beforeC == newC) {
                updateLatest = false
            }
        }
    }


    void saveIfNeeded() {
        if (latestFile.exists()) {
            checkIfNeedISave()
        }
        log.info "updating ? ${updateLatest} for ${latestFile}"
        if (updateLatest) {
            if (latestFile.exists()) {
                backupLocation.backupFile(latestFile)
            }
            latestFile.text = storeComplex.saveComplexObject(newObject)
        }
    }


//    File backupFile(){
//        String fileName = backupDateFormat.format(new Date()) + '.groovy'
//        File backupFIle = new File(saveStatDir, fileName)
//        FileUtilsJrr.copyFile(latestFile,backupFIle)
//        return backupFIle
//    }
}
