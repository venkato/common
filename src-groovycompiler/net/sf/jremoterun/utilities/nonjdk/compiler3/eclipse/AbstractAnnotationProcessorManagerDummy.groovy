package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager
import org.eclipse.jdt.internal.compiler.Compiler
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;

import java.util.logging.Logger;

@CompileStatic
class AbstractAnnotationProcessorManagerDummy extends AbstractAnnotationProcessorManager{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void configure(Object batchCompiler, String[] options) {

    }

    @Override
    void configureFromPlatform(Compiler compiler, Object compilationUnitLocator, Object javaProject, boolean isTestCode) {

    }

    @Override
    void setOut(PrintWriter out) {

    }

    @Override
    void setErr(PrintWriter err) {

    }

    @Override
    ICompilationUnit[] getNewUnits() {
        return new ICompilationUnit[0]
    }

    @Override
    ReferenceBinding[] getNewClassFiles() {
        return new ReferenceBinding[0]
    }

    @Override
    ICompilationUnit[] getDeletedUnits() {
        return new ICompilationUnit[0]
    }

    @Override
    void reset() {

    }

    @Override
    void processAnnotations(CompilationUnitDeclaration[] units, ReferenceBinding[] referenceBindings, boolean isLastRound) {
        log.info "dummy done"
    }

    @Override
    void setProcessors(Object[] processors) {

    }
}
