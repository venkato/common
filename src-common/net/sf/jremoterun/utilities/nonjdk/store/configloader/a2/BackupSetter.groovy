package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class BackupSetter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public LoaderBackupGeneric backupGeneric;

    BackupSetter(LoaderBackupGeneric backupGeneric) {
        this.backupGeneric = backupGeneric
    }


    void setRotate(int count) {
        backupGeneric.backupLocation = new BackupLocationRotation(backupGeneric.latestFile.getParentFile(), count)
    }

    void setDateTime() {
        setDateTime2(backupGeneric.latestFile.getName())
    }

    void setDateTime2(String suffix) {
        backupGeneric.backupLocation = new BackupLocationDateTime(backupGeneric.latestFile.getParentFile(), suffix)
    }
}
