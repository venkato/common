package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ExactChildPattern implements ChildChildPattern, Serializable {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static final long serialVersionUID = -6511634932885188423L;

    String child;

    ExactChildPattern(String child) {
        this.child = child
        if (child == null) {
            throw new NullPointerException('child is null')
        }
    }

    @Override
    File resolveChild(File parent) {
        if(child.length()==0){
            return parent
        }
        return new File(parent, child)
    }

    @Override
    String approximatedName() {
        return child
    }

    @Override
    String toString() {
        return child
    }

    @Override
    ExactChildPPattern childL(String child1) {
        return new ExactChildPPattern(this, child1)
    }

    @Override
    ExactChildPPattern childP(ChildPattern child1) {
        return new ExactChildPPattern(this, child1)
    }

    String getJustFileName() {
        String child1 = child;
        if (child1.endsWith('/')) {
            child1 = child1.substring(0, child1.length() - 1)
        }
        return child1.replace('\\', '/').tokenize('/').last()
    }
}
