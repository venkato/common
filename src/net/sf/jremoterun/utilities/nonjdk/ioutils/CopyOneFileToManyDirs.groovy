package net.sf.jremoterun.utilities.nonjdk.ioutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.IsFileEquals

import java.util.logging.Logger

@CompileStatic
class CopyOneFileToManyDirs {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public boolean convertEol = false;
    public boolean ignoreError = false;
    public boolean ignoreSameDest = true;
    public List<File> destFiles = [];

    public long maxLengthForText = 1_000_000
    public long minFreeSpaceInMg = 10
    public long freeSpaceFactor = 2
    public BackupDirFactory backupDirFactory = net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory.backupDirFactory;

    CopyOneFileToManyDirs() {
    }

    CopyOneFileToManyDirs(boolean ignoreError) {
        this.ignoreError = ignoreError
    }

    CopyOneFileToManyDirs(boolean ignoreError, boolean convertEol) {
        this.convertEol = convertEol
        this.ignoreError = ignoreError
    }

    void addDestIfNotExists(File f) {
        if(!destFiles.contains(f)){
            addDest(f)
        }
    }

    void addDest(File f) {
        assert !destFiles.contains(f)
        destFiles.add(f)
    }

    void addDest(ToFileRef2 f) {
        addDest (f.resolveToFile())
    }

    void doJob(ToFileRef2 fromFile) {
        copyFiles(fromFile, destFiles)
    }

    void doJob(File fromFile) {
        copyFiles(fromFile, destFiles)
    }

    void copyFiles(ToFileRef2 fromFile2, List<File> toFIles) {
        copyFiles(fromFile2.resolveToFile(), toFIles)
    }


    void copyFiles(File fromFile2, List<File> toFIles) {
        if (toFIles.size() == 0) {
            throw new Exception("no files to copy")
        }
        int countErrors = 0
        int countCopied = 0
        int countSkipped = 0
        toFIles.each {
            Boolean aaa=copyOneFile(fromFile2, it)
            if(aaa==null){
                countErrors++
            }else {
                if (aaa) {
                    countCopied++
                } else {
                    countSkipped++
                }
            }
        }
        onReport(fromFile2,toFIles,countCopied,countSkipped,countErrors)
    }

    void onReport(File fromFile2, List<File> toFIles,int countCopied,int countSkipped,int countErrors){
        log.info "${fromFile2} copied=${countCopied} skipped=${countSkipped} errors=${countErrors}"
    }

    Boolean copyOneFile(File fromFile2, File dest) {
        try {
            boolean doCopiing = true
            boolean sameFile2 = fromFile2 == dest
            if (sameFile2) {
                if (ignoreSameDest) {
                    doCopiing = false
                } else {
                    throw new IllegalStateException("Same dest ${fromFile2}")
                }
            }
            if (doCopiing) {
                return copyOneFileWithChecks(fromFile2, dest)
            }
            return false
        } catch (Throwable e) {
            onError(fromFile2, dest, e)
            return null
        }
    }

    void checkFreeSpace(File dest,long minSpace){
        if(!dest.exists()){
            dest = dest.getParentFile()
            assert dest.exists()
        }
        GeneralUtils.checkDiskFreeSpace(dest, minFreeSpaceInMg)
        GeneralUtils.checkDiskFreeSpaceInBytes(dest, minSpace * freeSpaceFactor)
    }

    Boolean copyOneFileWithChecks(File fromFile2, File dest) {
        log.info "checking ${dest}"
        assert fromFile2.exists()
        assert dest.parentFile.exists()
        if (dest.isDirectory()) {
            dest = new File(dest, fromFile2.getName())
        }
        boolean eq = isFileEquals(fromFile2, dest)
        if (eq) {
            return false
        }
        checkFreeSpace(dest,fromFile2.length())
        backup(dest)
        copyOneFileImpl(fromFile2,dest)
        return true
    }

    void copyOneFileImpl(File fromFile2, File dest) {
        log.info "coping ${dest}"
        if (convertEol) {
            assert fromFile2.length() < maxLengthForText
            String text1 = fromFile2.text
            text1 = text1.replace('\r\n', '\n')
            dest.text = text1
        } else {
            FileUtilsJrr.copyFile(fromFile2, dest)
        }
    }


    void backup(File dest){
        if(backupDirFactory!=null) {
            if(dest.exists()) {
                backupDirFactory.backupSimple(dest)
            }
        }
    }



    void onError(File fromFile2, File too, Throwable e) {
        if (ignoreError) {
            log.error3("failed copy ${fromFile2} to ${too}", e)
        } else {
            log.info("failed copy ${fromFile2} to ${too} : ${e}")
            throw e
        }

    }


    boolean isFileEquals(File f1, File f2) {
       return IsFileEquals.isFileEquals.isFileEqualsS(f1,f2)
    }


}
