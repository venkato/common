package net.sf.jremoterun.utilities.nonjdk.classpath.helpers.store

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean3;

import java.util.logging.Logger;

@CompileStatic
class MavenIdAndRepoSpecific implements MavenIdAndRepoContains , JavaBean3{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MavenId customMavenId
    public MavenIdAndRepoContains mavenIdAndRepo2;

    MavenIdAndRepoSpecific(MavenId customMavenId, MavenIdAndRepoContains mavenIdAndRepo2) {
        this.customMavenId = customMavenId
        this.mavenIdAndRepo2 = mavenIdAndRepo2
        mavenIdAndRepo2 as Enum
    }

    @Override
    MavenIdAndRepo getMavenIdAndRepo() {
        return new MavenIdAndRepo(customMavenId, mavenIdAndRepo2.getMavenIdAndRepo().getRepo())
    }

    @Override
    String toString() {
        return getMavenIdAndRepo().toString()
    }
}
