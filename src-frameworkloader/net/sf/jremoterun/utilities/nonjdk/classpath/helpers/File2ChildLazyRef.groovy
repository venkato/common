package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ToFileRef2

@CompileStatic
class File2ChildLazyRef implements ToFileRef2, ChildFileLazy, File2FileRefWithSupportI, Serializable {


    private static final long serialVersionUID = 1318678311L;

    public File parentFile;
    public ChildChildPattern child;

    File2ChildLazyRef(File parentRef, ChildChildPattern child) {
        this.parentFile = parentRef
        this.child = child
        if (parentRef == null) {
            throw new NullPointerException('parentRef is null')
        }
        if (child == null) {
            throw new NullPointerException('child is null')
        }
    }

    @Override
    File resolveToFile() {
        return child.resolveChild(parentFile)
    }

    @Override
    File2ChildLazyRef childL(String child1) {
        return new File2ChildLazyRef(this.parentFile, child.childL(child1))
    }

    @Override
    File2ChildLazyRef childP(ChildPattern child1) {
        return new File2ChildLazyRef(this.parentFile, child.childP(child1))
    }

    @Override
    String toString() {
        return "${parentFile.getAbsoluteFile()}/${child}"
    }


}
