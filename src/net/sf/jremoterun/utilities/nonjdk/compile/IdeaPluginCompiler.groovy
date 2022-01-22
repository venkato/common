package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.idea.IdeaFilesLayout

import java.util.logging.Logger

@CompileStatic
class IdeaPluginCompiler extends IfFrameworkCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    IdeaPluginCompiler() {
    }

    static void addIdeaStuff(File ideaDir, AddFilesToClassLoaderCommon adder) {
        adder.addAllJarsInDir  IdeaFilesLayout.groovyLib.ref.resolveChild(ideaDir);
        adder.addAllJarsInDir  IdeaFilesLayout.lib.ref.resolveChild(ideaDir);
    }

    void compile3(File ideaDir) {
        prepare()
        prepare2(ideaDir)
        compile()
        postCompileStep()
    }


    File getCompiledDir() {
        params.outputDir = new File(client.ifDir, 'build/ideaAndIf')
    }


    static void addIdeaCp(AddFilesToClassLoaderCommon adder,File ideaDir){
        assert ideaDir.exists()
//        adder.addAllJarsInDir new File(ideaDir, "plugins/git4idea/lib/")
//        adder.add new File(ideaDir, "plugins/github/lib/github.jar")
        adder.add IdeaFilesLayout.terminal.ref.resolveChild(ideaDir)
        adder.addAllJarsInDir  IdeaFilesLayout.lib.ref.resolveChild(ideaDir)
        adder.addAllJarsInDir  IdeaFilesLayout.groovyLib.ref.resolveChild(ideaDir);
        adder.addAllJarsInDirAndSubdirs  IdeaFilesLayout.kotlinLib.ref.resolveChild(ideaDir)
        File javaPlauginDir =  IdeaFilesLayout.pluginJavaLib.ref.resolveChild(ideaDir);
        if(javaPlauginDir.exists()){
            adder.addAllJarsInDir(javaPlauginDir)
        }
    }


    void prepare2(File ideaDir) {
        assert ideaDir.exists()
        client.adder.addAllJarsInDir  IdeaFilesLayout.groovyLib.ref.resolveChild(ideaDir);
        client.adder.addAllJarsInDir  IdeaFilesLayout.lib.ref.resolveChild(ideaDir);
        params.outputDir = getCompiledDir()
        params.outputDir.mkdirs()
        params.javaVersion = '1.8'
        if (InfocationFrameworkStructure.ifDir == null) {
            InfocationFrameworkStructure.ifDir = client.ifDir
            InfocationFrameworkStructure.ifCommonDir = client.ifDir
        }
        IfFrameworkSrcDirs.dir2.each { addInDir(it) }
        IfFrameworkSrcDirs.idea.each { addInDir(it) }

    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        super.addClassPath(adder)
        //client.adder.add JrrStarterJarRefs.jrrutilitiesOneJar.resolveToFile()
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
    }
}
