package net.sf.jremoterun.utilities.nonjdk.compiler3.eclipse

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.eclipse.jdt.core.compiler.CategorizedProblem
import org.eclipse.jdt.internal.compiler.problem.DefaultProblem
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory
import org.eclipse.jdt.internal.compiler.problem.ProblemSeverities;

import java.util.logging.Logger;

@CompileStatic
class JrrEclipseProblemFactory extends DefaultProblemFactory{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public EclipseCompiler3 eclipseCompiler3;

    JrrEclipseProblemFactory(EclipseCompiler3 eclipseCompiler3) {
        this.eclipseCompiler3 = eclipseCompiler3
    }

    @Override
    CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments, String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber) {
//        log.info("cp1 ${problemId} " +new String(originatingFileName))
        DefaultProblem r = super.createProblem(originatingFileName, problemId, problemArguments, messageArguments, severity, startPosition, endPosition, lineNumber, columnNumber) as DefaultProblem
        eclipseCompiler3.onNewProblem(severity,r)
        return r
    }

    @Override
    CategorizedProblem createProblem(char[] originatingFileName, int problemId, String[] problemArguments, int elaborationId, String[] messageArguments, int severity, int startPosition, int endPosition, int lineNumber, int columnNumber) {
//        if(eclipseCompiler3.failedOnFirstError){
//            throw new Exception("")
//        }
//        log.info("cp2 ${problemId} ${lineNumber} " +new String(originatingFileName))
//        severity = ProblemSeverities.Warning
//        Thread.dumpStack()
        DefaultProblem r = super.createProblem(originatingFileName, problemId, problemArguments, elaborationId, messageArguments, severity, startPosition, endPosition, lineNumber, columnNumber) as DefaultProblem
        eclipseCompiler3.onNewProblem(severity,r)
        return r;
    }
}
