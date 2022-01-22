package net.sf.jremoterun.utilities.nonjdk.ioutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.Backup4
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.IsFileEquals

import java.util.logging.Logger

@CompileStatic
class CopyManyFilesToOneDir {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public boolean ignoreError = false;
    public boolean ignoreSameDest = true;
//    public File destFile9;
    public List<File> srcFiles = [];
    //public List<File> destFiles = [];

    public long minFreeSpaceInMg = 10
    public long freeSpaceFactor = 2

    public List<File> countErrors = []
    public int countCopied = 0
    public int countSkipped = 0
    public Backup4 backup4
    public BackupDirFactory backupDirFactory = net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory.backupDirFactory;

    CopyManyFilesToOneDir() {
//        destFile9 = destFile
    }

    void addSrcIfNotExists(File f) {
        if(!srcFiles.contains(f)){
            addSrc(f)
        }
    }
    void addSrc(File f) {
        assert !srcFiles.contains(f)
        srcFiles.add(f)
    }

    void addSources(List<? extends ToFileRef2> f) {
        f.each {
            addSrc(it)
        }
    }
    void addSrc(ToFileRef2 f) {
        addSrc (f.resolveToFile())
    }

    void copyFiles(ToFileRef2 destFile1) {
        copyFiles ( destFile1.resolveToFile() )
    }

    void checkForErros(){
        if(ignoreError){
            throw new IllegalStateException("set check for errors")
        }
        if(countErrors.size()>0){
            throw new Exception("failed copy : ${countErrors.collect{it.getName()}}")
        }
    }

    void copyFiles(File destFile1) {
        if (srcFiles.size() == 0) {
            throw new Exception("no files to copy")
        }
        if(srcFiles.size()>1 && destFile1.isFile()){
            throw new Exception("copy many files to one file ${destFile1} , src=${srcFiles}")
        }
        srcFiles.each {
            Boolean aaa = copyOneFile(it, destFile1)
            if (aaa == null) {
                countErrors.add(it)
            } else {
                if (aaa) {
                    countCopied++
                } else {
                    countSkipped++
                }
            }
        }
        onReport(destFile1)
    }

    void onReport(File toFIles) {
        log.info "copied=${countCopied} skipped=${countSkipped} errors=${countErrors.size()}"
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

    void checkFreeSpace(File dest, long minSpace) {
        if (!dest.exists()) {
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
        checkFreeSpace(dest, fromFile2.length())
        if(dest.exists()) {
            backup(dest)
        }
        copyOneFileImpl(fromFile2, dest)
        return true
    }



    void backup(File dest){
        if(backupDirFactory!=null) {
            if(dest.exists()) {
                if(backup4==null) {
                    backup4 = backupDirFactory.calcToDir3(dest.getParentFile())
                }
                backup4.backupFile(dest)
            }
        }
    }

    void copyOneFileImpl(File fromFile2, File dest) {
        log.info "coping ${dest}"
        FileUtilsJrr.copyFile(fromFile2, dest)
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
        return  IsFileEquals.isFileEquals.isFileEqualsS(f1,f2)
    }

    static boolean copyIfDifferentS(File f1, File f2) {
        assert f1.exists()
        boolean needCopy;
        if(f2.exists()) {
            needCopy = IsFileEquals.isFileEquals.isFileEqualsS(f1, f2)
        }else{
            needCopy=true
            File parentFile = f2.getParentFile()
            assert parentFile.exists()
        }
        if(needCopy) {
            FileUtilsJrr.copyFile(f1, f2)
        }
        return needCopy
    }

    @Deprecated
    static boolean isFileEqualsS(File f1, File f2) {
        return IsFileEquals.isFileEquals.isFileEqualsS(f1,f2)
    }


}
