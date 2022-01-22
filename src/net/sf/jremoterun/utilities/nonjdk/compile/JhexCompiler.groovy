package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure

import java.util.logging.Logger

@CompileStatic
class JhexCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    List mavenIds = [
            LatestMavenIds.guavaMavenIdNew,
    ]

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();


    void prepare() {
//        params.printWarning = false
        compilerPure.outputDir = GitSomeRefs.jhexViewer.childL('build/b1').resolveToFile()
        compilerPure.outputDir.mkdirs()

//        addInDir GitReferences.jhexViewer
        compilerPure.addInDir GitSomeRefs.jhexViewer.childL('src/main/java/com/google/security/zynamics/zylib/gui/JHexPanel/')
        compilerPure.addInDir GitSomeRefs.jhexViewer.childL('src/main/java/com/google/security/zynamics/zylib/gui/JCaret/')
        compilerPure.addInDir GitSomeRefs.jhexViewer.childL('src/main/java/com/google/security/zynamics/zylib/general/Convert.java')
        compilerPure.addInDir GitSomeRefs.jhexViewer.childL('src/main/java/com/google/security/zynamics/zylib/gui/GuiHelper.java')

        compilerPure.adder.addAll mavenIds
        compilerPure.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        compilerPure.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        compilerPure.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
        compilerPure.javaVersion = '1.7'
    }


}
