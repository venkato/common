package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.antutils.JrrAntUtils
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.idea.IdeaFilesLayout
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBuilderAddGroovyRuntime
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.jetbrains.jps.cmdline.Launcher

import java.util.logging.Logger

@CompileStatic
class IdeaRunnerBuilderCompiler  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    public static List mavenIds = [
            DropshipClasspath.groovy
    ]


    public File baseDir

    void prepare() {
        if(baseDir==null){
            baseDir = InfocationFrameworkStructure.ifDir;
        }
        assert baseDir!=null
        compilerPure.javaVersion = '1.8'
        compilerPure.annotationProcessorManagerDummy = true
        compilerPure.addInDir IfFrameworkSrcDirs.src_idea_launcher
        compilerPure.adder.addAll mavenIds
        JrrStarterJarRefs2.values().toList().each {
            compilerPure.addClassPathCopy(it.gitOriginRef())
        }
        compilerPure.addClassPathCopy( JrrStarterJarRefs.jrrutilitiesOneJar)
        compilerPure.outputDir = new File(baseDir, 'build/idearunner')
    }


    public static ExactChildPattern launcherChildPattern=new ExactChildPattern('build/jps-launcher.jar')

    File createCustomJar() {
        File fileJar = new File(baseDir, launcherChildPattern.child)
        fileJar.delete();
        assert !fileJar.exists()
//        ZipUtil.pack(compilerPure.outputDir, fileJar)
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(fileJar,compilerPure.outputDir)
        fileJar.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
        return fileJar;
    }

    void updateCompiler(File compilerJar) {
        List<Class> classes3 = (List) [Launcher,]
        classes3.each {
            JrrAntUtils.addClassToZip2(compilerJar, compilerPure.outputDir, it)
        }
        JrrAntUtils.addPackageToZip(compilerJar, compilerPure.outputDir, IdeaBuilderAddGroovyRuntime)
    }



}
