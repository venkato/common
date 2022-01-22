package net.sf.jremoterun.utilities.nonjdk.git.fs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.util.FS
import org.eclipse.jgit.util.ProcessResult;

import java.util.logging.Logger;

@CompileStatic
interface FxJrrI {


//    GitRunCmdCustom getGitRunCmdCustom();
//    void setGitRunCmdCustom(GitRunCmdCustom c);

    FS receiveFS()

    ProcessBuilder runInShellSuper(String cmd, String[] args)

    FS.ExecutionResult executeSuper(ProcessBuilder pb, InputStream inputStream) throws IOException, InterruptedException

    ProcessResult runHookIfPresentSuper(Repository repository, String hookName, String[] args, OutputStream outRedirect, OutputStream errRedirect, String stdinArgs)
}
