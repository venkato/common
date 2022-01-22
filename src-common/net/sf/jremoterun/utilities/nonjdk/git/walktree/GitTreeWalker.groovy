package net.sf.jremoterun.utilities.nonjdk.git.walktree

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.git.GitRepoUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.rstarunner.StatusDelayListener
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectDatabase
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.ObjectLoader
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.lib.ReflogEntry
import org.eclipse.jgit.lib.ReflogReader
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTree
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.filter.PathFilter
import org.eclipse.jgit.treewalk.filter.TreeFilter

import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.logging.Logger;

/**
 * show raw commit : git cat-file -p <sha1|tree-id>
 */
@CompileStatic
class GitTreeWalker {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd')
    public boolean continues = true
    public GitRepoUtils gitRepoUtils;
    public TreeWalkJrr treeWalk;
    public RevWalk revWalk


    public TreeFilter[] treeFilterMarker;
    public ObjectDatabase db
    public StatusDelayListener statusDelayListener;

    public boolean continueThisDeep = true
    public int currentDepth = -1


    public int checkedCommitsCount = 0;
    public int diffFound = 0;
    public RevCommit oldestCheckedCommit;
    public RevCommit oldestDiffFoundCommit;
    public HashSet<String> checkedCommits = new HashSet<>()
    public Boolean doDiffAnalize


    public TreeWalkSettings s = new TreeWalkSettings();

//    public boolean fetchBefore = true;
//    public List<String> fetchPrefix = ['origin',]


    public Map<String, CommitFoundEl> bigCommitsMap = [:]
    public List<CommitFoundEl> bigCommits = []

    public Map<String, CommitFoundEl> foundCommitsMap = [:]
    public List<CommitFoundEl> foundCommits = []
    public Stack<RevCommit> commitStack = new Stack<>()


    GitTreeWalker(GitRepoUtils gitRepoUtils) {
        this.gitRepoUtils = gitRepoUtils
        treeWalk = new TreeWalkJrr(gitRepoUtils.gitRepository);
        revWalk = new RevWalk(gitRepoUtils.gitRepository)
        db = gitRepoUtils.gitRepository.getObjectDatabase();
    }

    /**
     * @see org.eclipse.jgit.api.ReflogCommand
     */
    private void doFromRefLog(String refName){
        ReflogReader reflogReader = gitRepoUtils.gitRepository.getReflogReader(refName)
        List<ReflogEntry> entries = reflogReader.getReverseEntries()
        entries.each {
            ObjectId newId = it.getNewId()
            ObjectId oldId = it.getOldId()
            PersonIdent who = it.getWho()
            String comment = it.getComment()

        }
    }

    void doOne(String commitID) throws IOException {
        try {
            doDiffAnalize = s.isDoDiff()
            gitRepoUtils.fetcherIfNeeded.fetchIfNeeded(commitID)

            RevCommit commit2 = gitRepoUtils.findCommit(commitID)
            commit2 = revWalk.parseCommit(commit2)
            searchDeep(commit2)
        } finally {
            close()
        }
    }

    void doMany(List<String> commitIDs) throws IOException {
        try {
            doDiffAnalize = s.isDoDiff()
            commitIDs.each {
                gitRepoUtils.fetcherIfNeeded.fetchIfNeeded(it)
            }
            commitIDs.each {
                RevCommit commit2 = gitRepoUtils.findCommit(it)
                commit2 = revWalk.parseCommit(commit2)
                searchDeep(commit2)
            }
        } finally {
            close()
        }
    }

    static String getCommitDateStr(RevCommit commitID) {
        return sdf.format(getCommitDate(commitID))
    }

    static Date getCommitDate(RevCommit commitID) {
        long time1 = commitID.getCommitTime()
        time1 = time1 * 1000
        return new Date(time1);
    }

    void close() {
        JrrIoUtils.closeQuietly2(treeWalk, log);
        JrrIoUtils.closeQuietly2(revWalk, log);
        db.close()
    }


    void searchDeep(RevCommit commitID) throws IOException {
        commitStack.clear()
        RevCommit currentCommit = commitID
        while (true) {
            RevCommit ress = searchDeepImpl(currentCommit)
            if (ress == null) {
                log.info("seems finish search")
                break
            } else {
                currentCommit = ress
            }

        }
    }

