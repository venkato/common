package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum ArtioMavenIds implements MavenIdContains, EnumNameProvider {


    //artio_codecs,
//    aeron_client,
//aeron_driver,
//aeron_samples,
//aeron_system_tests,
//    binary_entrypoint_codecs,
//    binary_entrypoint_impl,
    //binary_entrypoint_system_tests,
    codecs,
    core,
//    ilink3_codecs,
//    ilink3_impl,
//    ilink_system_tests,
    samples,
    session_codecs,
    session_fixt_codecs,
    system_tests,
//sbe_all,
//sbe_benchmarks,
//sbe_samples,
//sbe_tool,
    ;


    MavenId m;

    ArtioMavenIds() {
        String artifact = 'artio-' + name().replace('_', '-')
        m = new MavenId('uk.co.real-logic', artifact, '0.185');
    }


    @Override
    String getCustomName() {
        return m.artifactId
    }

}
