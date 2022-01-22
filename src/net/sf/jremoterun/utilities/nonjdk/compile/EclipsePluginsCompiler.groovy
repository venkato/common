package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.classpath.CutomJarAdd
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.CustObjMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2

import java.util.logging.Logger

@CompileStatic
class EclipsePluginsCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List mavenIds = [
            CustObjMavenIds.eclipseJavaCompiler,
            CustObjMavenIds.eclipseJavaAstParser,
            LatestMavenIds.jodaTime,
    ]

    File baseDir

    EclipsePluginsCompiler() {
    }

    void prepare() {
        if (baseDir == null) {
            baseDir = client.ifDir
        }
        params.printWarning = false
        params.javaVersion = '1.8'
        params.outputDir = new File(baseDir, 'build/eclipsebuild')
        params.outputDir.mkdirs()

        addInDir IfFrameworkSrcDirs.src_common
        addInDir IfFrameworkSrcDirs.src_eclipse_starter
        addInDir IfFrameworkSrcDirs.src_eclipse_showcmd
        addInDir GitReferences.eclipseFileCompiltion

        params.addTestClassLoaded(JrrClassUtils)
        params.addTestClassLoaded(JdkLogFormatter)
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
//        client.adder.addFileWhereClassLocated2(JdkLogFormatter, JrrStarterProjects.JrrStarter)
//        client.adder.addFileWhereClassLocated2(JrrClassUtils, JrrStarterProjects.JrrUtilities)
//        client.adder.addFileWhereClassLocated2(JavaVMClient, JrrStarterJarRefs2.jrrassist)
        adder.addAll DropshipClasspath.allLibsWithGroovy
        adder.addAll mavenIds
        adder.add JeditTermCompilerConsoleCompiler.compileIfNeededS()
        CutomJarAdd.addCustom(adder)

    }
}
