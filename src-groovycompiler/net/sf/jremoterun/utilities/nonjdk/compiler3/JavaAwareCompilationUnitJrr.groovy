package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.Phases
import org.codehaus.groovy.tools.javac.JavaAwareCompilationUnit;

import java.util.logging.Logger;

@CompileStatic
class JavaAwareCompilationUnitJrr extends JavaAwareCompilationUnit{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public GroovyCompiler groovyCompiler1;

    JavaAwareCompilationUnitJrr(CompilerConfiguration configuration, GroovyClassLoader groovyClassLoader, GroovyClassLoader transformClassLoader,GroovyCompiler groovyCompiler1) {
        super(configuration, groovyClassLoader,transformClassLoader)
        this.groovyCompiler1 = groovyCompiler1
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
        if(phase4<= Phases.FINALIZATION){
            description1 = Phases.getDescription(phase4)
        }

        log.info "doing phase : ${phase4} : ${description1}"
        super.gotoPhase(phase4)
    }


}
