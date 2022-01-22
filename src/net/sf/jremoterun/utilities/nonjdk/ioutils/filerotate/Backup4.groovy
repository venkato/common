package net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import org.apache.commons.codec.digest.DigestUtils

import java.util.logging.Logger

@CompileStatic
class Backup4 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File baseOrigin;
    public File toDir;

    Backup4(File baseOrigin, File toDir) {
        this.baseOrigin = baseOrigin
        this.toDir = toDir
    }

    File backupFile(File f) {
        if(!f.exists()){
            throw new FileNotFoundException(f.getAbsolutePath());
        }
        String parent1 = baseOrigin.getPathToParent(f)
        File toFileee = new File(toDir, parent1)
        if (f.isFile()) {
            FileUtilsJrr.copyFile(f, toFileee)
        } else if (f.isDirectory()) {
            FileUtilsJrr.copyDirectory(f, toFileee)
        } else {
            throw new IllegalStateException("starange file ${f}")
        }
        return toFileee
    }

    File backupSimple(File f){
        File toFileee = new File(toDir, f.getName())
        if (f.isFile()) {
            FileUtilsJrr.copyFile(f, toFileee)
        } else if (f.isDirectory()) {
            FileUtilsJrr.copyDirectory(f, toFileee)
        } else {
            throw new IllegalStateException("starange file ${f}")
        }

        return toFileee;
    }


}
