package net.sf.jremoterun.utilities.nonjdk.classpath.refs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum SshdMavenIds implements MavenIdContains , EnumNameProvider{

    cli,
    spring_sftp,
    contrib,
    git,
    ldap,
    sftp,
    scp,
    netty,
    mina,
    core,
    //openpgp,
    putty,
    common,
//    apache_sshd,
//    sshd,
    ;


    MavenId m;

    SshdMavenIds() {
        String artifact = 'sshd-' + name().replace('_','-')
        // new ClRef('me.bazhenov.groovysh.GroovyShellService');
        // check compatibility with
        m = new MavenId("org.apache.sshd", artifact, '2.13.2');

    }


    public static List<? extends MavenIdContains> all = (List) values().toList()

    public static List<? extends MavenIdContains> getAllWithoutNetty(){
        List<? extends MavenIdContains> list1 = (List) values().toList()
        list1.remove(netty)
        return list1
    }


    @Override
    String getCustomName() {
        return m.artifactId
    }
}
