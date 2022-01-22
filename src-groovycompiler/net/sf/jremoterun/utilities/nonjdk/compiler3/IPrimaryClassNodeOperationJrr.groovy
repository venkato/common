package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.SourceUnit;

import java.util.logging.Logger;

@CompileStatic
class IPrimaryClassNodeOperationJrr implements CompilationUnit.IPrimaryClassNodeOperation{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    CompilationUnit.IPrimaryClassNodeOperation orig123
    GroovyCompiler groovyCompiler;

    IPrimaryClassNodeOperationJrr(CompilationUnit.IPrimaryClassNodeOperation orig123, GroovyCompiler groovyCompiler) {
        this.orig123 = orig123
        this.groovyCompiler = groovyCompiler
    }

//    @Override
//    void doPhaseOperation(CompilationUnit unit) throws CompilationFailedException {
//        super.doPhaseOperation(unit)
//    }

    @Override
    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {

        String name123 = classNode.getName()
        try {
            orig123.call(source, context, classNode);
        }catch(Throwable ee){
            int classGenerationErrorCount1 = groovyCompiler.classGenerationErrorCount
            groovyCompiler.classGenerationErrorCount++;
            if(classGenerationErrorCount1 < groovyCompiler.params.printFirstErrorsOnClassGeneration) {
                log.info("failed on ${name123} : ${ee.getClass()}")
            }
            throw ee
        }
    }




}
