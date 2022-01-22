package net.sf.jremoterun.utilities.nonjdk.javacompiler

import groovy.io.FileType
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy
import net.sf.jremoterun.utilities.nonjdk.compiler3.CopyFilesTmp
import net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse.EclipseCompiler3
import org.apache.commons.io.FileUtils
import org.eclipse.jdt.core.compiler.CompilationProgress

import java.util.logging.Logger

@CompileStatic
public class EclipseJavaCompilerPure {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public File outputDir
    public AddFileToClassloaderDummy adder = new AddFileToClassloaderDummy()
    List<File> files = []
    EclipseCompiler3 compiler3;
    String javaVersion
    public boolean rememberOutput = false;
    public boolean checkFilesPresentInDir = true
    List<String> additionalFlags = ['-g', '-nowarn',]
    CompilationProgress compilationProgress;
    Map<String, String> customDefaultOptions;
    boolean annotationProcessorManagerDummy = true
    boolean enableBootClassPath = true
    public CopyFilesTmp copyFilesTmp =new CopyFilesTmp();

    EclipseJavaCompilerPure() {
        copyFilesTmp.filePrefix = 'javapure-'
    }

    void enableDebug(){
        if(customDefaultOptions==null) {
            customDefaultOptions = [:]
        }
        customDefaultOptions.put(org.eclipse.jdt.internal.compiler.impl.CompilerOptions.OPTION_JdtDebugCompileMode, org.eclipse.jdt.internal.compiler.impl.CompilerOptions.ENABLED)
    }

    void addJavaPath(File javaDir){
        enableBootClassPath = false
        File modDir = new File(javaDir,'jmods')
        if(modDir.exists()) {
            List<File> list1 = modDir.listFiles().toList()
            assert list1.size()>0
            list1.each {
                if(it.getName().endsWith('jmod')){
                    adder.add(it)
                }
            }
        }else {
            File modDir2 = new File(javaDir,'jre/lib')
            if(modDir2.exists()){
                adder.addAllJarsInDir(modDir2)
            }else{
                File modDir3 = new File(javaDir,'lib')
                if(modDir3.exists()){
                    adder.addAllJarsInDir(modDir3)
                }else{
                    throw new Exception("failed add ${javaDir}")
                }
            }
        }
    }


    void addClassPathCopy(ToFileRef2 f){
        adder.add(copyFilesTmp.addClassPathCopy(f))
    }

    void addClassPathCopy(File f){
        adder.add(copyFilesTmp.addClassPathCopy(f))
    }

    void setFromSpec(CompileFileLayout compileFileLayout){
        assert compileFileLayout.outputDir!=null
        assert compileFileLayout.javaVersion!=null
        if(compileFileLayout.libs.size()>0) {
            adder.addAll compileFileLayout.libs
        }
        compileFileLayout.srcDirs.each {
            try {
                addInDir(it)
            }catch(Throwable e){
                log.info("failed on ${it}",e)
                throw e;
            }
        }
        javaVersion = compileFileLayout.javaVersion
        outputDir = compileFileLayout.outputDir.resolveToFile()
    }

    void addInDir(ToFileRef2 f) {
        addInDir f.resolveToFile()
    }

    void addInDir(File f) {
        assert f.exists()
        if (f.isFile()) {
            this.files.add(f)
        } else {
            int filesFound = addInDirImpl(f)
            if (checkFilesPresentInDir && filesFound == 0) {
                throw new Exception("No files found in ${f}")
            }
        }
    }

    int addInDirImpl(File f) {
        assert f.directory
        int filesFound = 0;
        f.eachFileRecurse(FileType.FILES, {
            boolean added = addFileImpl(it)
            if (added) {
                filesFound++
            }
        })
        return filesFound

    }

    boolean addFileImpl(File f2) {
        String name = f2.name
        if (name.endsWith('.java')) {
            this.files.add(f2)
            return true
        }
    }

    public boolean cleanOutputDir =true

    void checkOutDir() {
        assert outputDir != null
        if(cleanOutputDir) {
            FileUtils.deleteQuietly(outputDir)
        }
        outputDir.mkdir()
        assert outputDir.exists()
        if(cleanOutputDir) {
            outputDir.deleteDir()
        }
        outputDir.mkdir()
        if (!outputDir.exists()) {
            throw new FileNotFoundException("failed create ${outputDir}")
        }

        if(cleanOutputDir) {
            assert outputDir.listFiles().length == 0
        }
    }

    StringWriter createCompiler() {
        StringWriter javacOutput = new StringWriter();
        PrintWriter writer = new PrintWriter(javacOutput);
        compiler3 = new EclipseCompiler3(writer, writer, false, customDefaultOptions, compilationProgress, rememberOutput,annotationProcessorManagerDummy,enableBootClassPath)
        return javacOutput
    }

    void compile() {
        assert javaVersion != null
        checkOutDir()
        this.files = this.files.unique();
        String[] javacParameters = makeParameters();
        StringWriter javacOutput = createCompiler()
        boolean result = compile2(javacParameters)
//        log.info "result : ${result}"
        if (result) {
            String trim2 = javacOutput.toString().trim()
            if (trim2.length() > 0) {
                log.info "${trim2}";
            }
        } else {
            String header = "Compile error \n${javacOutput}"
            throw new Exception(header)
        }
    }

    boolean compile2(String[] javacParameters) {
        try {
            boolean result = compiler3.compile(javacParameters)
            return result
        }finally {
            copyFilesTmp.close()
        }
    }


    private String[] makeParameters() {
        if (this.files.size() == 0) {
            throw new Exception("No file to compile")
        }
        LinkedList<String> params = new LinkedList<String>();
        params.addAll(additionalFlags)
        params.add("-d");
        assert outputDir != null
        params.add(outputDir.getAbsolutePath());
        // add flags
        params.add('-source')
        params.add(javaVersion)
        params.add('-target')
        params.add(javaVersion)
        if (adder.addedFiles2.size() > 0) {
            params.add("-classpath");
            params.add(adder.addedFilesWithOrder.join(File.pathSeparator));
        } else {
        }

        params.addAll(this.files.collect { it.absolutePath });

        return params.toArray(new String[params.size()]);
    }


}
