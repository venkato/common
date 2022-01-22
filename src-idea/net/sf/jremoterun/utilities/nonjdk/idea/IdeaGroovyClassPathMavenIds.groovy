package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds;

import java.util.logging.Logger;

@CompileStatic
class IdeaGroovyClassPathMavenIds {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<? extends MavenIdContains> mavenIds = [
            DropshipClasspath.groovy
            , GroovyMavenIds.ant
            , GroovyMavenIds.json
            , GroovyMavenIds.xml
            , GroovyMavenIds.nio
            , GroovyMavenIds.groovysh
            , GroovyMavenIds.yaml
            , GroovyMavenIds.datetime
            , GroovyMavenIds.dateutil
            , LatestMavenIds.jansi
            , LatestMavenIds.jline2

    ]


}
