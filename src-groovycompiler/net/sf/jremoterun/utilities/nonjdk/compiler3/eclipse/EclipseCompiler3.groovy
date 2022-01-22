package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jdt.core.compiler.CategorizedProblem
import org.eclipse.jdt.core.compiler.CompilationProgress
import org.eclipse.jdt.internal.compiler.ClassFile
import org.eclipse.jdt.internal.compiler.CompilationResult
import org.eclipse.jdt.internal.compiler.IProblemFactory
import org.eclipse.jdt.internal.compiler.batch.FileSystem
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities

/**
 * Error id in IProblem class
 * Msg in org\eclipse\jdt\internal\compiler\problem\messages.properties next to ProblemHandler class
 * @see org.eclipse.jdt.internal.compiler.problem.ProblemHandler *
 * @see org.eclipse.jdt.core.compiler.IProblem
 */
@CompileStatic
class EclipseCompiler3 extends org.eclipse.jdt.internal.compiler.batch.Main {

    private static final java.util.logging.Logger logJdk = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<File, List<String>> myOutputs = new HashMap<>();
    public boolean rememberOutput;
    public boolean throwExceptionIfOccured = true
    public boolean annotationProcessorManagerDummy;
    public boolean enableBootClassPath
//    public boolean failedOnFirstError = true;
    public List<DefaultProblem> problems = [];
    public JrrEclipseProblemFactory eclipseProblemFactory = new JrrEclipseProblemFactory(this);
    public Exception firstErrorStackTrace

    EclipseCompiler3(PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress, boolean rememberOutput,
                     boolean annotationProcessorManagerDummy, boolean enableBootClassPath) {
        super(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress)
        this.rememberOutput = rememberOutput
        this.annotationProcessorManagerDummy = annotationProcessorManagerDummy
        this.enableBootClassPath = enableBootClassPath
    }


    String logClassPathJrrCommon(List<FileSystem.Classpath> aa) {
        List<String> collect1 = aa.toList().collect { printClassPathJrr(it) }
        return collect1.join('\n')
    }

    String printClassPathJrr(FileSystem.Classpath classpath) {
        return classpath.getPath()
    }

    @Override
    protected ArrayList<FileSystem.Classpath> handleBootclasspath(ArrayList<String> bootclasspaths, String customEncoding) {
        ArrayList<FileSystem.Classpath> aa = super.handleBootclasspath(bootclasspaths, customEncoding)
        if (enableBootClassPath) {
            return aa
        }
        logJdk.info "boot cp disabled"
        ArrayList<FileSystem.Classpath> classpaths1 = new ArrayList<FileSystem.Classpath>()

        return classpaths1
    }

    @Override
    protected void setPaths(ArrayList<String> bootclasspaths, String sourcepathClasspathArg, ArrayList<String> sourcepathClasspaths, ArrayList<String> classpaths, String modulePath, String moduleSourcepath, ArrayList<String> extdirsClasspaths, ArrayList<String> endorsedDirClasspaths, String customEncoding) {
        if (!enableBootClassPath) {
            bootclasspaths = new ArrayList<>()
        }
        super.setPaths(bootclasspaths, sourcepathClasspathArg, sourcepathClasspaths, classpaths, modulePath, moduleSourcepath, extdirsClasspaths, endorsedDirClasspaths, customEncoding)
    }

    public void outputClassFiles(CompilationResult result) {
        super.outputClassFiles(result);
        if (rememberOutput) {
            if (result == null || result.hasErrors() && !proceedOnError) {

            } else {
                List<String> classFiles = new ArrayList<>();
                for (ClassFile file : result.getClassFiles()) {
                    classFiles.add(new String(file.fileName()) + ".class");
                }
                File file2 = new File(new String(result.getFileName()))
                myOutputs.put(file2, classFiles);
            }
        }
    }

    @Override
    boolean compile(String[] argv) {
        return super.compile(argv)
    }

    public FileSystemJrr nameEnvironment

    public FileSystemJrr getLibraryAccess() {
//        logJdk.info('cl = '+logClassPathJrrCommon(checkedClasspaths.toList()))
        assert nameEnvironment == null
        nameEnvironment = new FileSystemJrr(this.checkedClasspaths, this.filenames,
                this.annotationsFromClasspath && CompilerOptions.ENABLED.equals(this.options.get(CompilerOptions.OPTION_AnnotationBasedNullAnalysis)),
                this.limitedModules);
        nameEnvironment.setModule1(this.module);
        JrrClassUtils.invokeJavaMethod(this, 'processAddonModuleOptions', nameEnvironment)
//        processAddonModuleOptions(nameEnvironment);
        return nameEnvironment;
    }


    Collection<String> buildUsedClasses() {
        return nameEnvironment.usedNames.collect { it.replace('/', '.') }.sort()
    }

    @Override
    protected void initializeAnnotationProcessorManager() {
        if (annotationProcessorManagerDummy) {
            this.batchCompiler.annotationProcessorManager = new AbstractAnnotationProcessorManagerDummy()
        } else {
            super.initializeAnnotationProcessorManager()
        }

    }

    @Override
    IProblemFactory getProblemFactory() {
        return eclipseProblemFactory
    }

    void onNewProblem(int severity, DefaultProblem categorizedProblem) {
        problems.add(categorizedProblem)
        int mast123 = severity & org.eclipse.jdt.internal.compiler.problem.ProblemSeverities.CoreSeverityMASK;
        if (mast123 > 0 && firstErrorStackTrace == null) {
            firstErrorStackTrace = new Exception('just stack trace')
        }
    }

    @Override
    void performCompilation() {
        super.performCompilation()
    }
}
