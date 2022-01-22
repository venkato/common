package net.sf.jremoterun.utilities.nonjdk.git.walktree

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.diff.RawText
import org.eclipse.jgit.patch.FileHeader

import java.util.logging.Logger

@CompileStatic
class DiffFormatterOneFileJrr extends DiffFormatter {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GitTreeWalker gitTreeWalker;
    public DiffEntryEnriched diffEntry1;

    DiffFormatterOneFileJrr(OutputStream out,GitTreeWalker gitTreeWalker) {
        super(out)
        this.gitTreeWalker = gitTreeWalker
    }

    @Override
    protected void formatGitDiffFirstHeaderLine(ByteArrayOutputStream o, DiffEntry.ChangeType type, String oldPath, String newPath) throws IOException {
    }

    @Override
    public void formatIndexLine(OutputStream o, DiffEntry ent) throws IOException {
        super.formatIndexLine(o, ent)
    }

    @Override
    void format(FileHeader head, RawText a, RawText b) throws IOException {
        super.format(head, a, b)
    }

    @Override
    public void writeContextLine(RawText text, int line) throws IOException {
        if(gitTreeWalker.s.writeContextLine1) {
            super.writeContextLine(text, line)
        }
    }

    public byte plus = '+'.charAt(0)
    public byte minus = '+'.charAt(0)
    public byte newline = '\n'.charAt(0)

    @Override
    public void writeAddedLine(RawText text, int line) throws IOException {
        if(gitTreeWalker.s.parseAddRemove){
            OutputStream outputStream2 = getOutputStream()
            OutputStream outputStream3 = new ByteArrayOutputStream()
            outputStream2.write(plus);
            text.writeLine(outputStream3, line);
            byte[] array3 = outputStream3.toByteArray()
            diffEntry1.linesAdded.add(new String(array3))
            outputStream2.write(array3)
            outputStream2.write(newline);
        }else {
            super.writeAddedLine(text, line)
        }
    }



    @Override
    public void writeRemovedLine(RawText text, int line) throws IOException {
        if(gitTreeWalker.s.parseAddRemove){
            OutputStream outputStream2 = getOutputStream()
            OutputStream outputStream3 = new ByteArrayOutputStream()
            outputStream2.write(minus);
            text.writeLine(outputStream3, line);
            byte[] array3 = outputStream3.toByteArray()
            diffEntry1.linesRemoved.add(new String(array3))
            outputStream2.write(array3)
            outputStream2.write(newline);
        }else {
            super.writeRemovedLine(text, line)
        }
    }
}
