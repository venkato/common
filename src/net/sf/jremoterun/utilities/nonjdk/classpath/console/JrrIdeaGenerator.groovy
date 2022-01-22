package net.sf.jremoterun.utilities.nonjdk.classpath.console

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.groovystarter.ClassNameSynonym
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.JavaProcessRunner
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.compile.IdeaInitPluginCompiler
import net.sf.jremoterun.utilities.nonjdk.compile.IdeaPluginCompiler
import net.sf.jremoterun.utilities.nonjdk.compile.JrrUtilsCompiler
import net.sf.jremoterun.utilities.nonjdk.ioutils.CopyManyFilesToOneDir
import net.sf.jremoterun.utilities.nonjdk.javalangutils.JavaCmdOptions

import java.util.logging.Logger

@CompileStatic
class JrrIdeaGenerator implements ClassNameSynonym {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String pluginName = 'IdeaJarLoader'

    void compileInitAndUpdate(File ideaDir, File ideaPLuginDir) {
        File ideaPlugin = compileAndPrepare(ideaDir)
        File ideaPluginConfigs = ideaPLuginDir.child('config/plugins')
        assert ideaPLuginDir.exists()
        FileUtilsJrr.copyDirectoryToDirectory(ideaPlugin, ideaPluginConfigs)
        updateVmOptions(ideaPLuginDir)
        log.info "plugin created in ${ideaPluginConfigs}"
    }

    File compileAndPrepare(File ideaDir) {
        new JrrUtilsCompiler().all2()
        IdeaInitPluginCompiler compiler = new IdeaInitPluginCompiler()
        compiler.compile3(ideaDir)
        File pluginDir2 = compiler.client.ifDir.child("build/${pluginName}")
        File pluginDir = pluginDir2.child("lib")
        pluginDir.mkdirs()
        CopyManyFilesToOneDir copyManyFilesToOneDir = new CopyManyFilesToOneDir()
        copyManyFilesToOneDir.addSrc(JrrStarterJarRefs2.jremoterun)
        copyManyFilesToOneDir.addSrc(JrrStarterJarRefs2.jrrassist)
        copyManyFilesToOneDir.addSrc(JrrStarterJarRefs2.jrrbasetypes)
        copyManyFilesToOneDir.addSrc(JrrStarterJarRefs.jrrutilitiesOneJar)
        copyManyFilesToOneDir.copyFiles(pluginDir)
//        FileUtilsJrr.copyFileToDirectory(JrrStarterJarRefs2.jremoterun.resolveToFile(), pluginDir)
//        FileUtilsJrr.copyFileToDirectory(JrrStarterJarRefs2.jrrassist.resolveToFile(), pluginDir)
//        FileUtilsJrr.copyFileToDirectory(JrrStarterJarRefs.jrrutilitiesOneJar.resolveToFile(), pluginDir)
        File pluginJar = pluginDir.child("${pluginName}.jar")
        File metaInf = compiler.client.ifDir.child('resources/idea/META-INF')
        compiler.testUpdateIdeaJar2(pluginJar, metaInf)
        return pluginDir2
    }

    void compileAndGenerateIdeaConfig2(File ideaDir, File ideaLogDir) {
        compile(ideaDir)
        generateIdeaConfig2(ideaLogDir)
    }

    void compile(File ideaDir){
        new IdeaPluginCompiler().compile3(ideaDir)
    }

    void generateIdeaConfig2(File ideaLogDir) {
        generateIdeaConfig(getGitRepo(), ideaLogDir, new IdeaPluginCompiler().getCompiledDir())
    }

    static File getGitRepo(){
        CustomObjectHandlerImpl handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler as CustomObjectHandlerImpl
        File gitRepo = handler.cloneGitRepo3.gitBaseDir
        return gitRepo
    }

    void updateVmOptions( File ideaPLuginDir){
        File ideaVmOptions = net.sf.jremoterun.utilities.nonjdk.idea.IdeaFilesLayout.idea64ExeOptions.ref.resolveChild( ideaPLuginDir)
        assert ideaVmOptions.exists()
        String text = ideaVmOptions.text
        boolean needSave = false
        if(!text.contains(JrrStarterJarRefs2.jremoterun.name())){
            String s = JavaProcessRunner.createAgentArg(JavaCmdOptions.javaagent,JrrStarterJarRefs2.jremoterun)+'\n'
            text+=s
            needSave = true
        }
        if(!text.contains(JrrStarterJarRefs2.java11base.name())){
            String s = JavaProcessRunner.createAgentArg(JavaCmdOptions.javaagent,JrrStarterJarRefs2.java11base)+'\n'
            text+=s
            needSave = true
        }
        if(needSave){
            ideaVmOptions.text = text
        }
    }


    void generateIdeaConfig(File gitRepo, File ideaLogDir, File compiledClasses) {
        String s = JrrConfigGenerator.readText('idea.groovy')
        s = replace2(s, 'FgitRepoF', gitRepo)
        s = replace2(s, 'FideaLogDirF', ideaLogDir)
        s = replace2(s, 'FcompiledClassesF', compiledClasses)
        File jrrConfigDir2 = JrrConfigGenerator.getJrrConfigDir()
        jrrConfigDir2.child('idea.groovy').text = s
    }

    String replace2(String text, String key, File f) {
        String filePath = f.absolutePath.replace('\\', '/')
        assert text.contains(key)
        return text.replace(key, filePath)
    }

}
