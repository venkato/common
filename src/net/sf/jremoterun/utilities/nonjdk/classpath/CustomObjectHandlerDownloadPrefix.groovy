package net.sf.jremoterun.utilities.nonjdk.classpath

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumCustomNameResolver
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum CustomObjectHandlerDownloadPrefix implements EnumNameProvider{

    tmp,
    android,
    sf,
    replica,
    svn,
    maven,
    unzip,
    copytmp,
    ;

    @Override
    String getCustomName() {
        return name()
    }
}
