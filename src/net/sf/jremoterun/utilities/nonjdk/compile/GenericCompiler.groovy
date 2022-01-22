package net.sf.jremoterun.utilities.nonjdk.compile;

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.CopyOnUsage
import net.sf.jremoterun.utilities.nonjdk.compiler3.CompileRequestClient
import net.sf.jremoterun.utilities.nonjdk.compiler3.CompileRequestClientBasic
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompilerParams
import net.sf.jremoterun.utilities.nonjdk.compiler3.GroovyCompilerReply
import net.sf.jremoterun.utilities.nonjdk.javacompiler.CompileFileLayout
import org.apache.commons.io.FileUtils

import java.util.logging.Logger;
import groovy.transform.CompileStatic;


@CompileStatic
abstract class GenericCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MavenCommonUtils mcu = new MavenCommonUtils()

    public GroovyCompilerParams params = new GroovyCompilerParams()

    public CompileRequestClientBasic client = new CompileRequestClientBasic()
    public GroovyCompilerReply response

    abstract void prepare();
    abstract void addClassPath(AddFilesToClassLoaderGroovy adder);

    GenericCompiler() {
        initBasic()
    }

    void initBasic(){
        client.initIfDir()
        client.init()
    }

    void compile() {
        response = client.compile(params)
    }

    void all2(){
        prepare()
        addClassPath(client.adder)
        compile()
        postCompileStep()
    }

    void postCompileStep(){

    }



    void setFromSpec(CompileFileLayout compileFileLayout){
        assert params.outputDir==null
        assert compileFileLayout.javaVersion!=null
        if(compileFileLayout.libs.size()>0) {
            client.adder.addAll compileFileLayout.libs
        }
        compileFileLayout.srcDirs.each {
            addInDir(it)
        }
        params.javaVersion = compileFileLayout.javaVersion
        params.outputDir = compileFileLayout.outputDir.resolveToFile()
    }

    static void copyMavenIdToDir(MavenIdContains mavenId, File toDir){
        File fileMavenId = new MavenCommonUtils().findMavenOrGradle(mavenId.m)
        assert fileMavenId!=null
        FileUtilsJrr.copyFileToDirectory(fileMavenId, toDir)
    }

    void addInDir(File... files){
        params.addInDir(files)
    }

//    void addInDir(ToFileRefSelf toFileRef){
//        params.addInDir toFileRef.resolveToFile()
//    }

    void addInDir(ToFileRef2 toFileRef){
        params.addInDir toFileRef.resolveToFile()
    }

}
