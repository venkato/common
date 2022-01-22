package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.io.FileType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrFieldAccessorSetter
import net.sf.jremoterun.utilities.classpath.AddFilesToUrlClassLoaderGroovy
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.compiler3.ast.ASTTransformationSetPropsMethod
import net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse.EclipseCompilerFactoryC
import org.codehaus.groovy.control.CompilationUnit

//import net.sf.jremoterun.utilities.nonjdk.langi.JrrFieldAccessorSetter
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.tools.javac.JavaAwareCompilationUnit
import org.codehaus.groovy.tools.javac.JavacCompilerFactory
import org.codehaus.groovy.transform.stc.ExtensionMethodCache

import java.util.logging.Logger

@CompileStatic
class GroovyCompiler {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static ClRef javacCnr = new ClRef('net.sf.jremoterun.utilities.nonjdk.compiler3.javac.JavacCompilerFactoryC')


    CompilerConfiguration configuration = new CompilerConfiguration();
    JavaAwareCompilationUnit unit;
    File outDir
    final GroovyClassLoader groovyClassLoader3;
    AddFilesToUrlClassLoaderGroovy addFilesToUrlClassLoaderGroovy;
    List<String> additionalFlags = ['-g']
    JavacCompilerFactory compilerFactory;
    boolean eclipseCompiler
    ASTTransformationSetPropsMethod astTransformationSetPropsMethod = new ASTTransformationSetPropsMethod()
    public ClassNodeResolverJrr classNodeResolverJrr = new ClassNodeResolverJrr()
    public volatile boolean classNodeResolverCustomSet = false
    public GroovyCompilerParams params;
    public volatile int classGenerationErrorCount =0;


    GroovyCompiler(GroovyCompilerParams params) {
        this.params = params
        groovyClassLoader3 = JrrClassUtils.getCurrentClassLoaderGroovy();
        //astTransformationSetPropsMethod.generateForExceptions = true
        //log.info " doing doExperiments : ${astTransformationSetPropsMethod.generateForExceptions}"
    }



    void init(){
        this.eclipseCompiler = params.eclipseCompiler
        if (eclipseCompiler) {
            compilerFactory = new EclipseCompilerFactoryC(this, params)
        } else {
            //Class clazz = javacCnr.loadClass(JrrClassUtils.getCurrentClassLoader())
            compilerFactory = JrrClassUtils.invokeConstructor(javacCnr, this, params) as JavacCompilerFactory
        }
        this.outDir = params.outputDir;
        addFilesToUrlClassLoaderGroovy = new AddFilesToUrlClassLoaderGroovy(groovyClassLoader3);
//        configuration.setDebug(true) // start failing after enabing debug
        if(params.groovyMaxErrorTolerance!=null) {
            configuration.setTolerance(params.groovyMaxErrorTolerance)
        }
        if(params.groovyOptimizationOptions.size()>0){
            params.setGroovyOptimizationOptions(params.groovyOptimizationOptions)
        }
        configuration.setDebug(params.groovyDebugFlag)
        //configuration.setVerbose(true) // useless
        configuration.setJointCompilationOptions(params.jointCompilationOptions)
        assert groovyClassLoader3 != null
        configuration.setTargetDirectory(outDir)
//        log.info "nik new vistor created 113"
        if(params.generateGroovyMethods) {
            configuration.addCompilationCustomizers(astTransformationSetPropsMethod);
        }
        unit = new JavaAwareCompilationUnitJrr(configuration, groovyClassLoader3,null,this){

            @Override
            public void addPhaseOperation(final CompilationUnit.ISourceUnitOperation op, final int phase) {
                if(!classNodeResolverCustomSet){
                    setClassNodeResolver(classNodeResolverJrr)
                    setClassGenField(this)
                    classNodeResolverCustomSet = true;
                }
                super.addPhaseOperation (op,phase);
            }
        }
        assert classNodeResolverCustomSet
        unit.setCompilerFactory(compilerFactory)
        if(true) {
            if (params.needCustomJrrGroovyFieldsAccessors) {
                JrrFieldAccessorSetter.setFieldAccessors();
            } else {
                assert !JrrFieldAccessorSetter.inited
            }
        }else{
            JrrFieldAccessorSetter.setFieldAccessors();
        }
        if(params.reloadExtMethods){
            GroovyExtensionMethodsReloader.rescanExtensionMethodsAll();
//            GroovyExtensionMethodsReloader.rescanExtensionMethods(JrrClassUtils.getCurrentClassLoader());
        }
    }

    void setClassGenField(JavaAwareCompilationUnitJrr unit3){
        CompilationUnit.IPrimaryClassNodeOperation orig123= JrrClassUtils.getFieldValue(unit3,'classgen') as CompilationUnit.IPrimaryClassNodeOperation
        assert orig123!=null
        CompilationUnit.IPrimaryClassNodeOperation nodeOperation = new IPrimaryClassNodeOperationJrr(orig123,this);
        JrrClassUtils.setFieldValue(unit3,'classgen',nodeOperation)
    }



    void compile() {
        try {
            unit.compile()
        }finally {
            if(classGenerationErrorCount>0) {
                log.info "classGenerationErrorCount = ${classGenerationErrorCount}"
            }
        }
    }

    void addClassesInDirForCompile(File dir,boolean checkFilesPresentInDir) {
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
            if(checkFilesPresentInDir && files.size()==0){
                throw new Exception("no files in dir ${dir}")
            }
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
