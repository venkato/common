package net.sf.jremoterun.utilities.nonjdk.store.configloader.a2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileCreator
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2ChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.IsFileEquals

import java.util.logging.Logger;

@CompileStatic
class TextConfigLoader {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File latestFile;

    public String fileSuffix;

    public boolean updateLatest
    public BackupLocationI backupLocation


    TextConfigLoader() {
    }

    TextConfigLoader(File2ChildLazyRef saveStatDir, String fileSuffix) {
        assert saveStatDir.parentFile.exists()
        ChildFileCreator childFileCreator = new ChildFileCreator(saveStatDir.parentFile, saveStatDir.child);
        init(childFileCreator.getDirCreate(), fileSuffix)
    }

    TextConfigLoader(File dir, String fileSuffix) {
        init(dir, fileSuffix)
    }

    void init(File dir, String fileSuffix) {
        assert dir.exists()
        this.latestFile = new File(dir, 'latest' + fileSuffix)
        this.fileSuffix = fileSuffix
        backupLocation= new BackupLocationDateTime( new FileToFileRef(dir),fileSuffix)
//        backupLocation.createDir()
        //backupLocation.fileSuffix = fileSuffix
    }

    void saveIfNeeded(String newContent) {
        updateLatest = isNeedSave(newContent)
        log.info "updating ? ${updateLatest} for ${latestFile}"
        if (updateLatest) {
            saveContent(newContent)
        }
    }

    void saveContentImpl(String newContent) {
        latestFile.text = newContent
    }

    void saveContent(String newContent) {
        if (latestFile.exists()) {
            backupLocation.backupFile(latestFile)
        }
        saveContentImpl(newContent)
    }



    boolean isNeedSave(String newContent) {
        if (!latestFile.exists()) {
            return true
        }
        boolean contentSame = latestFile.text == newContent
        return !contentSame
    }


}
