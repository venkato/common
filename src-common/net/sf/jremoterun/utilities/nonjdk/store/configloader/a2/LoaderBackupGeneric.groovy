package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
interface LoaderBackupGeneric<T> {

    void saveIfNeeded(T newContent)


    boolean isNeedSave(T newContent)

    BackupLocationI getBackupLocation()
    void setBackupLocation(BackupLocationI backupLocationI)

    File getLatestFile()
    void  setLatestFile(File f)
}
