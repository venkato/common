package net.sf.jremoterun.utilities.nonjdk.git.walktree

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.diff.DiffEntry;

import java.util.logging.Logger;

@CompileStatic
class DiffEntryEnriched {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public DiffEntry diffEntry;
    public List<String> linesAdded = [] ;
    public List<String> linesRemoved = [];
    public String diffText;

    DiffEntryEnriched(DiffEntry diffEntry) {
        this.diffEntry = diffEntry
        this.diffText = diffText
    }


}
