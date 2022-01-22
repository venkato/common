package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.antutils.JrrAntUtils
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBuilderAddGroovyRuntime
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.jetbrains.jps.cmdline.Launcher
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger

@CompileStatic
class IdeaGroovyCustomizeCompiler extends GenericCompiler{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //public EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    public static List mavenIds = [
            DropshipClasspath.groovy
    ]


    public File baseDir

    void prepare() {
        if(baseDir==null){
            baseDir = InfocationFrameworkStructure.ifDir;
        }
        assert baseDir!=null
        params.needCustomJrrGroovyFieldsAccessors = false
        params.javaVersion = '1.7'
        ClRef clRef = new ClRef('net.sf.jremoterun.utilities.nonjdk.compiler3.ast.ASTTransformationSetPropsMethod');
        params.addInDir IfFrameworkSrcDirs.src_groovycompiler.childL(clRef.getClassPath()+ ClassNameSuffixes.dotgroovy.customName)
//        params.addInDir IfFrameworkSrcDirs.src_groovycompiler.childL('net/sf/jremoterun/utilities/nonjdk/compiler3/ast/ASTTransformationSetPropsMethod.groovy')
//        params.addInDir new File(baseDir,'src-idea-launcher')
        params.outputDir = new File(baseDir, 'build/ideagroovycompiler')
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        adder.addAll(mavenIds)
    }

    File createCustomJar() {
        File fileJar = new File(baseDir, 'build/ideaGroovyCustomize.jar')
        fileJar.delete();
        assert !fileJar.exists()
//        ZipUtil.pack(params.outputDir, fileJar)
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(fileJar,params.outputDir)
        fileJar.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
        return fileJar;
    }

//
//    static File getJarFile(File ideaPath){
//        assert ideaPath.exists()
//        File f = ideaPath.child('plugins/Groovy/lib/groovyCompilerCustomize.jar');
//        assert f.getParentFile().exists()
//        return f;
//    }


}
