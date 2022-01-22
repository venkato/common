package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.eclipse.jgit.api.AddCommand
import org.eclipse.jgit.api.Status
import org.eclipse.jgit.api.StatusCommand
import org.eclipse.jgit.diff.DiffAlgorithm
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.dircache.DirCache
import org.eclipse.jgit.dircache.DirCacheEntry
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.ObjectDatabase
import org.eclipse.jgit.lib.ObjectLoader
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.merge.MergeAlgorithm
import org.eclipse.jgit.merge.MergeFormatter
import org.eclipse.jgit.merge.MergeResult
import org.eclipse.jgit.util.io.AutoCRLFOutputStream

import java.nio.charset.StandardCharsets
import java.util.logging.Logger

@CompileStatic
class DiffApplierFast {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<DiffEntry> entriesOriginal;
    public CommitIdCanonicalTreeParser iterator1;
    public CommitIdCanonicalTreeParser iterator2;
    public GitRepoUtils gitRepoUtils;
    public IdentityHashMap<DiffEntry, DiffStatusEnum> diffStatus = new IdentityHashMap<>()

    public DirCache dirCache;
    public Status status1

    DiffApplierFast(List<DiffEntry> entries, GitRepoUtils gitRepoUtils) {
        this.entriesOriginal = entries
        this.gitRepoUtils = gitRepoUtils
    }


    DiffApplierFast(List<DiffEntry> entriesOriginal, CommitIdCanonicalTreeParser iterator1, CommitIdCanonicalTreeParser iterator2, GitRepoUtils gitRepoUtils) {
        this.entriesOriginal = entriesOriginal
        this.iterator1 = iterator1
        this.iterator2 = iterator2
        this.gitRepoUtils = gitRepoUtils
    }


    public static enum DiffStatusEnum {
        modifiedNoConflict,
        added,
        copied,
        deleted,
        renamed,
        ignored,
    }

    List<DiffEntry> getByStatus(DiffStatusEnum statusEnum) {
        return diffStatus.findAll { it.value == statusEnum }.collect { it.key }
    }


    void prepare() {
        callStatus()
//        handleStatus()
        readCache()
        entriesOriginal.each {
            DiffStatusEnum take = resolveStatus(it)
            diffStatus.put(it, take)
        }
    }

    void doStuff() {
        prepare()
        applyNewValues()
        if (isHasSomethingToAdd()) {
            addToIndex()
        }
    }

    boolean isHasSomethingToAdd() {
        return calcTouchedNow().size() > 0 || calcRemoved().size() > 0
    }

    HashSet<String> calcRemoved() {
        HashSet<String> res = new HashSet<>()
        getByStatus(DiffStatusEnum.deleted).each { res.add(it.getOldPath()) }
        getByStatus(DiffStatusEnum.renamed).each { res.add(it.getOldPath()) }
        return res
    }

    HashSet<String> calcTouchedNow() {
        HashSet<String> res = new HashSet<>()
        getByStatus(DiffStatusEnum.modifiedNoConflict).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.added).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.copied).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.renamed).each { res.add(it.getNewPath()) }
        return res
    }

    HashSet<String> calcAddedNow() {
        HashSet<String> res = new HashSet<>()
        getByStatus(DiffStatusEnum.added).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.copied).each { res.add(it.getNewPath()) }
        getByStatus(DiffStatusEnum.renamed).each { res.add(it.getNewPath()) }
        return res
    }


    void applyNewValues() {
        getByStatus(DiffStatusEnum.modifiedNoConflict).each { applyNewValueEntry(it) }
        getByStatus(DiffStatusEnum.added).each {
            applyNewValueEntry(it)
//            addedNow.add(it.getNewPath())
        }
        getByStatus(DiffStatusEnum.copied).each {
            applyNewValueEntry(it)
//            addedNow.add(it.getNewPath())
        }
        getByStatus(DiffStatusEnum.deleted).each { deletePath(it.getOldPath()) }
        getByStatus(DiffStatusEnum.renamed).each {
            applyNewValueEntry(it)
            deletePath(it.getOldPath())
//            addedNow.add(it.getNewPath())
        }
    }


    void callStatus() {
        StatusCommand status = gitRepoUtils.git.status()
        HashSet<String> badPaths = new HashSet<>()
        entriesOriginal.each {
            DiffEntryUtils.addBothEntryPaths(it, badPaths)
        }
        badPaths.each { status.addPath(it) }
        status1 = status.call()
    }

    DiffStatusEnum resolveStatus(DiffEntry entry) {
        switch (entry.getChangeType()) {
            case DiffEntry.ChangeType.MODIFY:
                return DiffStatusEnum.modifiedNoConflict
            case DiffEntry.ChangeType.ADD:
                return DiffStatusEnum.added
            case DiffEntry.ChangeType.COPY:
                return DiffStatusEnum.copied
            case DiffEntry.ChangeType.DELETE:
                return DiffStatusEnum.deleted
            case DiffEntry.ChangeType.RENAME:
                return DiffStatusEnum.renamed
            default:
                throw new UnsupportedOperationException("${entry.getChangeType()} ${entry}")
        }
    }


    void readCache() {
        Repository repo = gitRepoUtils.gitRepository
        dirCache = repo.readDirCache()
    }

    void applyNewValueEntry(DiffEntry entry) {
        File f = new File(gitRepoUtils.gitBaseDir, entry.getNewPath());
        File parentDir = f.getParentFile()
        parentDir.mkdirs()
        assert parentDir.exists()
        BufferedOutputStream outputStream = f.newOutputStream()
        try {
            ObjectLoader objectLoader = gitRepoUtils.gitRepository.open(entry.getNewId().toObjectId())
            objectLoader.copyTo(outputStream);
            outputStream.flush()
        } finally {
            JrrIoUtils.closeQuietly2(outputStream, log)
        }
    }

    void deletePath(String path) {
        File f = new File(gitRepoUtils.gitBaseDir, path);
        assert f.exists()
        f.delete()
        assert !f.exists()
    }

    void addToIndex() {
        if (calcTouchedNow().size() > 0) {
            addToIndex3(calcTouchedNow(), true)
        }
        addToIndex2(calcRemoved())
        addToIndex2(calcAddedNow())
    }


    void addToIndex2(HashSet<String> paths) {
        if (paths.size() > 0) {
            addToIndex3(paths, true)
            addToIndex3(paths, false)
        }
    }

    void addToIndex3(HashSet<String> paths, boolean setUpdated1) {
        AddCommand addCommand = gitRepoUtils.git.add()
        addCommand.setUpdate(setUpdated1)
        gitRepoUtils.doCustomGitTuning(addCommand)
        paths.each { addCommand.addFilepattern(it) }
        dirCache = addCommand.call()
    }


}
