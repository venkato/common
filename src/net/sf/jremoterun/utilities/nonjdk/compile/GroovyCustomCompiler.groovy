package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.antutils.JrrAntUtils
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.log.FileExtentionClass
import net.sf.jremoterun.utilities.nonjdk.log.JdkLoggerExtentionClass
import org.junit.Test

import java.nio.file.attribute.FileTime
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

@CompileStatic
class GroovyCustomCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Date defaultDate = new SimpleDateFormat('yyyy-MM-dd').parse('2008-03-03')
    public static FileTime defaultFileTime = FileTime.from(defaultDate.getTime(), TimeUnit.MILLISECONDS)

    public static ExactChildPattern groovyCustomSrc = new ExactChildPattern('groovycustom/src')

    File baseDir


    void prepare() {
        if (baseDir == null) {
            baseDir = client.ifDir
        }
        params.javaVersion = '1.6'
        params.addInDir new File(baseDir, groovyCustomSrc.child)
        params.addInDir new File(baseDir, IfFrameworkSrcDirs.src_logger_ext_methods.dirName)
        params.outputDir = new File(baseDir, "build/groovycustom1")
    }

    File dest

    File zip() {
        FileUtilsJrr.copyDirectory(new File(baseDir, IfFrameworkResourceDirs.resources_groovy.ref.child), params.outputDir)
        dest = new File(baseDir, "build/groovy_custom.jar")
        dest.delete()
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(dest,params.outputDir)
        dest.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
        return dest
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {

    }

    @Test
    @Override
    void all2() {
        super.all2()
        zip()
    }


    void updateCompilerDefaultDir() {
        File child = JrrStarterJarRefs2.groovy_custom.gitOriginRef().resolveToFile()
        assert child.exists()
        updateCompiler(child)
        FileUtilsJrr.copyFile(child, JrrStarterJarRefs2.groovy_custom.resolveToFile())
    }

    void updateCompiler(File compilerJar) {

//        List<Class> classes3 = (List) [JdkLoggerExtentionClass, FileExtentionClass, CompileStatic,]
        List<Class> classes3 = (List) [JdkLoggerExtentionClass, FileExtentionClass, org.codehaus.groovy.runtime.metaclass.MissingMethodExceptionNoStack, org.codehaus.groovy.runtime.typehandling.ShortTypeHandlingClassCast, org.codehaus.groovy.tools.javac.JavacCompilerFactoryImpl,]
        classes3.each {
//            JrrAntUtils.addClassToZip2(compilerJar, params.outputDir, it)
            JrrAntUtils.addPackageToZip(compilerJar, params.outputDir, it)
        }
//        log.info "updating package : ${JrrStaticCompilationVisitorDel.package.name}"
//        JrrAntUtils.addPackageToZip(compilerJar, params.outputDir, JrrStaticCompilationVisitorDel)

    }
}
