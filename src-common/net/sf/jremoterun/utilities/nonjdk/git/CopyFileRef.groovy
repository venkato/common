package net.sf.jremoterun.utilities.nonjdk.git

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import java.util.logging.Logger

@CompileStatic
class CopyFileRef implements ToFileRef2{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ToFileRef2 nested;

    CopyFileRef(ToFileRef2 nested) {
        this.nested = nested
    }

    @Override
    File resolveToFile() {
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        return handler.resolveToFile(this)

    }
}
