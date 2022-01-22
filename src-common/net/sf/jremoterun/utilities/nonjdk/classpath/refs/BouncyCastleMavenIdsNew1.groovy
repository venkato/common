package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum BouncyCastleMavenIdsNew1 implements MavenIdContains, EnumNameProvider {


    bcutil_lts8on,
    bctls_lts8on,
    bcprov_lts8on,
    bcpkix_lts8on,
    bcpg_lts8on,
    bcmail_lts8on,
    bcjmail_lts8on,


    ;
    // war


    MavenId m;

    BouncyCastleMavenIdsNew1() {
        m = new MavenId('org.bouncycastle', name().replace('_', '-'), '2.73.6');
    }

    public static List<BouncyCastleMavenIdsNew1> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

}
