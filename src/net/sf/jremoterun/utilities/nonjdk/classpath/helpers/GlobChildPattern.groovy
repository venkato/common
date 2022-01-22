package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.oro.text.GlobCompiler
import org.apache.oro.text.regex.Pattern;

import java.util.logging.Logger;

/**
 * @see org.apache.oro.text.GlobCompiler
 * @see org.apache.commons.io.filefilter.AgeFileFilter
 */
@CompileStatic
class GlobChildPattern  implements ChildChildPattern {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public FilenameFilter childStartWith;
    //String childStartWithS;

    GlobChildPattern(FilenameFilter childStartWith) {
        this.childStartWith = childStartWith
    }

    GlobChildPattern(String childStartWith) {
        this.childStartWith=new org.apache.oro.io.GlobFilenameFilter(childStartWith);
      //  this.childStartWithS = childStartWith
    }

    @Override
    File resolveChild(File parent) {
        List<File> foundChilds = parent.listFiles(childStartWith).toList()
        int size1 = foundChilds.size()
        if (size1 == 0) {
            throw new IllegalStateException("Child not found ${childStartWith} in ${parent}")
        }
        if (size1 > 1) {
            throw new IllegalStateException("found many ${size1} ${parent} : ${foundChilds.collect {it.getName()}}")
        }
        return foundChilds[0]
    }

    @Override
    String approximatedName() {
        //return childStartWith.toString().replace('*','_')
        throw new UnsupportedOperationException()
    }

//    @Override
//    String toString() {
//        return approximatedName()
//    }

    @Override
    ChildChildPattern childL(String child1) {
        return new ExactChildPPattern(this,child1);
    }

    @Override
    ChildChildPattern childP(ChildPattern child1) {
        return new ExactChildPPattern(this,child1);
    }
}
