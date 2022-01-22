package net.sf.jremoterun.utilities.nonjdk.io

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.ioutils.CopyManyFilesToOneDir
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.Backup4
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.IsFileEquals
import net.sf.jremoterun.utilities.nonjdk.linux.EolConversion
import net.sf.jremoterun.utilities.nonjdk.linux.LinuxEolTranslation

import java.util.logging.Logger

@CompileStatic
class DirCopyUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    public File newLoc;
    public boolean ignoreError = false
    public long minFreeSpaceInMg = 10;
    public long freeSpaceFactor
    public boolean ignoreSameDest
    public BackupDirFactory backupDirFactory = net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory.backupDirFactory;


    public List<File> copiedAll = []
    public List<File> skippedAll = []

    public List<File> errorFiles = []


    static CopyManyFilesToOneDir copyLibs(CopyManyFilesToOneDir copyManyFilesToOneDir){
//        CopyManyFilesToOneDir copyManyFilesToOneDir =new CopyManyFilesToOneDir()
        copyManyFilesToOneDir.addSources(JrrStarterJarRefs2.values().toList().collect {it.gitOriginRef()})
        copyManyFilesToOneDir.copyFiles (JrrStarterOsSpecificFiles.copyDir)
    }

    void printReport() {
        log.info "copied=${copiedAll.size()} skipped=${skippedAll.size()} errors=${errorFiles.size()}"
        createReport('copied', copiedAll)
//        createReport('skipped',skippedAll)
        createReport('errors', errorFiles)
    }

    void createReport(String prefix, List<File> fiels) {
        if (fiels.size() > 0) {
            String s = fiels.collect { newLoc.getPathToParent(it) }.sort().join(', ')
            log.info "${prefix} : ${s}"
        }
    }

    DirCopyUtils(File newLoc) {
        this.newLoc = newLoc
        assert newLoc.exists()
        assert newLoc.isDirectory()
    }

    void copyDir(ToFileRef2 ref, String childPath) {
        File dir3 = new File(newLoc, childPath)
        createDirAndCheck dir3
        File fileSrc = ref.resolveToFile()
        copyDirImpl(fileSrc, dir3)
    }

    void copyDirImpl(File ref, File destDir) {
        assert destDir.isDirectory()
        ref.listFiles().toList().each {
            if (it.isFile()) {
                copyOneFile(it, destDir, isConvertEol(it))
            } else if (it.isDirectory()) {
                File destChild = new File(destDir, it.getName())
                createDirAndCheck(destChild)
                copyDirImpl(it, destChild)
            } else {
                throw new IllegalStateException(it.getName())
            }

        }
    }

    EolConversion isConvertEol(File ref){
        if(ref.getName().endsWith('.sh')){
            return EolConversion.linux
        }
        if(ref.getName().endsWith('.bat')){
            return EolConversion.windows
        }
        return EolConversion.asIs
    }

    void createDirAndCheck(File dir1) {
        dir1.mkdirs()
        assert dir1.exists()
        assert dir1.isDirectory()
    }

//    Boolean copyOneFileToDir(ToFileRef2 fromFile2, String dest, boolean convertEol, Object enumRef) {
//        File toDir = new File(newLoc, dest)
//        createDirAndCheck(toDir)
//        File file4 = fromFile2.resolveToFile()
//        if (enumRef == JrrStarterOsSpecificFilesFirstDownload.gr_custom_dot_sh || enumRef == JrrStarterOsSpecificFilesFirstDownload.gr_custom_dot_bat) {
//            if (!file4.exists()) {
//                log.info "ignore copying optional file : ${dest}"
//                skippedAll.add(file4)
//                return false
//            }
//        }
//        return copyOneFile(file4, toDir, convertEol)
//    }

    Boolean copyOneFile(File fromFile2, File dest, EolConversion convertEol) {
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
                return copyOneFileWithChecks(fromFile2, dest, convertEol)
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


    boolean copyOneFileWithChecks(File fromFile2, File dest, EolConversion convertEol) {
//        log.info "checking ${dest}"
        assert fromFile2.exists()
        assert dest.parentFile.exists()
        if (dest.isDirectory()) {
            dest = new File(dest, fromFile2.getName())
        }

        boolean copied = copyOneFileWithChecksImpl(fromFile2, dest, convertEol)
        if (copied) {
            copiedAll.add(dest)
        } else {
            skippedAll.add(dest)
        }
    }

    boolean copyOneFileWithChecksImpl(File fromFile2, File dest, EolConversion convertEol) {
        if (convertEol!=null && convertEol!=EolConversion.asIs) {
            return copyOneFileEol(fromFile2, dest,convertEol)
        }
        boolean eq = IsFileEquals.isFileEquals.isFileEqualsS(fromFile2, dest)
        if (eq) {
            return false
        }
        checkFreeSpace(dest, fromFile2.length())
        if(dest.exists()) {
            backupFile(dest)
        }
        return copyOneFileImpl(fromFile2, dest)
    }

    public int rotateCount = 30
    Backup4 backup4

    void backupFile(File f) {
        if (backupDirFactory != null) {
            if(newLoc.exists()) {
                if (backup4 == null) {
                    backup4 = backupDirFactory.calcToDir2(newLoc, rotateCount)

                }
                backup4.backupFile(f)
            }
        }
    }

    public long maxLengthForText = 1_000_000

//    public boolean translateEolToLinux = true

    String translateEol(String text1, EolConversion conversion){
        return LinuxEolTranslation.translate(text1,conversion);
    }

    boolean copyOneFileEol(File fromFile2, File dest,EolConversion conversion) {
        assert fromFile2.length() < maxLengthForText
        String text1 = fromFile2.text
        text1= translateEol(text1,conversion);
        if (isTextFileSame(text1, dest)) {
            return false
        }
        log.info "coping as text ${dest}"
        checkFreeSpace(dest, fromFile2.length())
        if(dest.exists()) {
            backupFile(dest)
        }
        dest.text = text1
        return true
    }


    boolean copyOneFileImpl(File fromFile2, File dest) {
        log.info "coping ${dest}"
        FileUtilsJrr.copyFile(fromFile2, dest)
        return true
    }

    boolean isTextFileSame(String newText, File f) {
        return f.text == newText
    }


    void onError(File fromFile2, File too, Throwable e) {
        errorFiles.add(too)
        if (ignoreError) {
            log.error3("failed copy ${fromFile2} to ${too}", e)
        } else {
            log.info("failed copy ${fromFile2} to ${too} : ${e}")
            throw e
        }

    }



}
