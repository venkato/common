package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.junit.Test

import java.util.logging.Logger

@CompileStatic
class JavaPropReadWriteCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File baseDir
    public EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();


    void prepare() {
        compilerPure.annotationProcessorManagerDummy = true
        compilerPure.javaVersion = '1.6'
        compilerPure.addInDir new File(baseDir, IfFrameworkSrcDirs.src_propssimplereadwrite.dirName)
        compilerPure.outputDir = new File(baseDir, 'build/' + IfFrameworkSrcDirs.src_propssimplereadwrite.name())
        compilerPure.outputDir.mkdirs()
    }


    void zip(File dist) {
//        if(dist==null) {
//            dist = new File(baseDir, GitReferences.jnaJvmti.pathInRepo);
//        }
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        dist.delete()
        assert !dist.exists()
//        ZipUtil.pack(params.outputDir, dist)
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(dist,compilerPure.outputDir)
        dist.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
    }


    void all() {
        prepare()
        compilerPure.compile()
    }


}
