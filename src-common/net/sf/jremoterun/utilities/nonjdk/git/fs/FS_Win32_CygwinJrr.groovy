package net.sf.jremoterun.utilities.nonjdk.git.fs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.api.errors.JGitInternalException
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.util.FS_Win32_Cygwin
import org.eclipse.jgit.util.ProcessResult;

import java.util.logging.Logger;

@CompileStatic
class FS_Win32_CygwinJrr extends FS_Win32_Cygwin implements FxJrrI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    GitRunCmdCustom gitRunCmdCustom = net.sf.jremoterun.utilities.nonjdk.git.fs.GitFSFactoryJrr.gitRunCmdCustom
    @Override
    protected File discoverGitExe() {
        File f = GitFSFactoryJrr.gitRunCmdCustom.discoverGitExe()
        if (f != null) {
            return f
        }
        File fr = super.discoverGitExe()
        return GitFSFactoryJrr.gitRunCmdCustom.discoveredGitExe(fr)
    }

    @Override
    protected File discoverGitSystemConfig() {
        File f = GitFSFactoryJrr.gitRunCmdCustom.discoverGitSystemConfig()
        if (f != null) {
            return f
        }
        File fr = super.discoverGitSystemConfig()
        return GitFSFactoryJrr.gitRunCmdCustom.discoveredGitSystemConfig(fr)
    }

    @Override
    File getGitSystemConfig() {
        File f = GitFSFactoryJrr.gitRunCmdCustom.getGitSystemConfig()
        if (f != null) {
            return f
        }
        File fr = super.getGitSystemConfig()
        return GitFSFactoryJrr.gitRunCmdCustom.gitSystemConfigUses(fr)
    }

    @Override
    ProcessBuilder runInShellSuper(String cmd, String[] args) {
        return super.runInShell(cmd, args)
    }

    @Override
    ProcessBuilder runInShell(String cmd, String[] args) {
        ProcessBuilder pb = GitFSFactoryJrr.gitRunCmdCustom.runInShell(cmd, args, this)
        if (pb != null) {
            return pb
        }
        return super.runInShell(cmd, args)
    }

    @Override
    ExecutionResult executeSuper(ProcessBuilder pb, InputStream inputStream) throws IOException, InterruptedException {
        return super.execute(pb, inputStream)
    }

    @Override
    ExecutionResult execute(ProcessBuilder pb, InputStream inputStream) throws IOException, InterruptedException {
        ExecutionResult er = GitFSFactoryJrr.gitRunCmdCustom.execute(pb, inputStream, this)
        if (er != null) {
            return er;
        }
        return super.execute(pb, inputStream)
    }

    @Override
    int runProcess(ProcessBuilder processBuilder, OutputStream outRedirect, OutputStream errRedirect, String stdinArgs) throws IOException, InterruptedException {
        return super.runProcess(processBuilder, outRedirect, errRedirect, stdinArgs)
    }

    @Override
    int runProcess(ProcessBuilder processBuilder, OutputStream outRedirect, OutputStream errRedirect, InputStream inRedirect) throws IOException, InterruptedException {
        return super.runProcess(processBuilder, outRedirect, errRedirect, inRedirect)
    }


    @Override
    FS receiveFS() {
        return this
    }

    @Override
    File findHook(Repository repository, String hookName) {
        File f = GitFSFactoryJrr.gitRunCmdCustom.findHook(repository, hookName, this)
        if (f != null) {
            return f
        }
        File fr = super.findHook(repository, hookName)
        return GitFSFactoryJrr.gitRunCmdCustom.foundHook(repository, hookName, this, fr)
    }

    @Override
    ProcessResult runHookIfPresentSuper(Repository repository, String hookName, String[] args, OutputStream outRedirect, OutputStream errRedirect, String stdinArgs) throws JGitInternalException {
        return super.runHookIfPresent(repository, hookName, args, outRedirect, errRedirect, stdinArgs)
    }

    @Override
    ProcessResult runHookIfPresent(Repository repository, String hookName, String[] args, OutputStream outRedirect, OutputStream errRedirect, String stdinArgs) throws JGitInternalException {
        return GitFSFactoryJrr.gitRunCmdCustom.runHookIfPresent(repository, hookName, args, outRedirect, errRedirect, stdinArgs, this)
    }

    @Override
    File userHome() {
        File userHomeJrr =GitFSFactoryJrr.gitRunCmdCustom.findUserHome( this)
        if(userHomeJrr!=null){
            return userHomeJrr
        }
        return super.userHome()
    }

}
