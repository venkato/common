package net.sf.jremoterun.utilities.nonjdk.backup

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import org.apache.commons.io.FileUtils;

import java.util.logging.Logger;

@CompileStatic
class BackupService {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int rotateCount = 9
    public File todir;

    BackupService(File todir) {
        this.todir = todir
        assert todir.exists()
    }

    File buildBackupPath(BackupElementDescI el) {
        return new File(todir,buildSubPath(el))
    }

    static String buildSubPath(BackupElementDescI el) {
        return "${el.getClass().getSimpleName()}/${el.name()}/${el.getFile().getName()}"
    }

    void backupAll(List<? extends BackupElementDescI> els) {
//        GeneralUtils.checkDiskFreeSpace(todir,minFreeSPaceInMb)
        els.each {
            try {
                log.info "doing backup ${it}"
                backupEl(it)
            } catch (Throwable e) {
                log.info "failed backup ${it} ${e}"
                throw e
            }
        }
    }

    File createDirs(BackupElementDescI el){
        assert el.getFile().exists()
        File dest2 = new File(todir, el.getClass().getSimpleName())
        dest2.mkdir()
        assert dest2.exists()
        File dest3 = new File(dest2, el.name())
        dest3.mkdir()
        assert dest3.exists()
        return dest3
    }

    void backupEl(BackupElementDescI el) {
        File dest3;
        File destF = buildBackupPath(el)
        if(destF.exists()) {
            dest3 = destF.getParentFile()
        }else {
            dest3= createDirs(el)
        }
        GeneralUtils.checkDiskFreeSpaceInBytes(dest3, el.getFile().length()*2)
        backupElImpl(el,destF)
    }

    boolean isNeedCopy(BackupElementDescI el,File destF){
        if (destF.exists()) {
            if (FileUtils.contentEquals(el.getFile(),destF)) {
                log.info "backup skipped as the same : ${el}"
                return  false
            } else {
                FileRotate.rotateFile(destF, rotateCount)
                return  true
            }
        } else {
            return true
        }
    }

    void backupElImpl(BackupElementDescI el,File destF) {
        boolean doCopy = isNeedCopy(el,destF)
        if (doCopy) {
            //destF.bytes = srcBytes
            FileUtilsJrr.copyFile(el.getFile(),destF)
            destF.setLastModified(el.getFile().lastModified())
        }
    }

}
