package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum AeronMavenIds implements MavenIdContains, EnumNameProvider {


    aeron_driver,
    aeron_client,
    aeron_archive,
    ;


    MavenId m;

    AeronMavenIds() {
        String artifact = name().replace('_', '-')
        m = new MavenId('io.aeron', artifact, '1.53.3');
    }


    @Override
    String getCustomName() {
        return m.artifactId
    }

}
