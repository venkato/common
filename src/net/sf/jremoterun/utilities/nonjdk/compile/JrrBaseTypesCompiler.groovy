package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesSrc
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.junit.Test

import java.util.logging.Logger

@CompileStatic
class JrrBaseTypesCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File baseDir
    public EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    static File detectGrHomeS() {
        if (GroovyMethodRunnerParams.gmrp == null) {
            return GitSomeRefs.starter.resolveToFile()
        }
        File testFile = new File(GroovyMethodRunnerParams.gmrpn.grHome, JrrStarterOsSpecificFilesSrc.jrrbasetypes.getSrcPath().child)
        if (testFile.exists()) {
            return GroovyMethodRunnerParams.gmrpn.grHome
        }
        return GitSomeRefs.starter.resolveToFile()
    }
    File detectGrHome() {
       return  detectGrHomeS()

    }

    void prepare() {
        compilerPure.javaVersion = '1.5'
        compilerPure.annotationProcessorManagerDummy = true;
        if (baseDir == null) {
            baseDir = detectGrHome()
        }
        compilerPure.addInDir new File(baseDir, JrrStarterOsSpecificFilesSrc.jrrbasetypes.getSrcPath().child)
        compilerPure.outputDir = new File(baseDir, JrrStarterOsSpecificFilesSrc.jrrbasetypes.name() + '/build/out2')
        compilerPure.outputDir.mkdirs()
    }


    void zip(File destJar) {
        File tmpJar = compilerPure.outputDir.parentFile.child(JrrStarterOsSpecificFilesSrc.jrrbasetypes.name() + '.jar')
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.acceptRejectFilterJar.endWithIgnoreClasses.add ClassNameSuffixes.dotjava.customName
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(tmpJar, compilerPure.outputDir)
        tmpJar.setLastModified(GroovyCustomCompiler.defaultDate.getTime())
        FileUtilsJrr.copyFile(tmpJar, destJar)
        log.info "copied to ${destJar}"
    }

    File all() {
        prepare()
        compilerPure.compile()
        //zipp()
        return zippToTmpDir()
    }


    File zipp() {
        File tmpJar = zippToTmpDir()
        File destJar = JrrStarterJarRefs2.jrrbasetypes.gitOriginRef().resolveToFile()
        if (!destJar.parentFile.exists()) {
            assert destJar.parentFile.mkdir()
        }
        tmpJar.setLastModified(GroovyCustomCompiler.defaultDate.getTime())
        FileUtilsJrr.copyFile(tmpJar, destJar)
        destJar.setLastModified(GroovyCustomCompiler.defaultDate.getTime())
        log.info "copied to ${destJar}"
        return destJar

    }

    File zippToTmpDir() {
        File tmpJar = new File(baseDir, JrrStarterOsSpecificFilesSrc.jrrbasetypes.name() + '/build/' + JrrStarterJarRefs2.jrrbasetypes.name() + '.jar')
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.acceptRejectFilterJar.endWithIgnoreClasses.add ClassNameSuffixes.dotjava.customName
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(tmpJar, compilerPure.outputDir)
        return tmpJar
    }


}
