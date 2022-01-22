package net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.GeneralUtils
import net.sf.jremoterun.utilities.nonjdk.TwoResult
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
import net.sf.jremoterun.utilities.nonjdk.ioutils.CopyManyFilesToOneDir
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.Backup4
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDir
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory;

import java.util.logging.Logger;

@CompileStatic
class JrrStartCopyUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static List<FileChildLazyRef> javaAgents = [JrrStarterJarRefs2.java11base.gitOriginRef(),
                                                       JrrStarterJarRefs2.jremoterun.gitOriginRef(),
    ]

    public static List<FileChildLazyRef> copyToEclipse = [JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef.calcGitRef(),
                                                          JrrStarterJarRefs2.java11base.gitOriginRef(),
                                                          JrrStarterJarRefs2.jremoterun.gitOriginRef(),
                                                          JrrStarterJarRefs2.jrrassist.gitOriginRef(),
    ]


//    public static List<FileChildLazyRef> copyJrrStarter = [JrrStarterOsSpecificFilesSrc.JrrStarter,]

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

    JrrStartCopyUtils(File newLoc) {
        this.newLoc = newLoc
        assert newLoc.exists()
        assert newLoc.isDirectory()
    }

//    List<TwoResult<ToFileRef2,String>> copyDirList = []
//
//    void addDefault(){
//        JrrStarterOsSpecificFilesSrc.values().toList().each {
//            copyDirList.add(new TwoResult<ToFileRef2, String>(it.calcGitRefSrc(),it.getRef().child))
//        }
//        copyDirList.add(new TwoResult<ToFileRef2, String>(JrrStarterOsSpecificFiles.log4j2_config, JrrStarterOsSpecificFiles.log4j2_config.ref.child)
//    }

    void copyStarterToNewLocation() {
        JrrStarterOsSpecificFilesSrc.values().toList().each {
            copyDir(it.calcGitRefSrc(), it.getSrcPath().child)
        }
        JrrStarterJarRefs2.values().toList().each {
            copyOneFileToDir(it.gitOriginRef(), JrrStarterOsSpecificFiles.originDir.ref.child, false, it)
            copyOneFileToDir(it.gitOriginRef(), JrrStarterOsSpecificFiles.copyDir.ref.child, false, it)
        }
        copyOneFileToDir(JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef, JrrStarterOsSpecificFiles.onejar.ref.child, false, JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef);
        JrrStarterOsSpecificFilesFirstDownload.values().toList().each {
            copyOneFileToDir(it, JrrStarterOsSpecificFilesSrc.firstdownload.ref.child, it.isNeedConvertToUnixEOL(), it)
        }
        copyDir(JrrStarterOsSpecificFiles.log4j2_config, JrrStarterOsSpecificFiles.log4j2_config.ref.child)
    }

    void copyDir(ToFileRef2 ref, String childPath) {
        File dir3 = new File(newLoc, childPath)
        createDirAndCheck dir3
        File fileSrc = ref.resolveToFile()

        //FileUtilsJrr.copyDirectory(fileSrc, dir3)
        copyDirImpl(fileSrc, dir3)
    }

    void copyDirImpl(File ref, File destDir) {
        assert destDir.isDirectory()
        ref.listFiles().toList().each {
            if (it.isFile()) {
                copyOneFile(it, destDir, false)
            } else if (it.isDirectory()) {
                File destChild = new File(destDir, it.getName())
                createDirAndCheck(destChild)
                copyDirImpl(it, destChild)
            } else {
                throw new IllegalStateException(it.getName())
            }

        }
    }

    void createDirAndCheck(File dir1) {
        dir1.mkdirs()
        assert dir1.exists()
        assert dir1.isDirectory()
    }

    Boolean copyOneFileToDir(ToFileRef2 fromFile2, String dest, boolean convertEol, Object enumRef) {
        File toDir = new File(newLoc, dest)
        createDirAndCheck(toDir)
        File file4 = fromFile2.resolveToFile()
        if (enumRef == JrrStarterOsSpecificFilesFirstDownload.gr_custom_dot_sh || enumRef == JrrStarterOsSpecificFilesFirstDownload.gr_custom_dot_bat) {
            if (!file4.exists()) {
                log.info "ignore copying optional file : ${dest}"
                skippedAll.add(file4)
                return false
            }
        }
        return copyOneFile(file4, toDir, convertEol)
    }

    Boolean copyOneFile(File fromFile2, File dest, boolean convertEol) {
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


    boolean copyOneFileWithChecks(File fromFile2, File dest, boolean convertEol) {
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

    boolean copyOneFileWithChecksImpl(File fromFile2, File dest, boolean convertEol) {
        if (convertEol) {
            return copyOneFileEol(fromFile2, dest)
        }
        boolean eq = isFileEquals(fromFile2, dest)
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

    boolean copyOneFileEol(File fromFile2, File dest) {
        assert fromFile2.length() < maxLengthForText
        String text1 = fromFile2.text
        text1 = text1.replace('\r\n', '\n')
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


    boolean isFileEquals(File f1, File f2) {
        if (!f2.exists()) {
            return false
        }
        assert f1.exists()
        if (f1.length() != f2.length()) {
            return false
        }
        String sha1 = FileCheckSumCalc.calcSha256ForFile(f1)
        String sha2 = FileCheckSumCalc.calcSha256ForFile(f2)
        return sha1 == sha2
    }


}
