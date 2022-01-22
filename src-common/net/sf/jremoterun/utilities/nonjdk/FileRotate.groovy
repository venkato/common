package net.sf.jremoterun.utilities.nonjdk;

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import org.apache.commons.io.FileUtils

import java.text.DecimalFormat
import java.util.logging.Logger

@CompileStatic
class FileRotate {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static FileRotate fileRotateInstance = new FileRotate();
    public static boolean copyIfFailedRenameG = true;
    public boolean doSync = true;
    public DecimalFormat decimalFormat1 = new DecimalFormat('00')

    static void rotateFile(File file, File archiveRotationDir, int maxDepth) {
        fileRotateInstance.rotateFileNS(file, archiveRotationDir, maxDepth)
    }

    File rotateFileNS(File file, File archiveRotationDir, int maxDepth) {
        if (file.exists()) {
            file = file.getAbsoluteFile().getCanonicalFile()
            return rotateFileImplSyncNS(file, archiveRotationDir, 0, maxDepth, true, copyIfFailedRenameG)
        }
    }

    static void rotateFile(File file, int maxDepth) {
        fileRotateInstance.rotateFileNS(file, maxDepth)
    }

    void rotateFileNS(File file, int maxDepth) {
        if (file.exists()) {
            if (file.isFile() && file.length() == 0) {

            } else {
                file = file.getAbsoluteFile().getCanonicalFile()
                rotateFileImplNS(file, 0, maxDepth)
            }
        }
    }

    static void rotateFileImpl(File file, int depth, int maxDepth) {
        fileRotateInstance.rotateFileImplNS(file, depth, maxDepth)
    }

    void rotateFileImplNS(File file, int depth, int maxDepth) {
        rotateFileImplSyncNS(file, file.getParentFile(), depth, maxDepth, true, copyIfFailedRenameG);
    }


    static void rotateFileImplSync(File file, File archiveRotationDir, int depth, int maxDepth, boolean doRenameOrCopy, boolean copyIfFailedRename) {
        fileRotateInstance.rotateFileImplSyncNS(file, archiveRotationDir, depth, maxDepth, doRenameOrCopy, copyIfFailedRename)
    }

    File rotateFileImplSyncNS(File file, File archiveRotationDir, int depth, int maxDepth, boolean doRenameOrCopy, boolean copyIfFailedRename) {
        if (doSync) {
            String syncOnObject = file.getAbsoluteFile().getCanonicalFile().getAbsolutePath().intern()
            synchronized (syncOnObject) {
                return rotateFileImplNS(file, archiveRotationDir, depth, maxDepth, doRenameOrCopy, copyIfFailedRename, false)
            }
        } else {
            return rotateFileImplNS(file, archiveRotationDir, depth, maxDepth, doRenameOrCopy, copyIfFailedRename, false)
        }
    }


    static File buildRotateFile(File file, File archiveRotationDir, int depth, boolean newLogic) {
        fileRotateInstance.buildRotateFileNS(file, archiveRotationDir, depth, newLogic)
    }

    File buildRotateFileNS(File file, File archiveRotationDir, int depth, boolean newLogic) {
        if (depth == 0) {
            if (newLogic) {
                return new File(archiveRotationDir, file.getName())
            }
            return file;
        }
        String dirSuffix;
        synchronized (decimalFormat1) {
            dirSuffix = "${file.getName()}.${decimalFormat1.format(depth)}"
        }
        return new File(archiveRotationDir, dirSuffix)
    }

    static void rotateFileImpl(File file, File archiveRotationDir, int depth, int maxDepth, boolean doRenameOrCopy, boolean copyIfFailedRename, boolean newLogic) {
        fileRotateInstance.rotateFileImplNS(file, archiveRotationDir, depth, maxDepth, doRenameOrCopy, copyIfFailedRename, newLogic)
    }

    File rotateFileImplNS(File file, File archiveRotationDir, int depth, int maxDepth, boolean doRenameOrCopy, boolean copyIfFailedRename, boolean newLogic) {
        int newDepth = depth + 1
        File fileFrom = buildRotateFileNS(file, archiveRotationDir, depth, newLogic);
        File file2 = buildRotateFileNS(file, archiveRotationDir, newDepth, newLogic)
        if (file2.exists()) {
            if (newDepth > maxDepth) {
                if (file2.isDirectory()) {
                    FileUtils.deleteDirectory(file2)
                } else {
                    assert file2.delete()
                }
                assert !file2.exists()
            } else {
                rotateFileImplNS(file, archiveRotationDir, newDepth, maxDepth, true, copyIfFailedRename, newLogic);
                assert !file2.exists()
            }
        }
        assert !file2.exists()
        if (doRenameOrCopy) {
            boolean renameDone = fileFrom.renameTo(file2)
            if (!renameDone) {
                if (copyIfFailedRename) {
                    log.info "Failed rename ${fileFrom} to ${file2}, do coping .."
                    if (fileFrom.isDirectory()) {
                        FileUtilsJrr.copyDirectory(fileFrom, file2);
                        FileUtils.deleteQuietly(fileFrom)
                    } else {
                        JrrUtilitiesFile.checkFileExist(fileFrom)
                        FileUtilsJrr.copyFile(fileFrom, file2);
                        fileFrom.delete()
                    }
//                    if(fileFrom.exists()){
//                        throw new Exception("Failed delete file : ${fileFrom}")
//                    }
                } else {
                    throw new IOException("Failed rename ${fileFrom} to ${file2}")
                }
            }
        } else {
            FileUtilsJrr.copyFile(fileFrom, file2)
        }
        return file2
    }


}
