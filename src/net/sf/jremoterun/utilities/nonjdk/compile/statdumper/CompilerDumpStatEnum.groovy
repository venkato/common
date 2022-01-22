package net.sf.jremoterun.utilities.nonjdk.compile.statdumper

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern

/**
 * @see org.codehaus.groovy.control.ClassNodeResolver#findDecompileed
 */
@CompileStatic
enum CompilerDumpStatEnum implements ChildPattern {
    classesParent,
    javaCompiler,
    groovyCompiler,
    lookupedByGroovy,
    groovyAstMethodsAddedTo,
    groovyAstMethodsSkipped,
    groovyResolvedClasses,
    groovyFailedResolvedClasses,
    classpath,
    usedFileAnalyzed,
    ;

    @Override
    File resolveChild(File parent) {
        return new File(parent, approximatedName())
    }

    @Override
    String approximatedName() {
        String suffix = '.txt'
        if (this == usedFileAnalyzed) {
            suffix = '.groovy'
        }
        return name() + suffix
    }
}
