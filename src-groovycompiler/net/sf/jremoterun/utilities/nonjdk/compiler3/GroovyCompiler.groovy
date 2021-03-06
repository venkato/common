package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.io.FileType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrFieldAccessorSetter
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse.EclipseCompilerFactoryC

//import net.sf.jremoterun.utilities.nonjdk.langi.JrrFieldAccessorSetter
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.javac.JavaAwareCompilationUnit
import org.codehaus.groovy.tools.javac.JavacCompilerFactory

import java.util.logging.Logger

@CompileStatic
class GroovyCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    static ClRef javacCnr = new ClRef('net.sf.jremoterun.utilities.nonjdk.compiler3.javac.JavacCompilerFactoryC')


    CompilerConfiguration configuration = new CompilerConfiguration();
    JavaAwareCompilationUnit unit;
    File outDir
    final GroovyClassLoader groovyClassLoader3;
    AddFilesToUrlClassLoaderGroovy addFilesToUrlClassLoaderGroovy;
    List<String> additionalFlags = ['-g']
    JavacCompilerFactory compilerFactory;
    boolean eclipseCompiler

    GroovyCompiler(GroovyCompilerParams params) {
        this.eclipseCompiler = params.eclipseCompiler
        if (eclipseCompiler) {
            compilerFactory = new EclipseCompilerFactoryC(this, params)
        } else {
            Class clazz = javacCnr.loadClass(JrrClassUtils.getCurrentClassLoader())
            compilerFactory = JrrClassUtils.invokeConstructor(clazz, this, params) as JavacCompilerFactory
        }
        this.outDir = params.outputDir;
        groovyClassLoader3 = JrrClassUtils.getCurrentClassLoaderGroovy();
        addFilesToUrlClassLoaderGroovy = new AddFilesToUrlClassLoaderGroovy(groovyClassLoader3);
//        configuration.setDebug(true) -- start failing after enabing debug
//        configuration.setVerbose(true) -- useless
        configuration.setJointCompilationOptions(params.jointCompilationOptions)
        assert groovyClassLoader3 != null
        configuration.setTargetDirectory(outDir)

        unit = new JavaAwareCompilationUnit(configuration, groovyClassLoader3)
        unit.setCompilerFactory(compilerFactory)
        if (params.needCustomJrrGroovyFieldsAccessors) {
            JrrFieldAccessorSetter.setFieldAccessors();
        }
    }


    void compile() {
//        jointCompilationOptions.put('flags',flags.toArray(new String[0]))
        unit.compile()
    }

    void addClassesInDirForCompile(File dir) {
//        assert dir.directory
        if (dir.isFile()) {
            unit.addSource(dir)
        } else {
            assert dir.isDirectory()
            List<File> files = []
            dir.eachFileRecurse(FileType.FILES, {
                File f = it as File
                String name1 = f.getName()
                if (name1.endsWith('.java') || name1.endsWith('.groovy')) {
                    files.add(f)
                }
            })
            File[] files1 = (File[]) files.toArray(new File[0])
            unit.addSources(files1)
        }
    }

    void setJavaVersion(String javaVersion2) {
        configuration.setTargetBytecode(javaVersion2)
        List<String> flags = additionalFlags
        flags.add('-source')
        flags.add(javaVersion2)
        flags.add('-target')
        flags.add(javaVersion2)
        if (eclipseCompiler) {
            flags.add("-${javaVersion2}".toString())
        }

    }


}
