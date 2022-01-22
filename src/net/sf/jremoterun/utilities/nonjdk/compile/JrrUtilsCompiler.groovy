package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunnerParams
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrLayoutfileName
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterOsSpecificFilesSrc
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds

import java.util.logging.Logger

@CompileStatic
class JrrUtilsCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List mavenIds = [
            LatestMavenIds.log4jOld,
    ]

    public File baseDir

    JrrUtilsCompiler() {
    }

    void prepare() {
        params.javaVersion = '1.7'
        params.needCustomJrrGroovyFieldsAccessors = false
        addDefaulSrc()
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        adder.addAll DropshipClasspath.allLibsWithGroovy
        adder.addGenericEnteries(mavenIds)
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
    }

    @Override
    void compile() {
        log.info "compiling"
        super.compile()
    }

    void addDefaulSrc() {
        params.annotationProcessorManagerDummy = true;
        if(baseDir==null){
            baseDir = net.sf.jremoterun.utilities.nonjdk.compile.JrrBaseTypesCompiler.detectGrHomeS()
        }
        addInDir new File(baseDir, JrrStarterOsSpecificFilesSrc.JrrStarter.getSrcPath().child)
        addInDir new File(baseDir, JrrStarterOsSpecificFilesSrc.JrrUtilities.getSrcPath().child)
        assert params.dirs.each { it.exists() }
        params.outputDir = new File(baseDir, JrrStarterOsSpecificFilesSrc.JrrUtilities.name()+'/build/out2')
        params.outputDir.mkdirs()
    }


    File zipp() {
        File tmpJar = zippToTmpDir()
        File destJar = new File(baseDir, JrrStarterOsSpecificFiles.jrrutilitiesOneJarChildRef.customName)
        if(!destJar.parentFile.exists()){
            assert destJar.parentFile.mkdir()
        }
        tmpJar.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
        FileUtilsJrr.copyFile(tmpJar,destJar)
        log.info "copied to ${destJar}"
        return destJar

    }

    File zippToTmpDir() {
        File tmpJar = new File(baseDir, "${JrrStarterOsSpecificFilesSrc.JrrUtilities.name()}/build/${JrrLayoutfileName.jrrutilities}")
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.acceptRejectFilterJar.endWithIgnoreClasses.add ClassNameSuffixes.dotjava.customName
        archiver.dateOfAllFiles = GroovyCustomCompiler. defaultFileTime
        archiver.createSimple(tmpJar,params.outputDir)
        return tmpJar
    }


}
