package net.sf.jremoterun.utilities.nonjdk.shellcommands

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class GrepCommand extends NativeCommand {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    boolean searchInAllFilesInDir = false;
    boolean ignoreCase = false;
    boolean awareOfNoAscciiSymbols = false;
    String searchText;
    File file;

    GrepCommand(String searchText, File file) {
        super('grep', [])
        assert file.exists()
        this.searchText = searchText
        this.file = file
        process.printBadExitCode = false
    }

    @Override
    void buildCustomArgs() {
        super.buildCustomArgs()
        if (ignoreCase) {
            fullCmd.add('-i')
        }
        if (awareOfNoAscciiSymbols) {
            fullCmd.add('-a')
        }
        if (searchInAllFilesInDir) {
            assert file.isDirectory()
            fullCmd.add('-r')
        }
        fullCmd.add(searchText)
        fullCmd.add(file.getAbsolutePath())

    }
}
