package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileCreator
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2ChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.ListStore2
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.MapStore2
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.StoreComplex
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.InstanceCreationMethodsI
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.ListInstanceCreationMethods
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.MapInstanceCreationMethods

import java.util.logging.Logger;

@CompileStatic
class GroovyStoreLoad<T> implements LoaderBackupGeneric<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String latestC = 'latest.groovy'

    public T newObject
    public boolean updateLatest = true
    File latestFile
    public StoreComplex<T> storeComplex
    public ConfigLoaderInstanceWrapperFile2 configLoaderInstanceWrapperFile
    BackupLocationI backupLocation
    public NewValueListener<Throwable> onExceptionNewValue

    GroovyStoreLoad(T newObject, File2ChildLazyRef saveStatDir) {
        assert saveStatDir.parentFile.exists()
        ChildFileCreator childFileCreator = new ChildFileCreator(saveStatDir.parentFile, saveStatDir.child);
        init(newObject, childFileCreator.getCreatedParentDir())
    }

    GroovyStoreLoad(T newObject, File latestFile4) {
        init(newObject, latestFile4)
    }

    void init(T newObject, File latestFile4) {
        assert newObject != null
        this.newObject = newObject
        latestFile = latestFile4
        backupLocation = new BackupLocationDateTime(new FileToFileRef(latestFile4.getParentFile()), '.groovy')
    }

    void defaultListMap() {
        boolean isList1
        if (newObject instanceof Collection) {
            isList1 = true
        } else if (newObject instanceof Map) {
            isList1 = false
        } else {
            if (newObject == null) {
                throw new UnsupportedOperationException("can't detect list or map for null object")
            }
            throw new UnsupportedOperationException("Not list or map : ${newObject.getClass().getName()}")
        }
        defaultListMap2(isList1)
    }

    void defaultListMap2(boolean isList1) {
        InstanceCreationMethodsI creationMethods
        if (isList1) {
            storeComplex = new ListStore2()
            creationMethods = new ListInstanceCreationMethods()
        } else {
            storeComplex = new MapStore2()
            creationMethods = new MapInstanceCreationMethods()
        }
        configLoaderInstanceWrapperFile = new ConfigLoaderInstanceWrapperFile2(latestFile, GroovyConfigLoaderJrr.configLoader, creationMethods)
    }


    void onException(Throwable e) {
        if (onExceptionNewValue == null) {
            log.warn("failed load previous settings", e)
        } else {
            onExceptionNewValue.newValue(e)
        }
    }

    void checkIfNeedISave() {
        updateLatest = isNeedSave(newObject)
    }


    boolean isNeedSave(T newContent) {
        //boolean continueee = true
        T before
        //boolean updateLatest
        try {
            before = configLoaderInstanceWrapperFile.loadSettings()
        } catch (Throwable e) {
            //continueee = false
            //  updateLatest = true
            onException(e)
            return true
        }
        T beforeC = configLoaderInstanceWrapperFile.creationMethods.clone1(before)
        T newC = configLoaderInstanceWrapperFile.creationMethods.clone1(newContent)
        if (beforeC == newC) {
            return false
        }
        return true

    }

    void saveIfNeeded() {
        saveIfNeeded(newObject)
    }

    void saveIfNeeded(T newObject) {
        if (latestFile.exists()) {
            checkIfNeedISave()
        }
        log.info "updating ? ${updateLatest} for ${latestFile}"
        if (updateLatest) {
            String newText = storeComplex.saveComplexObject(newObject)
            if (latestFile.exists()) {
                backupLocation.backupFile(latestFile)
            }
            saveContentImpl(newText)
        }
    }

    void saveContentImpl(String newContent) {
        latestFile.text = newContent
    }


}
