package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef

import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.st.JdkLogFormatter
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.InfocationFrameworkStructure
import net.sf.jremoterun.utilities.nonjdk.archiver.JrrJarArchiver
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.CutomJarAdd
import net.sf.jremoterun.utilities.nonjdk.classpath.checkers.GroovyMethodsChecker
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.IfframeworkClasspath
import net.sf.jremoterun.utilities.nonjdk.classpath.ifframecalc.framew.MavenIdsCollector
import net.sf.jremoterun.utilities.nonjdk.classpath.inittracker.LogItem
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2
import net.sf.jremoterun.utilities.nonjdk.classpath.refs2.CutomJarAdd1
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.GitReferences
import net.sf.jremoterun.utilities.nonjdk.compiler3.CompileRequestClient
import net.sf.jremoterun.utilities.nonjdk.compiler3.CompileRequestClientBasic

import java.util.logging.Logger

@CompileStatic
class IfFrameworkCompiler extends GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File baseDir


    void addIdw() {
        client.adder.add CutomJarAdd1.downloadIdw()
    }

    public static MavenIdsCollector mavenIdsCollector;

    static void addLibs(AddFilesToClassLoaderGroovy adder, CompileRequestClientBasic client){
        client.addClassPathCopy(JrrStarterJarRefs.jrrutilitiesOneJar)
        client.addClassPathCopy(JrrStarterJarRefs2.jrrassist.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.jrrbasetypes.gitOriginRef())
        client.addClassPathCopy(JrrStarterJarRefs2.java11base.gitOriginRef())
//        adder.add JrrClassLocationRefs.Java11ModuleAccessF1
        adder.addAll net.sf.jremoterun.utilities.nonjdk.compile.MavenIdCollectorFactory.factory1.getCollector().loadMavenIds2 (new IfframeworkClasspath())
        adder.add JeditTermCompilerConsoleCompiler.compileIfNeededS()
        CutomJarAdd.addCustom(adder)
    }



    File  getOutputDir4(){
         return baseDir.child('build/ifbuild')
    }

    void prepare() {
        if (baseDir == null) {
            baseDir = client.ifDir
        }
        // if provider loaded java.nio.file.spi.FileSystemProvider, then
        // org.apache.sshd.client.subsystem.sftp.SftpFileSystemProvider loaded, which cause load
        // org.apache.sshd.common.io.IoServiceFactoryFactory
        // Could be 2 Factories, which it doesn't like and throw Exception 2 Factory found
        params.annotationProcessorManagerDummy = true;
        net.sf.jremoterun.utilities.nonjdk.shell.GroovySehllSshServiceSettings.setSshProps()
        params.printWarning = false
        params.javaVersion = '1.6'
        params.outputDir = getOutputDir4()
        params.outputDir.mkdirs()
        params.addInDir GitReferences.jnaplatext.resolveToFile()


        List<String> dirs = InfocationFrameworkStructure.dirs2
        dirs.each {
            params.addInDir(baseDir.child(it))
        }
        params.addInDir baseDir.child(GroovyCustomCompiler.groovyCustomSrc.child)
        params.addInDir baseDir.child(IfFrameworkSrcDirs.src_logger_ext_methods.dirName)

        addIdw()

        params.addTestClassLoaded JrrClassUtils
        params.addTestClassLoaded JdkLogFormatter
        params.addTestClassLoadedSameClassLoader(groovy.json.JsonSlurper)
        params.addTestClassLoadedSameClassLoader(groovy.util.AntBuilder)
        params.addTestClassLoadedSameClassLoader(groovy.util.slurpersupport.GPathResult)
//        log.info "finished"
        addClassPath(client.adder)
//        addToolsJar()
    }

    @Override
    void addClassPath(AddFilesToClassLoaderGroovy adder) {
        addLibs(adder,client);
        addToolsJar()
        adder.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaui()
        adder.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rsyntaxtextarea()
    }

    void addToolsJar() {
        File toolsJar = mcu.getToolsJarFile()
        if(toolsJar.exists()) {
            client.adder.add toolsJar
        }
    }

    void afterCompileChecks(){
        GroovyMethodsChecker checker=new GroovyMethodsChecker();
        checker.checkGroovyMethods(params.outputDir,  new ClRef(LogItem))
    }


    void zipp(File destJar) {
        JrrJarArchiver jarArchiver = new JrrJarArchiver(true)
        jarArchiver.acceptRejectFilterJar.endWithIgnoreClasses.add ClassNameSuffixes.dotjava.customName
        try {
            jarArchiver.step1(destJar, false)
            jarArchiver.step2(params.outputDir)
            jarArchiver.stepFinish()
        }finally{
            jarArchiver.stepFinalAlways()
        }
    }

    public static ClRef commonRedefinitionTester = new ClRef('net.sf.jremoterun.utilities.nonjdk.javassist.ClassRedefinitionTester')

    public static ClRef ideaRedefinitionTester = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.IdeaRedefineClassloaderTester')


    void testIdeaClassReloader() {
        client.adder.add params.outputDir
        testIdeaClassReloader2(client.loader)
    }

    static void testIdeaClassReloader2(ClassLoader cl) {
        // client.adder.addF params.outputDir
        ContextClassLoaderWrapper.wrap2(cl, {
            RunnableFactory.createRunnerFromClass(commonRedefinitionTester, cl).run()
            RunnableFactory.createRunnerFromClass(ideaRedefinitionTester, cl).run()

        })
    }




}
