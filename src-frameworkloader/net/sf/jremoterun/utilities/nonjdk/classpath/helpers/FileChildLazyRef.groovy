package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
class FileChildLazyRef implements ToFileRef2 , ChildFileLazy, File2FileRefWithSupportI{
    //private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ToFileRef2 parentRef;
    ChildPattern child;

    FileChildLazyRef(ToFileRef2 parentRef, ChildPattern child) {
        this.parentRef = parentRef
        this.child = child
    }
    FileChildLazyRef(ToFileRef2 parentRef, String child) {
        this.parentRef = parentRef
        this.child = new ExactChildPattern(child)
    }

    @Override
    File resolveToFile() {
        File parentFile = parentRef.resolveToFile()
        return child.resolveChild(parentFile)
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this,child)
    }

    @Override
    String toString() {
        return "${parentRef} .child( ${child.approximatedName()} )"
    }
}
