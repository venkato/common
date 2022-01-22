package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
class FileChildLazyRef implements ToFileRef2 , ChildFileLazy, File2FileRefWithSupportI,Serializable{
    //private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static final long serialVersionUID = 131242311L;

    ToFileRef2 parentRef;
    ChildPattern child;

    FileChildLazyRef(ToFileRef2 parentRef, ChildPattern child) {
        this.parentRef = parentRef
        this.child = child
        if(parentRef==null){
            throw new NullPointerException('parentRef is null')
        }
        if(child==null){
            throw new NullPointerException('child is null')
        }
    }

    FileChildLazyRef(ToFileRef2 parentRef1, String child1) {
        if(parentRef1==null){
            throw new NullPointerException("parentRef is null for child=${child1}")
        }
        if(child1==null){
            throw new NullPointerException("child is null")
        }
        this.parentRef = parentRef1
        this.child = new ExactChildPattern(child1)
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
        return "${parentRef}/${child}"
    }



}
