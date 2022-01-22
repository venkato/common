package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum BouncyCastleMavenIdsNew2 implements MavenIdContains, EnumNameProvider {


    bcutil_debug_jdk15to18,
    bcutil_debug_jdk18on,
    bcutil_jdk14,
    bcutil_jdk15to18,
    bcutil_jdk18on,
    bctls_jdk18on,
    bctls_jdk15to18,
    bctls_jdk14,
    bctls_debug_jdk18on,
    bctls_debug_jdk15to18,
    bcprov_jdk18on,
    bcprov_jdk15to18,
    bcprov_jdk14,
    bcprov_ext_jdk18on,
    bcprov_ext_jdk15to18,
    bcprov_ext_jdk14,
    bcprov_ext_debug_jdk18on,
    bcprov_ext_debug_jdk15to18,
    bcprov_debug_jdk18on,
    bcprov_debug_jdk15to18,
    bcpkix_jdk18on,
    bcpkix_jdk15to18,
    bcpkix_jdk14,
    bcpkix_debug_jdk18on,
    bcpg_debug_jdk18on,
    bcpkix_debug_jdk15to18,
    bcpg_jdk18on,
    bcpg_jdk15to18,
    bcpg_jdk14,
    bcpg_debug_jdk15to18,
    bcmail_jdk18on,
    bcmail_jdk15to18,
    bcmail_jdk14,
    bcmail_debug_jdk18on,
    bcmail_debug_jdk15to18,
    bcjmail_jdk18on,
    bcjmail_jdk15to18,
    bcjmail_debug_jdk18on,
    bcjmail_debug_jdk15to18,

    ;
    // war


    MavenId m;

    BouncyCastleMavenIdsNew2() {
        m = new MavenId('org.bouncycastle', name().replace('_', '-'), '1.78.1');
    }

    public static List<BouncyCastleMavenIdsNew2> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

}
