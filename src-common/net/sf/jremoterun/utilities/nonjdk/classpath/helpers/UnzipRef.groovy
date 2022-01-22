package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2;

import java.util.logging.Logger;

@CompileStatic
class UnzipRef implements ToFileRef2,ChildFileLazy, File2FileRefWithSupportI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public ToFileRef2 refToZip;

    UnzipRef(ToFileRef2 refToZip) {
        this.refToZip = refToZip
        if(refToZip==null){
            throw new NullPointerException('ref is null')
        }
    }

    @Override
    File resolveToFile() {
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        return handler.resolveToFile(this)
    }

    @Override
    FileChildLazyRef childL(String child) {
        return new FileChildLazyRef(this,child)
    }


    @Override
    FileChildLazyRef childP(ChildPattern child) {
        return new FileChildLazyRef(this,child)
    }

}
