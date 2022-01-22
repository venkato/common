package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ExactChildPPattern implements ChildChildPattern , Serializable{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ChildPattern parent;
    ChildPattern child;

    ExactChildPPattern(ChildPattern parent, String child) {
        this(parent, new ExactChildPattern(child))
    }

    ExactChildPPattern(ChildPattern parent, ChildPattern child) {
        this.parent = parent
        this.child = child
        if(parent==null){
            throw new NullPointerException('parentRef is null')
        }
        if(child==null){
            throw new NullPointerException('child is null')
        }
    }

    @Override
    File resolveChild(File parentF) {
        File f = parent.resolveChild(parentF)
        if(f==null){
            return null
        }
        return child.resolveChild(f)
    }

    @Override
    String approximatedName() {
        return parent.approximatedName() + '/' + child.approximatedName()
    }

    @Override
    String toString() {
        return "${parent}/${child}"
    }

    @Override
    ChildChildPattern childL(String child1) {
        return new ExactChildPPattern(this,child1);
    }

    @Override
    ChildChildPattern childP(ChildPattern child1) {
        return new ExactChildPPattern(this,child1);
    }
}
