package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileCreator
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2ChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class BackupLocationDateTime implements BackupLocationI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ToFileRef2 saveStatDir;
    File latestBackupedFile;
    public String fileSuffix;

    public SimpleDateFormat backupDateFormat = new SimpleDateFormat('yyyy-MM-dd--HH-mm')

    BackupLocationDateTime() {
    }

    BackupLocationDateTime(File saveStatDir, String fileSuffix) {
        this(new FileToFileRef(saveStatDir),fileSuffix)
    }

    BackupLocationDateTime(ToFileRef2 saveStatDir, String fileSuffix) {
        this.saveStatDir = saveStatDir
        this.fileSuffix = fileSuffix
        if (saveStatDir instanceof File2ChildLazyRef) {
            assert saveStatDir.parentFile.exists()
        }
    }

    String createFileSuffix() {
        return backupDateFormat.format(new Date()) + fileSuffix
    }

    File createDir() {
        File saveStatDir2
        if (saveStatDir instanceof File2ChildLazyRef) {
            ChildFileCreator childFileCreator = new ChildFileCreator(saveStatDir.parentFile, saveStatDir.child);
            saveStatDir2 = childFileCreator.getDirCreate()
        } else {
            saveStatDir2 = saveStatDir.resolveToFile()
        }
        assert saveStatDir2 != null
        assert saveStatDir2.exists()
        assert saveStatDir2.isDirectory()
        return saveStatDir2
    }

    File createBackupFile() {
        File saveStatDir2 = createDir()
        return new File(saveStatDir2, createFileSuffix())
    }

    @Override
    void backupFile(File latestFile) {
        File backupFIle = createBackupFile()
        FileUtilsJrr.copyFile(latestFile, backupFIle)
        latestBackupedFile = backupFIle
    }


}
