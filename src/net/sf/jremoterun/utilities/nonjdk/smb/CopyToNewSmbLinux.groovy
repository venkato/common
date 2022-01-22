package net.sf.jremoterun.utilities.nonjdk.smb

import groovy.transform.CompileStatic
import jcifs.smb.SmbFile
import jcifs.smb.SmbFileOutputStream
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.shareduserstart.SharedFolderSettingsEnum
import net.sf.jremoterun.utilities.nonjdk.sshd.JrrStarterFilesToCopy
import net.sf.jremoterun.utilities.nonjdk.sshd.JrrStarterFilesToCopySmb
import org.apache.commons.io.IOUtils

import java.util.logging.Logger

@CompileStatic
class CopyToNewSmbLinux {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public JrrStarterFilesToCopySmb jrrStarterFilesToCopy = new JrrStarterFilesToCopySmb()

    void copyStarter(SmbFile linuxSharedDir) {

        log.info2(linuxSharedDir)
        assert linuxSharedDir.isDirectory()
        assert linuxSharedDir.exists()
        assert linuxSharedDir.toString().endsWith('/')
        SmbFile toDirBase = new SmbFile(linuxSharedDir, SharedFolderSettingsEnum.jrrStarter.childPath)
        log.info "to dir = ${toDirBase}"
        assert toDirBase.exists()
        jrrStarterFilesToCopy.copyFiles(toDirBase)
    }

    void copyIffCompiledJar(SmbFile linuxSharedDir, File iffFrameworkJarOrigin) {
        assert linuxSharedDir.isDirectory()
        SmbFile iffDir = new SmbFile(linuxSharedDir, SharedFolderSettingsEnum.InvocationFramework.childPath)
        iffDir.mkdirs()
        assert iffDir.exists()
        SmbFile destFile = new SmbFile(linuxSharedDir, SharedFolderSettingsEnum.invocationFrameworkJar.childPath)
        SmbFile parentFile = new SmbFile(destFile.getParent())
//        SmbFile parentFile = new SmbFile(destFile.getParent(),destFile.context)
        assert parentFile.exists()
        SmbUtilsJrr.copyFile(iffFrameworkJarOrigin, destFile)
    }

    void createDirs(SmbFile linuxSharedDir) {
        assert linuxSharedDir.isDirectory()
        SmbFile toDirBase = new SmbFile(linuxSharedDir, SharedFolderSettingsEnum.jrrStarter.childPath)
        createDirDeep(toDirBase)
    }

    void createDirDeep(SmbFile f) {
        f.mkdirs()
        assert f.exists()
        assert f.isDirectory()
    }


    void copyToLinuxAll(SmbFile linuxSharedDir, File iffFrameworkJarOrigin) {
        assert linuxSharedDir.isDirectory()
        assert iffFrameworkJarOrigin.isFile()
        SmbFile toDirBase = new SmbFile(linuxSharedDir, SharedFolderSettingsEnum.jrrStarter.childPath)
        createDirDeep toDirBase
        copyStarter(linuxSharedDir)

        SmbFile iffDir = new SmbFile(linuxSharedDir, SharedFolderSettingsEnum.InvocationFramework.childPath)
        createDirDeep(iffDir)
        IfFrameworkSrcDirs.all.each {
            File file1 = it.resolveToFile()
            SmbFile destDir = new SmbFile(iffDir, file1.getName())
            SmbUtilsJrr.copyDirectory(file1, destDir)
        }
        IfFrameworkResourceDirs.all.each {
            File file1 = it.resolveToFile()
            SmbFile destDir = new SmbFile(iffDir, file1.getName())
            SmbUtilsJrr.copyDirectory(file1, destDir)
        }
        copyIffCompiledJar(linuxSharedDir, iffFrameworkJarOrigin)
        SmbFile ivyCacheDir = new SmbFile(linuxSharedDir, SharedFolderSettingsEnum.ivyCacheDir.childPath)
        createDirDeep(ivyCacheDir)
        SmbFile jrrDownloadDir = new SmbFile(linuxSharedDir, SharedFolderSettingsEnum.jrrDownloadDir.childPath)
        createDirDeep(jrrDownloadDir)
    }

}
