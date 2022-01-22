package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileCreator
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2ChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef

import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class BackupLocationRotation  implements BackupLocationI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File latestBackupedFile;

    ToFileRef2 saveStatDir;

    public int rotateFileCount

    BackupLocationRotation(File  saveStatDir, int rotateFileCount) {
        this(new FileToFileRef(saveStatDir),rotateFileCount)
    }

    BackupLocationRotation(ToFileRef2 saveStatDir, int rotateFileCount) {
        this.saveStatDir = saveStatDir
        this.rotateFileCount = rotateFileCount
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
        return saveStatDir2
    }

    @Override
    void backupFile(File latestFile) {
        File backupDir1 = createDir()
        latestBackupedFile =  FileRotate.fileRotateInstance.rotateFileNS(latestFile,backupDir1,rotateFileCount)
    }


}
