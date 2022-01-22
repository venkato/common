package net.sf.jremoterun.utilities.nonjdk.git.walktree

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.CommitApplier
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoType
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog.OpenUrlHandler
import net.sf.jremoterun.utilities.nonjdk.rstacore.TextAssociatedRemeberLinkGenerator
import net.sf.jremoterun.utilities.nonjdk.rstarunner.RstaRunnerWithStackTrace2
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.logging.Logger;

@CompileStatic
class GitRstaRunnerWithStackTrace2 extends RstaRunnerWithStackTrace2{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public TextAssociatedRemeberLinkGenerator shaCommitLinkGenerator;

    GitRstaRunnerWithStackTrace2(File file,OpenUrlHandler openUrlHandler) {
        super(file)
        shaCommitLinkGenerator = new TextAssociatedRemeberLinkGenerator(openUrlHandler)
        logsView.textArea.setLinkGenerator(shaCommitLinkGenerator)
    }

    GitRstaRunnerWithStackTrace2(String text2,OpenUrlHandler openUrlHandler) {
        super(text2)
        shaCommitLinkGenerator = new TextAssociatedRemeberLinkGenerator(openUrlHandler)
        logsView.textArea.setLinkGenerator(shaCommitLinkGenerator)
    }

    @Override
    RstaRunnerWithStackTrace2 createClonePanelBasic() {
        return new GitRstaRunnerWithStackTrace2(fileWithConfig,shaCommitLinkGenerator.openUrlHandler);
    }

    void initWithSuffix(GitRepoUtils gitRepoUtils, GitRepoType gitRepoType){
        shaCommitLinkGenerator.links.clear()
        shaCommitLinkGenerator.urlPrefix = new URL(gitRepoUtils.guessRepoUrl().toString()+gitRepoType.commitSuffix)
    }


    void init(GitTreeWalker gitTreeWalker){
        onStopRequestedRunnable = {gitTreeWalker.continues = false};
    }


    void addCommitLinks(CommitApplier commitApplier){
        commitApplier.revCommits.each {
            addCommitLinks(it)
        }
        commitApplier.newRevCommits.each {
            addCommitLinks(it)
        }
    }

    void addCommitLinks(GitTreeWalker gitTreeWalker){
        gitTreeWalker.bigCommits.each {
            addCommitLinks(it.commit)
        }
        gitTreeWalker.foundCommits.each {
            addCommitLinks(it.commit)
            addCommitLinks(it.parent2)
        }
    }

    void addCommitLinks(RevCommit commit){
        shaCommitLinkGenerator.links.add(commit.name())

    }

}