    RevCommit searchDeepImpl(final RevCommit currentCommit) throws IOException {
                //log.info "cp1 = ${currentCommit.name()}"
        int parentCOunt = currentCommit.getParentCount()
        boolean checkFoundCommit = false
        for (int i = 0; i < parentCOunt; i++) {
            if (!continues) {
                return null
            }
//                log.info "checking ${commitID.name()} - ${i}"
            RevCommit parent2 = currentCommit.getParent(i);
            String key2 = buildCommitKey(currentCommit, parent2)
//                    log.info "checking ${key2}"
            if (checkedCommits.contains(key2)) {

            } else {
                checkedCommits.add key2
                parent2 = revWalk.parseCommit(parent2);
                boolean searchdeeper1 = isSearchDeeper(currentCommit, parent2, s.depthCountMax - commitStack.size(), i)
                        //log.info "search deeper ${key2} ${searchdeeper1}"
                if (searchdeeper1) {
                    if (!continues) {
                        return null
                    }
                    checkFoundCommit = true
                    if (isSearchThisCommitOnly(currentCommit, parent2, s.depthCountMax, i)) {
                        searchThisCommitAndParent(currentCommit, parent2, s.depthCountMax, i);
                    }
                    commitStack.push(currentCommit)
                    return parent2
                }
            }

        }
        if (!checkFoundCommit) {
            if (commitStack.size() == 0) {
                return null
            }
            RevCommit pop = commitStack.pop()
            return pop

        }
        return currentCommit
    }


    String buildCommitKey(RevCommit commitID, RevCommit parent2) {
        return "${commitID.name()}-${parent2.name()}"
    }

    boolean isSearchThisCommitOnly(RevCommit commitID, RevCommit parent2, int depthCount, int parentCOunt) {
        if (s.inspectOnly0Parent && parentCOunt != 0) {
            return false
        }
        if (s.startOlder != null) {
            if (s.startOlder < getCommitDate(commitID)) {
                return false
            }
        }
        return true
    }


    boolean isSearchDeeper(RevCommit thisCommit, RevCommit parent2, int depthCount, int parentCOunt) {
        if (s.stopOlder != null) {
            if (s.stopOlder > getCommitDate(thisCommit)) {
                return false
            }
        }
        return depthCount > 0;
    }


    void writeStatus(RevCommit commit, RevCommit parent2, int depthCount, int childOffset) throws IOException {
        if (statusDelayListener != null) {
            statusDelayListener.setStatusWithDelay("d=${depthCount} ${checkedCommitsCount}")
        }
    }

    void searchThisCommitAndParent(RevCommit commit, RevCommit parent2, int depthCount, int childOffset) throws IOException {
        if (oldestCheckedCommit.is(null)) {
            oldestCheckedCommit = commit;
        } else {
            if (oldestCheckedCommit.getCommitTime() > commit.getCommitTime()) {
                oldestCheckedCommit = commit;
            }
        }
        writeStatus(commit, parent2, depthCount, childOffset)
        checkedCommitsCount++
        currentDepth = depthCount
        RevTree tree = commit.getTree();
        RevTree treeParent = parent2.getTree();
        try {
            treeWalk.reset()
            treeWalk.addTree(treeParent);
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(s.pathFilter);
            List<DiffEntry> scanResult = analeiseDiff()

            if (continues && scanResult.size() > 0) {
                if (oldestDiffFoundCommit.is(null)) {
                    oldestDiffFoundCommit = commit;
                } else {
                    if (oldestDiffFoundCommit.getCommitTime() > commit.getCommitTime()) {
                        oldestDiffFoundCommit = commit;
                    }
                }
                diffFound++
                List<DiffEntryEnriched> scanResult2 = []
                scanResult.each {
                    if (continues) {
                        if (doDiffAnalize) {
                            if (checkDiffSize(it)) {
                                DiffEntryEnriched entryEnriched = analiseDiffElement(it, commit, parent2, childOffset)
                                scanResult2.add(entryEnriched);
                            } else {
                                onDiffBig(it, commit, parent2, childOffset)
                            }
                        } else {
                            DiffEntryEnriched entryEnriched = analiseDiffElement(it, commit, parent2, childOffset)
                            scanResult2.add(entryEnriched);
                        }
                    }
                }
                onDiffs(scanResult2, commit, parent2)
            }
            commit.getCommitTime()
        } finally {

        }
    }

