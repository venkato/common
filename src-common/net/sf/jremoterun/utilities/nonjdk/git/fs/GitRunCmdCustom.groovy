package net.sf.jremoterun.utilities.nonjdk.git.fs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.util.ProcessResult;

import java.util.logging.Logger;

@CompileStatic
class GitRunCmdCustom {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File defaultGitLocation
    public File gitSystemConfig
    public File userHome
    public HashSet<String> mentionedHooks = new HashSet<>()


    ProcessBuilder runInShell(String cmd, String[] args, FxJrrI fs) {
        log.info("running cmd ${cmd} ${args}")
        return null
    }


    FS.ExecutionResult execute(ProcessBuilder pb, InputStream inputStream, FxJrrI fs) throws IOException, InterruptedException {
        return null
    }


    File discoverGitExe() {
        return defaultGitLocation
    }

    File discoverGitSystemConfig() {
        return gitSystemConfig
    }

    File getGitSystemConfig() {
        return gitSystemConfig
    }

    File discoveredGitExe(File file) {
        log.info("git exe = ${file}")
        return file
    }

    File discoveredGitSystemConfig(File file) {
        log.info("git config discoveredGitSystemConfig = ${file}")
        return file
    }

    File gitSystemConfigUses(File file) {
        log.info("git config gitSystemConfigUses = ${file}")
        return file
    }

    File findHook(Repository repository, String hookName, FxJrrI fs) {
        log.info "checking if hook present : ${hookName}"
        mentionedHooks.add(hookName)
        return null
    }

    File foundHook(Repository repository, String hookName, FxJrrI fs, File file) {
        log.info("using hook ${hookName} =  ${file}")
        mentionedHooks.add(hookName)
        return file
    }

    ProcessResult runHookIfPresent(Repository repository, String hookName, String[] args, OutputStream outRedirect, OutputStream errRedirect, String stdinArgs, FxJrrI fsPosixJrr) {
        log.info "running hook if present = ${hookName} cmd = ${args}"
        mentionedHooks.add(hookName)
        return fsPosixJrr.runHookIfPresentSuper(repository, hookName, args, outRedirect, errRedirect, stdinArgs)
    }

    File findUserHome(FxJrrI fs) {
        return userHome
    }
}
