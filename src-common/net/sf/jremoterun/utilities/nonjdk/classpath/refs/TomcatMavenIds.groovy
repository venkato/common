package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

/**
 * also add
 * @see net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds#tomcatJasper
 */
@CompileStatic
enum TomcatMavenIds implements MavenIdContains , EnumNameProvider{

    servlet_api
    ;

    MavenId m;

    TomcatMavenIds() {
        String artifact = 'tomcat-' + name().replace('_','-')
        // 9.0.21 - failed to start on simple servlet
        m = new MavenId("org.apache.tomcat", artifact, TomcatDiffMavenId.tomcat9.version);
    }


    public static List<? extends MavenIdContains> all = (List) values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }


}
