package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.CompilationUnit
import org.codehaus.groovy.control.ResolveVisitor;

import java.util.logging.Logger;

@CompileStatic
class ResolveVisitorJrr extends ResolveVisitor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public GroovyCompiler groovyCompiler1;

    ResolveVisitorJrr(CompilationUnit compilationUnit, GroovyCompiler groovyCompiler) {
        super(compilationUnit)
        groovyCompiler1 = groovyCompiler
        setClassNodeResolver(groovyCompiler1.classNodeResolverJrr)
    }

    @Override
    protected boolean resolve(ClassNode type, boolean testModuleImports, boolean testDefaultImports, boolean testStaticInnerClasses) {
        boolean b = super.resolve(type, testModuleImports, testDefaultImports, testStaticInnerClasses)
        if (b) {
            groovyCompiler1.resolvedClasses.add(type.getName())
        } else {
            groovyCompiler1.failedResolvedClasses.add(type.getName())
        }
        return b
    }

    @Override
    protected boolean resolve(ClassNode type) {
        return super.resolve(type)
    }
}
