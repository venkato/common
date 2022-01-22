package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddToAdderSelf
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import org.apache.tools.ant.DirectoryScanner

import java.util.logging.Logger

/**
 * @see org.apache.tools.ant.DirectoryScanner
 */
@CompileStatic
class AddDirectoryAntWithFiles implements AddToAdderSelf {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public DirectoryScanner scanner;

    AddDirectoryAntWithFiles(DirectoryScanner  excludeFiles) {
        this.scanner = excludeFiles
    }

    @Override
    void addToAdder(AddFilesToClassLoaderCommon adder) {
        List<File> files = scanner.getIncludedFiles().toList().collect { new File(scanner.getBasedir(), it) }
        adder.addAll(files)
    }

}
