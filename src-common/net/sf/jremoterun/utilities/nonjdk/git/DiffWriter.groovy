package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawTextComparator
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.treewalk.filter.TreeFilter;

import java.util.logging.Logger

@CompileStatic
class DiffWriter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<DiffEntry.ChangeType> excludeChangeTypes = [];
    public List<DiffEntry> entries
    public GitRepoUtils gitRepoUtils;
    public Collection<String> includePaths;
    public Collection<String> excludePathsStartWith = [];
    public Collection<String> headers = [];
    public Collection<String> footers = [];
    public TreeSet<String> ignored = [];
    public RawTextComparator rawTextComparator = RawTextComparator.WS_IGNORE_ALL
    public int maxFileSize = 10_000_000
    public boolean writeDiffContext = true

    DiffWriter(List<DiffEntry> entries, GitRepoUtils gitRepoUtils) {
        this.entries = entries
        this.gitRepoUtils = gitRepoUtils
    }

    void logEntries(){
        entries.each {
            log.info "${it}"
        }
    }

    List<String> getModifiedOrAdded(){
        List<DiffEntry> all1 = entries.findAll { it.getNewPath() != null }
        return all1.collect {it.getNewPath()}.sort()
    }

    boolean isEntryMatch(DiffEntry diffEntry) {
        if (isEntryMatchImpl(diffEntry)) {
            return true
        }
        DiffEntryUtils.addBothEntryPaths(diffEntry, ignored)
        return false
    }

    boolean isEntryMatchImpl(DiffEntry diffEntry) {
        if (excludeChangeTypes.contains(diffEntry.getChangeType())) {
            return false
        }
        if (DiffEntryUtils.isAnyPathStartWith(diffEntry, excludePathsStartWith)) {
            return false
        }
        if (includePaths != null) {
            if (DiffEntryUtils.isAnyPathMatched(diffEntry, includePaths)) {
                return true
            }
            return false
        }
        return true
    }

    void convertDiffToText(File file) {
        BufferedOutputStream outputStream = file.newOutputStream()
        try {
            convertDiffToText(outputStream)
        } finally {
            JrrIoUtils.closeQuietly2(outputStream,log)
        }
    }

    String convertDiffToText2() {
        ByteArrayOutputStream baas = new ByteArrayOutputStream()
        convertDiffToText(baas)
        return baas.toString()
    }

    void convertDiffToText(OutputStream outputStream) {
        DiffFormatter formatter = createDiffDefaultFormatter(outputStream)
        try {
            writeHeader(outputStream)
            convertDiffToText(formatter)
            writeFooter(outputStream)
            outputStream.flush()
        } finally {
            JrrIoUtils.closeQuietly2(formatter,log)
        }
    }

    void writeHeader(OutputStream out1) {
        headers.each { out1.write(it.getBytes());out1.write(DiffFormatterJrr.newLineBytes) }
    }

    void writeFooter(OutputStream out1) {
        if(ignored.size()>0){
            out1.write DiffFormatterJrr.gitDiffBytes
            out1.write "ignored ${ignored.size()}\n".getBytes()
            ignored.each {out1.write "${it}\n".getBytes()}
            out1.write(DiffFormatterJrr.newLineBytes)
        }
        footers.each { out1.write(it.getBytes());out1.write(DiffFormatterJrr.newLineBytes) }
    }

    DiffFormatter createDiffDefaultFormatterInstance(OutputStream out1) {
        DiffFormatterJrr diffFormatter = new DiffFormatterJrr(out1)
        diffFormatter.writeContext = writeDiffContext
        diffFormatter.setPathFilter(TreeFilter.ALL)
        return diffFormatter
    }

    DiffFormatter createDiffDefaultFormatter(OutputStream out1) {
        DiffFormatter diffFormatter = createDiffDefaultFormatterInstance(out1);
        diffFormatter.setDiffComparator(rawTextComparator)
        diffFormatter.setProgressMonitor(gitRepoUtils.progressMonitor)
        diffFormatter.setRepository(gitRepoUtils.gitRepository)
        diffFormatter.setBinaryFileThreshold(maxFileSize);
        return diffFormatter
    }

    List<DiffEntry> findMatchedEntries() {
        return entries.findAll { isEntryMatch(it) }
    }

    void convertDiffToText(DiffFormatter diffFormatter) {
        diffFormatter.format(findMatchedEntries())
        diffFormatter.flush()
    }
}
