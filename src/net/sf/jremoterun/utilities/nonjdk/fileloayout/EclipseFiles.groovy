package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum EclipseFiles implements ChildRedirect,EnumNameProvider{
    plugins('plugins'),
    configStart('configuration/org.eclipse.equinox.simpleconfigurator/bundles.info'),
    eclipseExe('eclipse.exe'),
    eclipseLinux('eclipse'),

    ;

    String customName;
    ExactChildPattern ref;

    EclipseFiles(String customName) {
        this.customName = customName
        ref = new ExactChildPattern (customName)
    }

}