    DiffEntryEnriched analiseDiffElement(DiffEntry diffEntry, RevCommit commit, RevCommit parent2, int childOffset) {
        ByteArrayOutputStream ou1 = new ByteArrayOutputStream()
        DiffFormatterOneFileJrr diffFormatter = createDiffComparator(ou1)
        try {
            DiffEntryEnriched entryEnriched = new DiffEntryEnriched(diffEntry)
            diffFormatter.diffEntry1 = entryEnriched
            diffFormatter.format(diffEntry);
            String diff123 = new String(ou1.toByteArray())

            entryEnriched.diffText = diff123
            onDiff(entryEnriched, commit, parent2, childOffset)
            return entryEnriched
        } finally {
            JrrIoUtils.closeQuietly2(diffFormatter, log);
        }
    }

    DiffEntryEnriched analiseDiffElementWithoutDiff(DiffEntry diffEntry, RevCommit commit, RevCommit parent2, int childOffset) {
        //ByteArrayOutputStream ou1 = new ByteArrayOutputStream()
        //DiffFormatterOneFileJrr diffFormatter = createDiffComparator(ou1)
        DiffEntryEnriched entryEnriched = new DiffEntryEnriched(diffEntry)
        onDiff(entryEnriched, commit, parent2, childOffset)
        return entryEnriched
    }


    boolean checkDiffSize(DiffEntry it) {
        if (it.getOldMode() != FileMode.MISSING) {
            ObjectLoader objectLoaderBase = db.open(it.getOldId().toObjectId(), Constants.OBJ_BLOB)
            if (objectLoaderBase.getSize() > s.maxSizeInBytes) {
                return false
            }
        }
        if (it.getNewMode() != FileMode.MISSING) {
            ObjectLoader objectLoaderTheir = db.open(it.getNewId().toObjectId(), Constants.OBJ_BLOB)
            if (objectLoaderTheir.getSize() > s.maxSizeInBytes) {
                return false
            }
        }
        return true
    }


    DiffFormatterOneFileJrr createDiffComparator(OutputStream ou1) {
//        assert doDiffAnalize == true
        DiffFormatterOneFileJrr diffFormatter = new DiffFormatterOneFileJrr(ou1, this)
        diffFormatter.setPathFilter(TreeFilter.ALL)
        diffFormatter.setRepository(gitRepoUtils.gitRepository)
        diffFormatter.setDiffComparator(RawTextComparator.WS_IGNORE_ALL)
        diffFormatter.setDetectRenames(false)
        diffFormatter.setBinaryFileThreshold(s.maxSizeInBytes)
        diffFormatter.setQuotePaths(false)
        return diffFormatter
    }

    void onDiffs(List<DiffEntryEnriched> scanResult2, RevCommit commit, RevCommit parent2) {

    }

    void onDiffBig(DiffEntry scanResult2, RevCommit commit, RevCommit parent2, int childOffset) {
        DiffEntryEnriched entryEnriched = new DiffEntryEnriched(scanResult2)
        addEl(entryEnriched, commit, parent2, childOffset, bigCommitsMap, bigCommits)
    }

    CommitFoundEl addEl(DiffEntryEnriched scanResult2, RevCommit commit, RevCommit parent2, int childOffset, Map<String, CommitFoundEl> bigCommitsMap, List<CommitFoundEl> bigCommits) {
        String key2 = buildCommitKey(commit, parent2);
        CommitFoundEl get2 = bigCommitsMap.get(key2)
        if (get2 == null) {
            get2 = new CommitFoundEl(commit, parent2, childOffset);
            bigCommitsMap.put(key2, get2)
            bigCommits.add(get2)
        }
        get2.diffEntries.add(scanResult2)
        return get2

    }


