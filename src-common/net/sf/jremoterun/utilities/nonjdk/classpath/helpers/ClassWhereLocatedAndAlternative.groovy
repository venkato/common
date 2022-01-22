package net.sf.jremoterun.utilities.nonjdk.classpath.helpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.classpath.ToFileRefSelf

import java.util.logging.Logger;

@CompileStatic
class ClassWhereLocatedAndAlternative implements ClassWhereLocatedAndAlternativeRef {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Class clazz;
    public ToFileRef2 alternative;

    ClassWhereLocatedAndAlternative(Class clazz, ToFileRef2 alternative) {
        this.clazz = clazz
        this.alternative = alternative
    }

    @Override
    File resolveToFile() {
        return MavenDefaultSettings.mavenDefaultSettings.customObjectHandler.resolveToFile(this)
    }

    @Override
    ClassWhereLocatedAndAlternative getRef() {
        return this
    }

    @Override
    String toString() {
        return "${clazz.getName()} ${alternative}"
    }
}
