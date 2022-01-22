package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ExactChildPattern implements ChildPattern{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    String child;

    ExactChildPattern(String child) {
        this.child = child
    }

    @Override
    File resolveChild(File parent) {
        return new File(parent,child)
    }

    @Override
    String approximatedName() {
        return child
    }

    @Override
    String toString() {
        return child
    }
}
