package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum HttpClient5CoreApacheMavenIds implements MavenIdContains, EnumNameProvider {


httpcore5_testing,
httpcore5_reactive,
httpcore5_h2,
httpcore5,
    ;


    MavenId m;

    HttpClient5CoreApacheMavenIds() {
        String artifactId = name().replace('_', '-')
        m = new MavenId('org.apache.httpcomponents.core5', artifactId, '5.3');
    }

    public static List<HttpClient5CoreApacheMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }


}
