package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileCreator
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2ChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef

import java.util.logging.Logger

@CompileStatic
class DataConfigLoader implements LoaderBackupGeneric<byte[]> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File latestFile;

    public String fileSuffix;

    public boolean updateLatest
    BackupLocationI backupLocation


    DataConfigLoader(File f,int rotateCount) {
        latestFile = f
        backupLocation= new BackupLocationRotation( new FileToFileRef(f.getParentFile()),rotateCount)
    }

    DataConfigLoader(File2ChildLazyRef saveStatDir, String fileSuffix) {
        assert saveStatDir.parentFile.exists()
        ChildFileCreator childFileCreator = new ChildFileCreator(saveStatDir.parentFile, saveStatDir.child);
        init(childFileCreator.getDirCreate(), fileSuffix)
    }

    DataConfigLoader(File dir, String fileSuffix) {
        init(dir, fileSuffix)
    }

    void init(File dir, String fileSuffix) {
        init2(dir,fileSuffix,'latest' )
    }

    void init2(File dir, String fileSuffix,String filePrefix) {
        assert dir.exists()
        this.latestFile = new File(dir,filePrefix + fileSuffix)
        this.fileSuffix = fileSuffix
        backupLocation= new BackupLocationDateTime( new FileToFileRef(dir),fileSuffix)
    }

    void saveIfNeeded(byte[] newContent) {
        updateLatest = isNeedSave(newContent)
        log.info "updating ? ${updateLatest} for ${latestFile}"
        if (updateLatest) {
            saveContent(newContent)
        }
    }

    void saveContentImpl(byte[] newContent) {
        latestFile.bytes = newContent
    }

    void saveContent(byte[] newContent) {
        if (latestFile.exists()) {
            backupLocation.backupFile(latestFile)
        }
        saveContentImpl(newContent)
    }



    boolean isNeedSave(byte[] newContent) {
        if (!latestFile.exists()) {
            return true
        }
        boolean contentSame = latestFile.bytes == newContent
        return !contentSame
    }


}
