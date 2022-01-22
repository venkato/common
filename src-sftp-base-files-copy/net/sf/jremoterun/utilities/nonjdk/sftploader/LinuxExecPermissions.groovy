package net.sf.jremoterun.utilities.nonjdk.sftploader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef

import java.nio.file.FileSystem
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission;
import java.util.logging.Logger;

@CompileStatic
class LinuxExecPermissions {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static LinuxExecPermissions linuxExecPermissions=new LinuxExecPermissions()


    public static Boolean isPermissionSupportedCache

    boolean isPermissionSupported(Path pp) {
        if (isPermissionSupportedCache != null) {
            if (!isPermissionSupportedCache) {
                return false
            }
        }
        FileSystem system1 = pp.getFileSystem()
        Class<?> class2
        ClRef clRef1 = new ClRef('sun.nio.fs.UnixFileSystem')
        try {
            class2 = clRef1.loadClass2()
        } catch (ClassNotFoundException e) {
            log.info("failed load ${clRef1}", e)
            isPermissionSupportedCache = false
            return false
        }
        return class2.isInstance(system1)
    }


    void setExecPermissions(File f) {
        if (f.getName().endsWith('.sh')) {
            Path path1 = f.toPath()
            if (isPermissionSupported(path1)) {
                //PosixFileAttributeView view1 = Files.getFileAttributeView(path1, PosixFileAttributeView);
                Set<PosixFilePermission> permissions1 = new HashSet<>(Files.getPosixFilePermissions(path1))
                permissions1.add(PosixFilePermission.OWNER_EXECUTE)
                permissions1.add(PosixFilePermission.GROUP_EXECUTE)
                permissions1.add(PosixFilePermission.OTHERS_EXECUTE)
                Files.setPosixFilePermissions(path1, permissions1)
            }
        }
    }

}
