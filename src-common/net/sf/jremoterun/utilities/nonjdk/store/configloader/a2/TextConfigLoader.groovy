package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr

import java.text.SimpleDateFormat;
import java.util.logging.Logger;

@CompileStatic
class TextConfigLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File latestFile;

    public SimpleDateFormat backupDateFormat = new SimpleDateFormat('yyyy-MM-dd--HH-mm')

    public String fileSuffix;

    public boolean updateLatest

    TextConfigLoader() {
    }

    TextConfigLoader(File dir, String fileSuffix) {
        assert dir.exists()
        this.latestFile = new File(dir,'latest'+fileSuffix)
        this.fileSuffix = fileSuffix
    }

    void saveIfNeeded(String newContent){
        updateLatest = isNeedSave(newContent)
        if(updateLatest){
            saveContent(newContent)
        }
    }

    void saveContent(String newContent){
        if(latestFile.exists()){
            File backupFile = createBackupFile()
            FileUtilsJrr.copyFile(latestFile, backupFile)
        }
        latestFile.text = newContent
    }

    boolean isNeedSave(String newContent){
        if(!latestFile.exists()){
            return true
        }
        boolean contentSame= latestFile.text == newContent
        return !contentSame
    }

    String createFileSuffix(){
        return backupDateFormat.format(new Date())+fileSuffix
    }

    File createBackupFile(){
        return new File( latestFile.getParentFile(), createFileSuffix())
    }




}
