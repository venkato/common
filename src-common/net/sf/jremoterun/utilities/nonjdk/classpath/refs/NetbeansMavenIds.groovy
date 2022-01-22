package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum NetbeansMavenIds implements MavenIdContains, EnumNameProvider{


    netbeans_swing_outline,
    openide_util,
    openide_util_ui,
    openide_util_lookup,
    ;


    MavenId m;

    NetbeansMavenIds() {
        m = new MavenId('org.netbeans.api','org-'+ name().replace('_', '-'), 'RELEASE180');
    }

    @Override
    String getCustomName() {
        return m.artifactId
    }

}
