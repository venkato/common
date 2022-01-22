package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import java.util.logging.Logger

@CompileStatic
class FileToFileRef implements ToFileRef2,ZeroOverheadFileRef,ChildFileLazy, File2FileRefWithSupportI, Serializable {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    private static final long serialVersionUID = 1L;

    File file

    FileToFileRef(File file) {
        this.file = file
        if(file==null){
            throw new NullPointerException('file is null')
        }
    }

    @Override
    File resolveToFile() {
        return file
    }

    @Override
    String toString() {
        return "${file}";
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child);
    }

    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this,child)
    }
}
