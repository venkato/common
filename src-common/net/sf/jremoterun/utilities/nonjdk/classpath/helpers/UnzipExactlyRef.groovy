package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import java.util.logging.Logger

@CompileStatic
class UnzipExactlyRef extends  UnzipRef implements ToFileRef2,ChildFileLazy, File2FileRefWithSupportI{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    UnzipExactlyRef(ToFileRef2 refToZip) {
        super(refToZip)
    }


}
