package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GroovyMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import org.junit.Test

import java.util.logging.Logger

@CompileStatic
class RstaCoreCompiler  extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static CustomObjectHandlerImpl handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler as CustomObjectHandlerImpl

    public static List<? extends MavenIdContains> mavenIds = [
//            LatestMavenIds.rsyntaxtextarea,
            LatestMavenIds.log4jOld,
            LatestMavenIds.rstaui,
            GitMavenIds.jgit,
            LatestMavenIds.jsoup,
            LatestMavenIds.junit,
            CustObjMavenIds.commnonsLang,
            LatestMavenIds.jodaTime,
            LatestMavenIds.commonsCodec,
            CustObjMavenIds.commonsIo,
            DropshipClasspath.ivyMavenId,
    ]

//    void prepare() {
//
//    }

    void prepare() {


        params.printWarning = false
        params.javaVersion = '1.6'
        params.addInDir new File(client.ifDir,'src-rsta-core')
        params.addInDir new File(client.ifDir,'src-common')
        params.outputDir = new File(client.ifDir,'build/rsta-core')
        params.outputDir.mkdirs()
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        adder.addGenericEnteries(mavenIds)
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)

        adder.addAll GroovyMavenIds.all
        adder.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rsta()
        adder.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaAutoCompetion()
//        adder.addFileWhereClassLocated(AddFileWithSources)
//        adder.add(JrrClassLocationRefs.SetConsoleOut21)
    }

    File dist

    void zip() {
//        FileUtils.copyDirectory(handler.resolveRef(gitRefResources),params.outputDir)
        dist= new File(client.ifDir,'dist/rsta-core.jar');
        dist.parentFile.mkdir()
        assert dist.parentFile.exists()
        dist.delete()
        assert !dist.exists()
//        ZipUtil.pack(params.outputDir,dist)
        JrrJarArchiver archiver = new JrrJarArchiver(true)
        archiver.dateOfAllFiles = GroovyCustomCompiler.defaultFileTime
        archiver.createSimple(dist,params.outputDir)
        dist.setLastModified(GroovyCustomCompiler. defaultDate.getTime())
    }


    @Test
    void all() {
//        prepare()
        prepare()
        addClassPath(client.adder)
        compile()
        zip()
    }


}
