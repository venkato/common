package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum JungGraphSwing implements MavenIdContains, EnumNameProvider {
    samples,
    visualization,
    io,
    algorithms,
    graph_impl,
    api,
    ;


    MavenId m;

    JungGraphSwing() {
        String artifactId = 'jung-' + name().replace('_', '-')
        m = new MavenId('net.sf.jung', artifactId, '2.1.1');
    }

    public static List<JungGraphSwing> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }



}
