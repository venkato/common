package net.sf.jremoterun.utilities.nonjdk.asmow2.verifier

import groovy.transform.CompileStatic
import net.sf.jremoterun.URLClassLoaderExt;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.tester.GroovyCompilationWorksTester
import net.sf.jremoterun.utilities.nonjdk.compiler3.CreateGroovyClassLoader
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger;

@CompileStatic
class JarByteCodeVerifier2 extends JarByteCodeVerifier{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public AddFilesToUrlClassLoaderGroovy adder;

    public URLClassLoader classLoader1;


    JarByteCodeVerifier2() {
        this(new URLClassLoaderExt(new URL[0],  CreateGroovyClassLoader.findExtClassLoader()))
    }

    JarByteCodeVerifier2(URLClassLoader classLoader1) {
        super(classLoader1)
        this.classLoader1 = classLoader1
        adder = new AddFilesToUrlClassLoaderGroovy(classLoader1)
    }



    void verifyJar(ToFileRef2 f ){
        verifyJar (f.resolveToFile())
    }

    void verifyJar(File f ){
        adder.add f
        ZipUtil.iterate(f, this)
        assert foundFiled
    }

}
