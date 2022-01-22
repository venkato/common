package net.sf.jremoterun.utilities.nonjdk.sshd

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesFirstDownload
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesSrc
import net.sf.jremoterun.utilities.nonjdk.io.DirCopyUtils
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.IsFileEquals

import java.util.logging.Logger

@CompileStatic
class JrrStarterFilesToCopy {
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

    void copyFiles(File toDirBase) {
        File srcDir = GitSomeRefs.starter.resolveToFile()
        copyFilesImpl(srcDir, toDirBase)
        copyCopyDir(srcDir, toDirBase)
    }


    void copyCopyDir(File srcDir, File toDirBase) {
        File destFile = new File(toDirBase, JrrStarterOsSpecificFiles.copyDir.ref.child + '/')
        if (!destFile.exists()) {
            File fileParent = destFile.getParentFile()
            if (!fileParent.exists()) {
                fileParent.mkdir()
            }
            destFile.mkdir()
        }
        File srcFile = new File(srcDir, JrrStarterOsSpecificFiles.originDir.ref.child)
        FileUtilsJrr.copyDirectory(srcFile, destFile)
    }


    void copyFilesImpl(File srcDir, File toDirBase) {
        DirCopyUtils dirCopyUtils = new DirCopyUtils(toDirBase)
        copyFilesImpl2(srcDir, dirCopyUtils)
        dirCopyUtils.printReport()
    }

    void copyFilesImpl2(File srcDir, DirCopyUtils dirCopyUtils) {
        assert srcDir.exists()
        getFilesToAdd().each {
            File destFile = new File(dirCopyUtils.newLoc, it.child)
            File srcFile = new File(srcDir, it.child)
            assert srcFile.exists()
            try {
                if (srcFile.isDirectory()) {
                    //FileUtilsJrr.copyDirectory(srcFile, destFile)

                    dirCopyUtils.copyDirImpl(srcDir,destFile)
                } else {
                    assert srcFile.isFile()
                    boolean eq = IsFileEquals.isFileEquals.isFileEqualsS(srcFile, destFile)
                    if (!eq) {
                        dirCopyUtils.copyOneFile(srcFile,destFile,dirCopyUtils.isConvertEol(srcFile))
//                        FileUtilsJrr.copyFile(srcFile, destFile)
                    }
                }
            } catch (Throwable e) {
                log.info "failed on ${destFile} ${e}"
                throw e
            }
        }

    }

    void copyFilesImplOld(File srcDir, File toDirBase) {
        assert srcDir.exists()
        assert toDirBase.exists()
        getFilesToAdd().each {
            File destFile = new File(toDirBase, it.child)
            File srcFile = new File(srcDir, it.child)
            assert srcFile.exists()
            try {
                if (srcFile.isDirectory()) {
                    FileUtilsJrr.copyDirectory(srcFile, destFile)
                } else {
                    assert srcFile.isFile()
                    boolean eq = IsFileEquals.isFileEquals.isFileEqualsS(srcFile, destFile)
                    if (!eq) {
                        FileUtilsJrr.copyFile(srcFile, destFile)
                    }
                }
            } catch (Throwable e) {
                log.info "failed on ${destFile} ${e}"
                throw e
            }
        }

    }

}
