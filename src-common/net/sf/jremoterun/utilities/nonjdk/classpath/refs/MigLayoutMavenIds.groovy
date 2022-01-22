package net.sf.jremoterun.utilities.nonjdk.classpath.refs;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
enum MigLayoutMavenIds implements MavenIdContains, EnumNameProvider{

    examples,
    demo,
    ideutil,
    javafx,
    swt,
    swing,
    core;


    MavenId m;

    MigLayoutMavenIds() {
        // other version are java11+
        m = new MavenId("com.miglayout", 'miglayout-' + name(), '5.3');
    }


    public static List<? extends MavenIdContains> all = (List) values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

}
