package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

/**
 * also add
 * @see net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds#tomcatJasper
 */
@CompileStatic
enum TomcatMavenIds  implements MavenIdContains , EnumNameProvider{

    embed_core,
    embed_el,
    ;

    MavenId m;

    TomcatMavenIds() {
        String artifact = 'tomcat-' + name().replace('_','-')
        // 9.0.21 - failed to start on simple servlet
        m = new MavenId("org.apache.tomcat.embed", artifact, '8.5.23');
    }


    public static List<? extends MavenIdContains> all = (List) values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }


}
