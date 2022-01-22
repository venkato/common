package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.CutomJarAdd
import net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.IfframeworkClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.SshConsoleClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew.MavenIdsCollector
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.SshdMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs2.CutomJarAdd1
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.Log4j2MavenIds

import java.util.logging.Logger

@CompileStatic
class SshConsoleCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Deprecated
    public static List<? extends MavenIdContains> mavenIds = [
            LatestMavenIds.logbackClassic,
            LatestMavenIds.logbackCore,
            LatestMavenIds.guavaMavenIdNew,
            LatestMavenIds.junit,
            SshdMavenIds.core,
            RstaJars.rsyntaxtextarea(),
            net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaui(),
            net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaAutoCompetion(),
//            LatestMavenIds.rstaLangSupport,
            LatestMavenIds.commonsCollection,
            //Log4j2MavenIds.slf4j_impl,
            LatestMavenIds.jansi,
            LatestMavenIds.jline2,
//            LatestMavenIds.jline3,
            CustObjMavenIds.commonsIo,
    ]

    SshConsoleCompiler() {
    }

    void addIdw() {
        client.adder.add CutomJarAdd1.downloadIdw()
    }

    File baseDir

    void prepare() {
        if(baseDir==null){
            baseDir = client.ifDir
        }
        params.printWarning = false
        params.outputDir = baseDir.child('build/sshbuild')
        params.outputDir.mkdirs()

        params.addInDir GitReferences.jnaplatext.resolveToFile()
//        client.adderParent.addM LatestMavenIds.junit

        List<String> dirs = InfocationFrameworkStructure.dirs2
        dirs.each {
            params.addInDir baseDir.child(it)
        }
        params.addTestClassLoaded org.fusesource.jansi.AnsiRenderWriter

        addIdw()
        params.javaVersion = '1.6'

//        log.info "finished"
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
        File toolsJar = mcu.getToolsJarFile()
        if(toolsJar.exists()) {
            client.adder.add toolsJar
        }
        adder.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rsyntaxtextarea()
//        adder.addAll mavenIds
//        if(IfFrameworkCompiler.mavenIdsCollector==null){
//            IfFrameworkCompiler.mavenIdsCollector= new MavenIdsCollector()
//        }
        adder.addAll net.sf.jremoterun.utilities.nonjdk.compile.MavenIdCollectorFactory.factory1.getCollector().loadMavenIds2(new SshConsoleClasspath())
        adder.addAll net.sf.jremoterun.utilities.nonjdk.compile.MavenIdCollectorFactory.factory1.getCollector().loadMavenIds2(new IfframeworkClasspath())
//        adder.addAll GroovyMavenIds.all
//        adder.addAll DropshipClasspath.allLibsWithGroovy
//        adder.addAll NexusSearchMavenIds.all
//        adder.addAll LatestMavenIds.usefulMavenIdSafeToUseLatest
        adder.add JeditTermCompilerConsoleCompiler.compileIfNeededS()
        CutomJarAdd.addCustom(adder)
    }

    File zipp() {
        File zipFile = baseDir.child('build/sshConsole.jar')
        zipFile.delete()
        assert !zipFile.exists()
//        ZipUtil.pack(params.outputDir, zipFile)
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(zipFile,params.outputDir)
        zipFile.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
        return zipFile;
    }


}
