package net.sf.jremoterun.utilities.nonjdk.git.fs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.util.FS_POSIX
import org.eclipse.jgit.util.FS_Win32
import org.eclipse.jgit.util.FS_Win32_Cygwin
import org.eclipse.jgit.util.SystemReader;

import java.util.logging.Logger;

@CompileStatic
class GitFSFactoryJrr extends org.eclipse.jgit.util.FS.FSFactory {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static GitRunCmdCustom gitRunCmdCustom = new GitRunCmdCustom()

    @Override
    FS detect(Boolean cygwinUsed) {
        FxJrrI r = detectJrr(cygwinUsed)
        return r.receiveFS();
    }

    FxJrrI detectJrr(Boolean cygwinUsed) {
        if (SystemReader.getInstance().isWindows()) {
            if (cygwinUsed == null) {
                cygwinUsed = Boolean.valueOf(FS_Win32_Cygwin.isCygwin());
            }
            if (cygwinUsed.booleanValue()) {
                return new FS_Win32_CygwinJrr();
            }
            return new FS_Win32Jrr();
        }
        return new FS_POSIXJrr();
    }


    static void installImpl(org.eclipse.jgit.util.FS.FSFactory f) {
        JrrClassUtils.setFieldValue(org.eclipse.jgit.util.FS, 'factory', f)
    }

    public static boolean inited = false

    void install() {
        if (inited) {
            log.info "already inited"
        } else {
            installImpl2()
        }
    }

    void installImpl2() {
        FS fsDetected = detect(null)
        FxJrrI r = fsDetected as FxJrrI
        JrrClassUtils.setFieldValue(org.eclipse.jgit.util.FS, 'DETECTED', fsDetected)
        installImpl(this)
    }

    static void registerLFS() {
        org.eclipse.jgit.lfs.BuiltinLFS.register()
    }
}
