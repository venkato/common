package net.sf.jremoterun.utilities.nonjdk.git.walktree.gitreport

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.walktree.CommitFoundEl
import net.sf.jremoterun.utilities.nonjdk.git.walktree.DiffEntryEnriched
import net.sf.jremoterun.utilities.nonjdk.git.walktree.GitTreeWalker

import java.nio.charset.StandardCharsets
import java.util.logging.Logger;

@CompileStatic
class GitReport {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GitTreeWalker gitTreeWalker;
    public List<GitReportColumns> columnsList = [GitReportColumns.childCount, GitReportColumns.sha1, GitReportColumns.commitDate, GitReportColumns.descriptionShort, GitReportColumns.descriptionLong]
    public String columnSeparator = ' '


    GitReport(GitTreeWalker gitTreeWalker) {
        this.gitTreeWalker = gitTreeWalker
    }

    void writeAll() {
        String oldestCommit = GitTreeWalker.getCommitDateStr(gitTreeWalker.oldestCheckedCommit)
        log.info "older checked commit : ${oldestCommit}"
        log.info gitTreeWalker.treeWalk.reportStat()
        writeBigCommits()
        writeFoundCommits()
    }

    void writeBigCommits() {
        if (gitTreeWalker.bigCommits.size() > 0) {
            log.info("got big commits ${gitTreeWalker.bigCommits.size()}")
            gitTreeWalker.bigCommits.each {
                log.info "got big commit ${convertElToString(it)}";
            }
        }
    }

    void writeFoundCommits() {
        log.info("founded commits ${gitTreeWalker.foundCommits.size()}")
        gitTreeWalker.foundCommits.each {
            log.info convertElToString(it);
        }

    }

    List<Object> buildRow(CommitFoundEl el) {
        return columnsList.collect { writeColumn(it, el) }
    }

    String convertElToString(CommitFoundEl el) {
        return buildRow(el).join(columnSeparator)
    }

    Object writeColumn(GitReportColumns column, CommitFoundEl el) {
        switch (column) {
            case GitReportColumns.childCount:
                return el.parentNum;
            case GitReportColumns.sha1:
                return el.commit.name();
            case GitReportColumns.sha1Parent:
                return el.parent2.name();
            case GitReportColumns.rawBuffer:
                return new String(el.commit.getRawBuffer(), StandardCharsets.UTF_8);
            case GitReportColumns.commitDate:
                return GitTreeWalker.getCommitDateStr(el.commit);
            case GitReportColumns.descriptionShort:
                return el.commit.getShortMessage();
            case GitReportColumns.descriptionLong:
                return el.commit.getFullMessage();
            case GitReportColumns.authorName:
//                return el.commit.getAuthorIdent().getName();
                return el.commit.getAuthorIdent().getName();
            case GitReportColumns.authorEmail:
                return el.commit.getAuthorIdent().getEmailAddress();
            case GitReportColumns.diffSummary:
                return el.diffEntries.collect {formatDiffEntryEnriched(it)};
        }
        throw new UnsupportedOperationException("${column}")
    }

    Object formatDiffEntryEnriched(DiffEntryEnriched entryEnriched){
        return entryEnriched.diffEntry
    }

}
