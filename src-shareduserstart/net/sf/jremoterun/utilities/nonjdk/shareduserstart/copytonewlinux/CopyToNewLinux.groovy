package net.sf.jremoterun.utilities.nonjdk.shareduserstart.copytonewlinux

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.shareduserstart.SharedFolderSettingsEnum
import net.sf.jremoterun.utilities.nonjdk.sshd.JrrStarterFilesToCopy;

import java.util.logging.Logger;

@CompileStatic
class CopyToNewLinux {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public JrrStarterFilesToCopy jrrStarterFilesToCopy = new JrrStarterFilesToCopy()

    void copyStarter(File linuxSharedDir){
        assert linuxSharedDir.isDirectory()
        File toDirBase = new File(linuxSharedDir, SharedFolderSettingsEnum.jrrStarter.childPath)
        assert toDirBase.exists()
        jrrStarterFilesToCopy.copyFiles(toDirBase)
    }

    void copyIffCompiledJar(File linuxSharedDir, File iffFrameworkJarOrigin){
        File iffDir = new File(linuxSharedDir, SharedFolderSettingsEnum.InvocationFramework.childPath)
        iffDir.mkdirs()
        assert iffDir.exists()
        File destFile=new File(linuxSharedDir, SharedFolderSettingsEnum.invocationFrameworkJar.childPath)
        File parentFile = destFile.getParentFile()
        assert parentFile.exists()
        FileUtilsJrr.copyFile(iffFrameworkJarOrigin, destFile)
    }

    void createDirs(File linuxSharedDir){
        assert linuxSharedDir.isDirectory()
        File toDirBase = new File(linuxSharedDir, SharedFolderSettingsEnum.jrrStarter.childPath)
        createDirDeep(toDirBase)
    }

    void createDirDeep(File f){
        f.mkdirs()
        assert f.exists()
        assert f.isDirectory()
    }


    void copyToLinuxAll(File linuxSharedDir, File iffFrameworkJarOrigin) {
        assert linuxSharedDir.isDirectory()
        assert iffFrameworkJarOrigin.isFile()
        File toDirBase = new File(linuxSharedDir, SharedFolderSettingsEnum.jrrStarter.childPath)
        createDirDeep toDirBase
        copyStarter(linuxSharedDir)

        File iffDir = new File(linuxSharedDir, SharedFolderSettingsEnum.InvocationFramework.childPath)
        createDirDeep(iffDir)
        IfFrameworkSrcDirs.all.each {
            File file1 = it.resolveToFile()
            File destDir = new File(iffDir, file1.getName())
            FileUtilsJrr.copyDirectory(file1, destDir)
        }
        IfFrameworkResourceDirs.all.each {
            File file1 = it.resolveToFile()
            File destDir = new File(iffDir, file1.getName())
            FileUtilsJrr.copyDirectory(file1, destDir)
        }
        copyIffCompiledJar(linuxSharedDir,iffFrameworkJarOrigin)
        File ivyCacheDir = new File(linuxSharedDir, SharedFolderSettingsEnum.ivyCacheDir.childPath)
        createDirDeep(ivyCacheDir)
        File jrrDownloadDir = new File(linuxSharedDir, SharedFolderSettingsEnum.jrrDownloadDir.childPath)
        createDirDeep(jrrDownloadDir)
    }


}
