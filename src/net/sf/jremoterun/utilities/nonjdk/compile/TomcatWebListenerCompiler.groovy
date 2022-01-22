package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.TomcatMavenIds
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.junit.Test

import java.util.logging.Logger



@CompileStatic
class TomcatWebListenerCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File baseDir
    public EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();


    void prepare() {
        if (baseDir == null) {
            baseDir = GitSomeRefs.commonUtil.resolveToFile()
        }
        //compilerPure.adder.add(new CopyOnUsage(JrrStarterJarRefs2.jrrassist.gitOriginRef()))

//        client.adder.addAll GroovyMavenIds.all
        compilerPure.annotationProcessorManagerDummy = true
        compilerPure.adder.add TomcatMavenIds.embed_core
        compilerPure.javaVersion = '1.7'
        compilerPure.addInDir IfFrameworkSrcDirs.src.childL(new ClRef('net.sf.jremoterun.utilities.nonjdk.net.tomcat.weblistener.JrrServletContextListener').getClassPath()+ ClassNameSuffixes.dotjava.customName)
        compilerPure.addInDir IfFrameworkSrcDirs.src.childL('net/sf/jremoterun/utilities/nonjdk/net/tomcat/jmxweblistener/')
        compilerPure.adder.add JrrStarterJarRefs2.jremoterun
        compilerPure.outputDir = new File(baseDir, 'build/weblistener')
        compilerPure.outputDir.mkdirs()
    }

    public File dist

    void zip() {
//        FileUtils.copyDirectory(handler.resolveRef(gitRefResources),compilerPure.outputDir)
        dist = new File(baseDir, 'build/weblistener.jar');
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        dist.delete()
        assert !dist.exists()
//        ZipUtil.pack(compilerPure.outputDir, dist)
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(dist,compilerPure.outputDir)
        dist.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
    }


    @Test
    void all() {
//        prepare()
        prepare()
        compilerPure.compile()
        zip()
    }


}
