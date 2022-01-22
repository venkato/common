package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithAndEndsChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum EclipsePluginFileLayout implements EnumNameProvider, ChildRedirect {


org_tigris_subversion_clientadapter,
org_tigris_subversion_subclipse_ui,
org_tigris_subversion_subclipse_core,
    ;

    ChildChildPattern ref;

    String customName;

    EclipsePluginFileLayout() {
        customName = name().replace('_','.')+'_'
        ref=EclipseFiles.plugins.ref.childP(new StartWithAndEndsChildPattern(customName,'.jar'));
    }

    public static List<EclipsePluginFileLayout> svnPlugins = values().toList()

}
