package net.sf.jremoterun.utilities.nonjdk.git.walktree

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.logging.Logger;

@CompileStatic
class CommitFoundEl {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    public RevCommit commit;
    public RevCommit parent2;
    public int parentNum;
    public List<DiffEntryEnriched> diffEntries = [];

    CommitFoundEl(RevCommit commit, RevCommit parent2, int parentNum) {
        //this.scanResult2 = scanResult2
        this.commit = commit
        this.parent2 = parent2
        this.parentNum = parentNum
    }
}
