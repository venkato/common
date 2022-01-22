package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum NettySslNativeMavenIds implements MavenIdContains , EnumNameProvider{

    tcnative_classes,
    // no files:
    //tcnative_boringssl_static,

    //tcnative,
    ;


    MavenId m;

    NettySslNativeMavenIds() {
        m = new MavenId('io.netty','netty-'+ name().replace('_', '-'), '2.0.62.Final');
    }

    public static List<NettySslNativeMavenIds> allEnums = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

}
