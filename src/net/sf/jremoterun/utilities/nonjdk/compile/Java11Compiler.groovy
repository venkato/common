package net.sf.jremoterun.utilities.nonjdk.compile

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.IfFrameworkSrcDirs
import net.sf.jremoterun.utilities.nonjdk.javacompiler.EclipseJavaCompilerPure
import net.sf.jremoterun.utilities.nonjdk.fileloayout.Java11ModuleRef

import java.util.logging.Logger

@CompileStatic
class Java11Compiler {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public EclipseJavaCompilerPure compilerPure = new EclipseJavaCompilerPure();

    Java11Compiler(File outputDir) {
        this.compilerPure.outputDir = outputDir
    }

    void addClassPath(File java11BaseModule){
        compilerPure.adder.add java11BaseModule
    }

    void doJob( File java11Home){
        File parentFile = compilerPure.outputDir.getParentFile()
        assert parentFile.exists()
        compilerPure.outputDir.mkdir()
        //compilerPure.cleanOutputDir = false
        compilerPure.enableBootClassPath = false
        compilerPure.annotationProcessorManagerDummy = true
        compilerPure.addInDir IfFrameworkSrcDirs.src.childL(net.sf.jremoterun.utilities.nonjdk.java11.modulewrappersi.ModuleWrapperI.getPackage().getName().replace('.','/'))
        compilerPure.addInDir IfFrameworkSrcDirs.src_java11first
        addClassPath Java11ModuleRef.java_base.ref.resolveChild(java11Home)
        compilerPure.javaVersion = '1.8'
        compileImpl()
    }

    void compileImpl(){
        compilerPure.compile()
    }

}
