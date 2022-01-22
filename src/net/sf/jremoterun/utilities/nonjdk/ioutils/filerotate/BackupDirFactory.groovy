package net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import org.apache.commons.codec.digest.DigestUtils

import java.util.logging.Logger

@CompileStatic
class BackupDirFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile BackupDirFactory backupDirFactory;

    public File baseBackupDir
    public int maxDepthDefault

    BackupDirFactory(File baseBackupDir, int maxDepthDefault) {
        this.baseBackupDir = baseBackupDir
        this.maxDepthDefault = maxDepthDefault
    }

    Backup4 calcToDir3(File f) {
        return calcToDir2(f, maxDepthDefault)
    }

    File backupSimple(File f) {
        File toFileee = calcToDir3(f, maxDepthDefault)
//        return backup4.backupSimple(f)
        if (f.isFile()) {
            FileUtilsJrr.copyFile(f, toFileee)
        } else if (f.isDirectory()) {
            FileUtilsJrr.copyDirectory(f, toFileee)
        }
        return toFileee
    }

    Backup4 calcToDir2(File f, int maxDepth) {
        Backup4 backup4 = new Backup4(f, calcToDir3(f, maxDepth));
        return backup4
    }

    File calcToDir3(File f, int maxDepth) {
//        assert f.isDirectory()
        File toDirrrer = calcToDirHexAndCreate(f)
        File destFile = new File(toDirrrer, f.getName())
        FileRotate.rotateFile(destFile, maxDepth)
//        rotateFile(f, toDirrrer, maxDepth)
        if (f.isDirectory()) {
            destFile.mkdir()
            assert destFile.exists()
            assert destFile.isDirectory()
        }
        return destFile
    }

    static void rotateFile(File file, File toDirrrer, int maxDepth) {
        if (file.exists()) {
            if (file.isFile() && file.length() == 0) {
                //log.info('skip rotate')
            } else {
                file = file.getAbsoluteFile().getCanonicalFile()
                int depthBegin = 0
                File fileFrom = FileRotate.buildRotateFile(file, toDirrrer, depthBegin, true);
                boolean exists1 = fileFrom.exists()
                //log.info "${exists1} ${fileFrom}, origin=${file}"
                if (exists1) {
                    String syncOnObject = file.getAbsoluteFile().getCanonicalFile().getAbsolutePath().intern()
                    synchronized (syncOnObject) {
                        FileRotate.rotateFileImpl(file, toDirrrer, depthBegin, maxDepth, true, FileRotate.copyIfFailedRenameG, true)
                    }
                }

            }
        }
    }


    File calcToDirHexJust(File f) {
        calcToDirHexJust(convertToUnixPath(f))
    }

    File calcToDirHexJust(String pathUnix) {
        //String pathUnix = f.getAbsoluteFile().getCanonicalFile().getAbsolutePathUnix().toLowerCase()
        String aa = new String(DigestUtils.sha256Hex(pathUnix));
        File uu = new File(baseBackupDir, aa)
        return uu
    }

    String convertToUnixPath(File f ){
        return  f.getAbsoluteFile().getCanonicalFile().getAbsolutePathUnix().toLowerCase()
    }

    File calcToDirHexAndCreate(File f) {
        String pathUnix = convertToUnixPath(f)
        File uu = calcToDirHexJust(pathUnix)
        assert baseBackupDir.isChildFile(uu)
        uu.mkdir()
        assert uu.exists()
        File infotxt = new File(uu, 'info.txt')
        if (infotxt.exists()) {
            assert infotxt.text == pathUnix
        } else {
            infotxt.text = pathUnix
        }
        return uu
    }

    List<File> printAllLocations(File f){
        List<File> result = []
        File just344 = calcToDirHexJust(f)
        if(just344.exists()){
            result.add(just344)
        }
        File parent = f
        while (true) {
            parent = parent.getParentFile()
            if (parent == null || parent.getCanonicalFile().getAbsolutePath() == '/') {
                break;
            }
            File pareentClas = calcToDirHexJust(parent)
            if (pareentClas.exists()) {
                String childPath = parent.getPathToParent(f)
                result.addAll checkExists(pareentClas, childPath)
            }
        }
        return result
    }

    List<File> checkExists(File parent,String child1){
        List<File> dirs = parent.listFiles().toList().findAll { it.isDirectory() }
        return dirs.collect {new File(it,child1)}.findAll {it.exists()}
    }


}
