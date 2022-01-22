package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum DockerMavenIds implements MavenIdContains, EnumNameProvider {

    java_api,
    java_core,
    java_transport_httpclient5,
    java_transport_jersey,
    java_transport_netty,
    java_transport_okhttp,
    java_transport_zerodep,
    java_transport,
    java,

    ;


    MavenId m;

    DockerMavenIds() {
        String artifactId = 'docker-'+name().replace('_', '-')
        m = new MavenId('com.github.docker-java', artifactId, '3.3.3');
    }

    public static List<DockerMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }


}
