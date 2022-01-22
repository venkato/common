package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesFile
import org.apache.tools.ant.DirectoryScanner

import java.util.logging.Logger

@CompileStatic
class AntChildPattern implements ChildChildPattern {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public DirectoryScanner scanner;

    AntChildPattern(DirectoryScanner scanner) {
        this.scanner = scanner
    }

    @Override
    File resolveChild(File parent) {
        JrrUtilitiesFile.checkFileExist(parent)
        scanner.setBasedir(parent)
        scanner.scan()
        List<String> foundChilds = scanner.getIncludedFiles().toList()
        int size1 = foundChilds.size()
        if (size1 == 0) {
            throw new IllegalStateException("Child not found ${scanner} in ${parent}")
        }
        if (size1 > 1) {
            throw new IllegalStateException("found many ${size1} : ${foundChilds}")
        }
        return new File(parent,foundChilds[0])
    }

    @Override
    String approximatedName() {
        throw new UnsupportedOperationException()
    }

//    @Override
//    String toString() {
//        return approximatedName()
//    }

    @Override
    ChildChildPattern childL(String child) {
        return new ExactChildPPattern(this,child)
    }

    @Override
    ChildChildPattern childP(ChildPattern child) {
        return new ExactChildPPattern(this,child)
    }
}
