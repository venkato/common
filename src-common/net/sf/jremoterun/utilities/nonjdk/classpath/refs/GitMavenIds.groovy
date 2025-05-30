package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum GitMavenIds implements MavenIdContains, EnumNameProvider {

    jgit_pgm,
    jgit_ui,
//    jgit_ssh_apache, // this requires sshd:2.2+
    jgit_ssh_jsch,
    jgit_lfs_server,
    jgit_lfs,
    jgit_junit_ssh,
    jgit_junit_http,
    jgit_junit,
    jgit_http_server,
    jgit_http_apache,
    jgit_gpg_bc,
    jgit_archive,
    jgit_ant,
    jgit,
    ;


    MavenId m;

    GitMavenIds() {
        String artifactId = 'org.eclipse.'+name().replace('_', '.')
        // 6+ need java9+
        m = new MavenId('org.eclipse.jgit', artifactId, '5.13.0.202109080827-r');
    }

    public static List<GitMavenIds> all = values().toList()


    @Override
    String getCustomName() {
        return m.artifactId
    }


}
