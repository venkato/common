package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.ioutils.filerotate.BackupDirFactory
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.junit.Test

import java.util.logging.Logger

@CompileStatic
class JnaCoreCompiler  {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    File baseDir
    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();


    void prepare() {
        if (baseDir == null) {
            baseDir = GitSomeRefs.jnaRepo.resolveToFile()
        }
        compilerPure.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        compilerPure.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())

//        client.adder.addAll GroovyMavenIds.all
        compilerPure.javaVersion = '1.5'
        compilerPure.addInDir new File(baseDir, GitReferences.jnaCore.src)
        compilerPure.outputDir = new File(baseDir, 'build/classes2')
        compilerPure.outputDir.mkdirs()
    }

    File dist

    void zip() {
//        FileUtils.copyDirectory(handler.resolveRef(gitRefResources),compilerPure.outputDir)
        dist = new File(baseDir, GitReferences.jnaCore.pathInRepo);
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        if(dist.exists()){
            if(BackupDirFactory.backupDirFactory!=null){
                BackupDirFactory.backupDirFactory.backupSimple(dist)
            }
        }

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
