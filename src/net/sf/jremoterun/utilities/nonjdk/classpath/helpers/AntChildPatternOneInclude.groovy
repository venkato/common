package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.tools.ant.DirectoryScanner;

import java.util.logging.Logger;

@CompileStatic
class AntChildPatternOneInclude extends DirectoryScanner implements ChildChildPattern {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String dirPatternIncludeJrr;

    AntChildPatternOneInclude(String dirInclude1) {
        this.dirPatternIncludeJrr = dirInclude1
        String[] includes3 = [dirInclude1]
        setIncludes(includes3)
    }

    @Override
    String toString() {
        return dirPatternIncludeJrr
    }


    @Override
    File resolveChild(File parent) {
        File f =  AntChildPattern.resolveChildS(this, parent, true)
//        if(f ==null){
//            throw new IllegalStateException("Child not found ${dirPatternIncludeJrr} in ${parent}")
//        }
        return f
    }

    public static String questionToChar = '_'
    public static String asterixToChar = '__'

    @Override
    String approximatedName() {
        return dirPatternIncludeJrr.replace('*',asterixToChar).replace('?', questionToChar)
    }

    @Override
    ChildChildPattern childL(String child) {
        return new ExactChildPPattern(this, child)
    }

    @Override
    ChildChildPattern childP(ChildPattern child) {
        return new ExactChildPPattern(this, child)
    }
}
