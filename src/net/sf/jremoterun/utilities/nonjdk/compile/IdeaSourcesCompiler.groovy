package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.mdep.DropshipClasspath
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkResourceDirs
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrClassLocationRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.LatestMavenIds
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.idea.IdeaFilesLayout

import java.util.logging.Logger

@CompileStatic
class IdeaSourcesCompiler extends IfFrameworkCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static List<IfFrameworkSrcDirs> dirs5 = [//
                                                    IfFrameworkSrcDirs.src_frameworkloader,
                                                    IfFrameworkSrcDirs.src_groovycompiler,
                                                    IfFrameworkSrcDirs.src_idea,
                                                    IfFrameworkSrcDirs.src_common,
                                                    IfFrameworkSrcDirs.src_logger_ext_methods,
    ]

    public static List mavenIds = [
            GitMavenIds.jgit,
            LatestMavenIds.jsoup,
            LatestMavenIds.jodaTime,
            LatestMavenIds.javaCompiler2Janino,
            LatestMavenIds.javaCompilerJaninoCommon,
    ]


    File baseDir

    IdeaSourcesCompiler(File baseDir) {
        this.baseDir = baseDir
    }

    static void addIdeaStuff(File ideaDir, AddFilesToClassLoaderCommon adder) {
        adder.addAllJarsInDir  IdeaFilesLayout.groovyLib.ref.resolveChild(ideaDir);
        adder.addAllJarsInDir  IdeaFilesLayout.lib.ref.resolveChild(ideaDir);
    }

    void prepare() {
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
        client.adder.addAll DropshipClasspath.allLibsWithoutGroovy
        client.adder.addAll mavenIds
        File toolsJarFile = mcu.getToolsJarFile()
        if(toolsJarFile.exists()) {
            client.adder.add toolsJarFile
        }


        params.printWarning = false;
        params.addInDir new File(baseDir, "src")
        params.outputDir = new File(client.ifDir, 'build/ideapluginbuild')
        params.outputDir.mkdirs()
        params.javaVersion = '1.6'
        dirs5.each {
            addInDir(client.ifDir.child(it.dirName))
        }
        addInDir client.ifDir.child(IfFrameworkResourceDirs.resources_groovy.ref.child)

    }


}

