package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class BackupLocation {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ToFileRef2 saveStatDir;
    public File latestBackupedFile;
    public String fileSuffix;

    public SimpleDateFormat backupDateFormat = new SimpleDateFormat('yyyy-MM-dd--HH-mm')


    String createFileSuffix() {
        return backupDateFormat.format(new Date()) + fileSuffix
    }

    File createBackupFile() {
        File saveStatDir2 = saveStatDir.resolveToFile()
        assert saveStatDir2!=null
        assert saveStatDir2.exists()
        return new File(saveStatDir2, createFileSuffix())
    }

    void backupFile(File latestFile){
//        String fileName = backupDateFormat.format(new Date()) + '.groovy'
//        File backupFIle = new File(saveStatDir, fileName)
        File backupFIle = createBackupFile()
        FileUtilsJrr.copyFile(latestFile,backupFIle)
        latestBackupedFile = backupFIle
//        return backupFIle
    }

    void createDir(){
        File saveStatDir2 = saveStatDir.resolveToFile()
        assert saveStatDir2!=null
        saveStatDir2.mkdir()
        assert saveStatDir2.isDirectory()
    }

}
