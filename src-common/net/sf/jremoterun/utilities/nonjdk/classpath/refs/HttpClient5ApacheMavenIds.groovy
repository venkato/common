package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum HttpClient5ApacheMavenIds implements MavenIdContains, EnumNameProvider {

    httpclient5_testing,
    httpclient5_win,
    httpclient5_cache,
    httpclient5_fluent,
    httpclient5,
    ;


    MavenId m;

    HttpClient5ApacheMavenIds() {
        String artifactId = name().replace('_', '-')
        m = new MavenId('org.apache.httpcomponents.client5', artifactId, '5.2.3');
    }

    public static List<HttpClient5ApacheMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }

}
