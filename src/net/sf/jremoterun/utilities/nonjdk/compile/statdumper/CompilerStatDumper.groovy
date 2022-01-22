package net.sf.jremoterun.utilities.nonjdk.compile.statdumper

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.filesutils.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.DumpLoadedClasses
import net.sf.jremoterun.utilities.nonjdk.compile.GenericCompiler

import java.util.logging.Logger

@CompileStatic
class CompilerStatDumper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public File dirDumpStat;
    public GenericCompiler compiler
    public int rotateCount = 30

    CompilerStatDumper(File dirDumpStat, GenericCompiler compiler) {
        this.dirDumpStat = dirDumpStat
        this.compiler = compiler
    }

    void dumpLoadedClassesStat() {
        List<String> classesParent = DumpLoadedClasses.dumpLoadedClasses(compiler.client.adderParent.classloader).collect { it.getName() }.sort(false)
        List<String> classesChilds = DumpLoadedClasses.dumpLoadedClasses(compiler.client.loader).collect { it.getName() }.sort(false)
        dirDumpStat.mkdir()
        assert dirDumpStat.exists()
        saveToFile(CompilerDumpStatEnum.classesParent, classesParent)
        if (compiler.response.usedClasses == null) {
            log.info "not java classes"
            saveToFile(CompilerDumpStatEnum.javaCompiler, [])
        } else {
            saveToFile(CompilerDumpStatEnum.javaCompiler, compiler.response.usedClasses)
        }
        saveToFile(CompilerDumpStatEnum.groovyCompiler, classesChilds)
        saveToFile(CompilerDumpStatEnum.lookupedByGroovy, compiler.response.lookupedByGroovyClasses)
        saveToFile(CompilerDumpStatEnum.groovyAstMethodsAddedTo, compiler.response.groovyAstMethodsAddedTo)
        saveToFile(CompilerDumpStatEnum.groovyAstMethodsSkipped, compiler.response.groovyAstMethodsSkipped)
        saveToFile(CompilerDumpStatEnum.groovyResolvedClasses, compiler.response.groovyResolvedClasses.findAll {it.contains('.')}.findAll {!it.startsWith('[')})
        saveToFile(CompilerDumpStatEnum.groovyFailedResolvedClasses, compiler.response.groovyFailedResolvedClasses)
        File classPathFile = new File(dirDumpStat, CompilerDumpStatEnum.classpath.approximatedName())
        FileRotate.rotateFile(classPathFile, rotateCount)
        classPathFile.text = compiler.client.adder.addedFiles2.collect { it.getAbsolutePathUnix() }.join('\n')
    }

    void saveToFile(CompilerDumpStatEnum statId, Collection<String> classes) {
        DumpLoadedClasses.dumpClassesS(statId.resolveChild(dirDumpStat), rotateCount, classes)
    }

}
