package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileChildLazyRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitSomeRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.compile.stddirs.CompileSpec
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.javacompiler.CompileFileLayout
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import org.junit.Test

import java.util.logging.Logger

@CompileStatic
class AradSocketCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    void doJob(){
        compilerPure.setFromSpec(createSpec())
        compilerPure.compile()
    }

    public static List<LatestMavenIds> mavenIds = [
            LatestMavenIds.slf4jApi,
    ]


    static CompileSpec createSpec2() {
        CompileSpec cs = new CompileSpec()
        cs.addSrc 'src'
        cs.setOut 'bin'
        return cs
    }

    static CompileFileLayout createSpec() {
        CompileFileLayout compileFileLayout= createSpec2().apply2(GitSomeRefs.aradSocket)
        compileFileLayout.javaVersion = '1.8'
        compileFileLayout.libs.addAll(mavenIds.collect {it.m})
        return compileFileLayout
    }


}
