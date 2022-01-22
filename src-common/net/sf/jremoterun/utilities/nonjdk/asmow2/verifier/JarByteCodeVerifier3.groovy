package net.sf.jremoterun.utilities.nonjdk.asmow2.verifier

import groovy.transform.CompileStatic
import net.sf.jremoterun.URLClassLoaderExt
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.compiler3.CreateGroovyClassLoader
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class JarByteCodeVerifier3 extends JarByteCodeVerifier2{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    void doVerify(){
        adder.add DropshipClasspath.groovy
        adder.add LatestMavenIds.jansi
        adder.add LatestMavenIds.jline2
        adder.add GroovyMavenIds.groovysh
        verifyJar(LatestMavenIds.jline2.m)
        verifyJar(GroovyMavenIds.groovysh.m)
    }

}
