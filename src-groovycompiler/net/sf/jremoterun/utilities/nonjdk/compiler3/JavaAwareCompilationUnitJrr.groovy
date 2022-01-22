package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.control.ClassNodeResolver
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.Phases
import org.codehaus.groovy.tools.javac.JavaAwareCompilationUnit;

import java.util.logging.Logger;

@CompileStatic
class JavaAwareCompilationUnitJrr extends JavaAwareCompilationUnit {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public GroovyCompiler groovyCompiler1;

    JavaAwareCompilationUnitJrr(CompilerConfiguration configuration, GroovyClassLoader groovyClassLoader, GroovyClassLoader transformClassLoader, GroovyCompiler groovyCompiler1) {
        super(configuration, groovyClassLoader, transformClassLoader)
        this.groovyCompiler1 = groovyCompiler1
        resolveVisitor = new ResolveVisitorJrr(this,groovyCompiler1)
        ClassNodeResolver classNodeResolver123 = groovyCompiler1.classNodeResolverJrr
        setClassNodeResolver(classNodeResolver123)
        resolveVisitor.setClassNodeResolver(groovyCompiler1.classNodeResolverJrr);
//        assert resolveVisitor instanceof ClassNodeResolverJrr;
    }

    /**
     * @see org.codehaus.groovy.control.CompilationUnit.IPrimaryClassNodeOperation
     */
    @Override
    void completePhase() throws CompilationFailedException {
        log.info "completePhase : ${getPhase()} : ${org.codehaus.groovy.control.Phases.getDescription(getPhase())}"
        super.completePhase()
    }

    @Override
    void gotoPhase(int phase4) throws CompilationFailedException {
        String description1
        if (phase4 <= Phases.FINALIZATION) {
            description1 = Phases.getDescription(phase4)
        }

        log.info "doing phase : ${phase4} : ${description1}"
        super.gotoPhase(phase4)
    }


//    @Override
//    public void addPhaseOperation(final CompilationUnit.ISourceUnitOperation op, final int phase) {
//        if (groovyCompiler1 != null) {
//            assert getClassNodeResolver() instanceof ClassNodeResolverJrr
//            if (!groovyCompiler1.classNodeResolverCustomSet) {
//                assert getClassNodeResolver() instanceof ClassNodeResolverJrr
//                setClassNodeResolver(groovyCompiler1.classNodeResolverJrr)
//                groovyCompiler1.setClassGenField(this)
//                groovyCompiler1.classNodeResolverCustomSet = true;
//            }
//        }
//        super.addPhaseOperation(op, phase);
//    }

}
