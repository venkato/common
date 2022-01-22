package net.sf.jremoterun.utilities.nonjdk.classpath.helpers.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.MavenIdAndRepoCustomSourceContains
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean3

import java.util.logging.Logger

@CompileStatic
class MavenIdAndRepoAndSourceSpecific implements MavenIdAndRepoCustomSourceContains, JavaBean3 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MavenId customMavenId
    public MavenIdAndRepoCustomSourceContains mavenIdAndRepo2;

    MavenIdAndRepoAndSourceSpecific(MavenId customMavenId, MavenIdAndRepoCustomSourceContains mavenIdAndRepo2) {
        this.customMavenId = customMavenId
        this.mavenIdAndRepo2 = mavenIdAndRepo2
        mavenIdAndRepo2 as Enum
    }

    @Override
    MavenIdAndRepo getMavenIdAndRepo() {
        return new MavenIdAndRepo(customMavenId, mavenIdAndRepo2.getMavenIdAndRepo().getRepo())
    }


    @Override
    ToFileRef2 getSourceRedirect() {
        return mavenIdAndRepo2.getSourceRedirect()
    }


    @Override
    String toString() {
        return getMavenIdAndRepo().toString()
    }
}
