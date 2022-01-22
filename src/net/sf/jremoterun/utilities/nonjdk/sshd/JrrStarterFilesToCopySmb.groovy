package net.sf.jremoterun.utilities.nonjdk.sshd

import groovy.transform.CompileStatic
import jcifs.smb.SmbFile
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesFirstDownload
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesSrc
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.IsFileEquals
import net.sf.jremoterun.utilities.nonjdk.smb.SmbUtilsJrr

import java.util.logging.Logger

@CompileStatic
class JrrStarterFilesToCopySmb {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<JrrStarterOsSpecificFiles> jrrStarterOsSpecificFilesss = [JrrStarterOsSpecificFiles.originDir,
                                                                          JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef,
    ]

    public List<JrrStarterOsSpecificFilesFirstDownload> firstDownloads = JrrStarterOsSpecificFilesFirstDownload.values().toList();


    public BackupDirFactory backupDirFactory = net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory.backupDirFactory;

    void removeCustom() {
        assert firstDownloads.remove(JrrStarterOsSpecificFilesFirstDownload.gr_custom_dot_sh)
        assert firstDownloads.remove(JrrStarterOsSpecificFilesFirstDownload.gr_custom_dot_bat)
    }


    List<ExactChildPattern> getFilesToAdd() {
        List<ExactChildPattern> r = []
        r.addAll(firstDownloads.collect { it.ref })
        r.addAll(jrrStarterOsSpecificFilesss.collect { it.ref })
        r.add JrrStarterOsSpecificFilesSrc.JrrInit.getSrcPath()
        r.add JrrStarterOsSpecificFilesSrc.custom.getSrcPath()
        return r
    }

    void copyFiles(SmbFile toDirBase) {
        File srcDir = GitSomeRefs.starter.resolveToFile()
        copyFilesImpl(srcDir, toDirBase)
        copyCopyDir(srcDir, toDirBase)
    }

    void copyCopyDir(File srcDir, SmbFile toDirBase) {
        SmbFile destFile = new SmbFile(toDirBase, JrrStarterOsSpecificFiles.copyDir.ref.child + '/')
        createParentDir(destFile)
        File srcFile = new File(srcDir, JrrStarterOsSpecificFiles.originDir.ref.child)
        SmbUtilsJrr.copyDirectory(srcFile, destFile)
    }

    void createDirAndParentDir(SmbFile destFile) {
        if (!destFile.exists()) {
            createParentDir(destFile)
            destFile.mkdir()
            assert destFile.exists()
        }
    }

    void createParentDir(SmbFile destFile) {
//        SmbFile fileParent = new SmbFile(destFile.getParent(), destFile.getContext())
        SmbFile fileParent = new SmbFile(destFile.getParent())
        if (!fileParent.exists()) {
            log.info "creating ${fileParent}"
            fileParent.mkdir()
        }
        assert fileParent.exists()
    }


    void copyEl(ExactChildPattern it, File srcDir, SmbFile toDirBase) {
        SmbFile destFile
        File srcFile = new File(srcDir, it.child)
        assert srcFile.exists()
        try {
            if (srcFile.isDirectory()) {
                destFile = new SmbFile(toDirBase, it.child + '/')
                createDirAndParentDir(destFile)
                SmbUtilsJrr.copyDirectory(srcFile, destFile)
            } else {
                destFile = new SmbFile(toDirBase, it.child)
                assert srcFile.isFile()

                boolean eq = true
                if (!destFile.exists()) {
                    eq = false
                }
                if (eq) {
                    String sha1 = FileCheckSumCalc.calcSha256ForFile(srcFile)
                    String sha2 = SmbUtilsJrr.calcSha256ForFile(destFile)
                    eq = sha1 == sha2
                }
                if (!eq) {
                    createParentDir(destFile)
                    SmbUtilsJrr.copyFile(srcFile, destFile)
                }
            }
        } catch (Throwable e) {
            log.info "failed on ${destFile} ${e}"
            throw e
        }

    }

    void copyFilesImpl(File srcDir, SmbFile toDirBase) {
        assert srcDir.exists()
        assert toDirBase.exists()
        getFilesToAdd().each {
            copyEl(it, srcDir, toDirBase)
        }

    }

}