    Boolean checkDiffMatched(DiffEntryEnriched scanResult2) {
        boolean commitFound = false
        boolean anyNotNull = false
        if (s.diffAddOnlyContains != null) {
            anyNotNull = true
            String addedFound = scanResult2.linesAdded.find { it.contains(s.diffAddOnlyContains) }
            if (addedFound != null) {
                String removedFound3 = scanResult2.linesRemoved.find { it.contains(s.diffAddOnlyContains) }
                if (removedFound3 == null) {
                    commitFound = true
                }
            }
            if (!commitFound && s.allNeedMatched) {
                return false
            }
        }
        if (s.diffRemoveOnlyContains != null) {
            anyNotNull = true
            String addedFound = scanResult2.linesRemoved.find { it.contains(s.diffRemoveOnlyContains) }
            if (addedFound != null) {
                String removedFound3 = scanResult2.linesAdded.find { it.contains(s.diffRemoveOnlyContains) }
                if (removedFound3 == null) {
                    commitFound = true
                }
            }
            if (!commitFound && s.allNeedMatched) {
                return false
            }
        }
        if (s.diffTouchOnlyContains != null) {
            anyNotNull = true
            String addedFound = scanResult2.linesRemoved.find { it.contains(s.diffTouchOnlyContains) }
            if (addedFound != null) {
                String removedFound3 = scanResult2.linesAdded.find { it.contains(s.diffTouchOnlyContains) }
                if (removedFound3 != null) {
                    commitFound = true
                }
            }
            if (!commitFound && s.allNeedMatched) {
                return false
            }
        }
        if (anyNotNull) {
            return commitFound
        }
        return null
    }

    void onDiff(DiffEntryEnriched diffEntryEnriched, RevCommit commit, RevCommit parent2, int childOffset) {
        boolean  goodCommit = onDiffisGoodCommit(diffEntryEnriched,commit,parent2,childOffset)
        //log.info("${commit.name()} ${commit.getShortMessage()}  ${goodCommit}")
        if (goodCommit) {
            addMatchedCommit(diffEntryEnriched, commit, parent2, childOffset)
            //addEl(diffEntryEnriched, commit, parent2, childOffset, foundCommitsMap, foundCommits);
            //foundCommits.add(new CommitFoundEl(diffEntryEnriched.diffEntry, commit, parent2,childOffset))
        }

    }

    boolean onDiffisGoodCommit(DiffEntryEnriched diffEntryEnriched, RevCommit commit, RevCommit parent2, int childOffset) {
        Boolean commitFoundFromDiff
        if (doDiffAnalize == true) {
            commitFoundFromDiff = checkDiffMatched(diffEntryEnriched)
        }
        //commit.getAuthorIdent()
        Boolean commitFound
        Boolean commitRawFound
        if (s.commitContains.size()>0) {
            if (commit.getShortMessage() != null) {
                if (s.isContains(commit.getShortMessage())) {
                    commitFound = true
                }
            }
            if (commit.getFullMessage() != null) {
                if (s.isContains(commit.getFullMessage())) {
//                if (commit.getFullMessage().contains(s.commitContains)) {
                    commitFound = true
                }
            }
            if (commitFound) {
            } else {
                commitFound = false
            }
        }
        if (s.commitRawContains.size()>0) {
            String s1 = new String(commit.getRawBuffer(), StandardCharsets.UTF_8);
            if ( this.s.isRawContains(s1)) {
                commitRawFound = true
            }

        }
        Boolean authorMatched;
        if (s.commitAuthorContains != null) {
            PersonIdent authorIdent = commit.getAuthorIdent()
            String name1 = authorIdent.getName()
            String emailAddress = authorIdent.getEmailAddress()
            //     log.info "name=${name1} email=${emailAddress}"
            if (name1 != null) {
                if (name1.contains(s.commitAuthorContains)) {
                    authorMatched = true
                }
            }
            if (emailAddress != null) {
                if (emailAddress.contains(s.commitAuthorContains)) {
                    authorMatched = true
                }
            }
            if (authorMatched == null) {
                authorMatched = false
            }

        }

        boolean goodCommit = false
        if (s.allNeedMatched) {
            if ((commitFound == null || commitFound) && (commitRawFound == null || commitRawFound) && (authorMatched == null || authorMatched) && (commitFoundFromDiff == null || commitFoundFromDiff)) {
                goodCommit = true
            }
        } else {
            if (commitFound || commitRawFound || commitFoundFromDiff || authorMatched) {
                goodCommit = true
            }
        }
//log.info "onDiff ${getCommitDate(commit)} ${commit.name()} ${commitFoundFromDiff} ${commitFound} authorMatched=${authorMatched} good = ${goodCommit}"
        return goodCommit
    }


    CommitFoundEl addMatchedCommit(DiffEntryEnriched scanResult2, RevCommit commit, RevCommit parent2, int childOffset) {
        return addEl(scanResult2, commit, parent2, childOffset, foundCommitsMap, foundCommits)
    }

    List<DiffEntry> analeiseDiff() {
        List<DiffEntry> scanResult = DiffEntry.scan(treeWalk, false, treeFilterMarker)
        return scanResult;
    }

}
