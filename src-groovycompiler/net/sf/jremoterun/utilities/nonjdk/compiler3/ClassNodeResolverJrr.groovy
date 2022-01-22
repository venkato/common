package net.sf.jremoterun.utilities.nonjdk.compiler3

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.control.ClassNodeResolver
import org.codehaus.groovy.control.CompilationUnit;

import java.util.logging.Logger;

/**
 * Anouther lookup happend in class
 * @see org.codehaus.groovy.ast.decompiled.DecompiledClassNode#createMethodNode
 * @see org.codehaus.groovy.control.ClassNodeResolver#findDecompiled
 */
@CompileStatic
class ClassNodeResolverJrr extends ClassNodeResolver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public HashSet<String> lookupedClasses = new HashSet<>()


    @Override
    LookupResult resolveName(String name1, CompilationUnit compilationUnit) {
        lookupedClasses.add(name1)
        //log.info "${name1}"
        return super.resolveName(name1, compilationUnit)
    }

    @Override
    void cacheClass(String name1, ClassNode res) {
        //log.info "${name1}"
        lookupedClasses.add(name1)
        super.cacheClass(name1, res)
    }

    @Override
    ClassNode getFromClassCache(String name1) {
        //log.info "${name1}"
        lookupedClasses.add(name1)
        return super.getFromClassCache(name1)
    }

    @Override
    LookupResult findClassNode(String name1, CompilationUnit compilationUnit) {
        //log.info "${name1}"
        lookupedClasses.add(name1)
        return super.findClassNode(name1, compilationUnit)
    }
}
