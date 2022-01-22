package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum MavenIverseMavenIds implements MavenIdContains, EnumNameProvider {


    standalone_static_uber,
    standalone_static,
    standalone_sisu_uber,
    standalone_sisu,
    standalone_shared,
    embedded_maven,
    ;


    ;
    // war


    MavenId m;

    MavenIverseMavenIds() {
        m = new MavenId('eu.maveniverse.maven.mima.runtime', name().replace('_', '-'), '2.4.15');
    }

    public static List<MavenIverseMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }






}
