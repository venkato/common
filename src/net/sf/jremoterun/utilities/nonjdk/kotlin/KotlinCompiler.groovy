package net.sf.jremoterun.utilities.nonjdk.kotlin

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy;

import java.util.logging.Logger;

@CompileStatic
class KotlinCompiler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    /**
     * Contains a lot of java services, which may break compilation in idea
     */
    public static MavenId mavenId = new MavenId('org.jetbrains.kotlin:kotlin-compiler:1.9.10')

    public ClRef compiler = new ClRef('org.jetbrains.kotlin.cli.jvm.K2JVMCompiler')
    public AddFileToClassloaderDummy classpath = new AddFileToClassloaderDummy();
    public File outputDir;
    public File kotlinHome;
    public File javaHome;
    public List<File> sources = []
    public List<String> fullArgs = []

    void buildArgs() {
        //fullArgs.add '-verbose'
        if(javaHome!=null) {
            fullArgs.add '-jdk-home'
            fullArgs.add javaHome.getAbsolutePath()
        }
//        fullArgs.add '-Xcompile-java'
//        fullArgs.add '-Xdebug'
//        fullArgs.add '-Xlist-phases'
        if (classpath.addedFiles2.size() > 0) {
            fullArgs.add '-classpath'
            fullArgs.add classpath.addedFiles2.collect { it.getAbsolutePath() }.join(File.pathSeparator)
        }
        if (outputDir != null) {
            fullArgs.addAll(['-d', outputDir.getAbsolutePath()])
        }
        if (kotlinHome != null) {
            fullArgs.addAll(['-kotlin-home', kotlinHome.getAbsolutePath()])
        }
        fullArgs.addAll(sources.collect { it.getAbsolutePath() })
    }

    void doCompile() {
        String[] v = fullArgs.toArray(new String[0]);
        JrrClassUtils.runMainMethod(compiler, v) // need check if works //NOFIELDCHECK
    }

}
