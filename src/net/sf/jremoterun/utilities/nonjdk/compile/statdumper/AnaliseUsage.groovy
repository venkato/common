package net.sf.jremoterun.utilities.nonjdk.compile.statdumper

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.io.filesutils.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.ClassPathJrrStore;

import java.util.logging.Logger;

@CompileStatic
class AnaliseUsage {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public File basDir
    public boolean doSort = true

    AnaliseUsage(File basDir) {
        this.basDir = basDir
    }

    void doAll() {
        AllClasspathAnalysis used2 = easyCalcUsed2()
        saveResult(used2)
    }

    AllClasspathAnalysis easyCalcUsed2() {
        File urlFile = CompilerDumpStatEnum.classpath.resolveChild(basDir)
        assert urlFile.exists()
        AllClasspathAnalysis analysis = new AllClasspathAnalysis();
        analysis.classesAddFromFile(CompilerDumpStatEnum.javaCompiler.resolveChild(basDir))
        analysis.classesAddFromFile(CompilerDumpStatEnum.lookupedByGroovy.resolveChild(basDir))
        analysis.classesAddFromFile(CompilerDumpStatEnum.groovyResolvedClasses.resolveChild(basDir))
        List<File> files = urlFile.readLines().collect { new File(it) };
        assert files.size() > 0
        analysis.adder.addAll(files)
        analysis.analise()
        return analysis;
    }


    List doSortMy(List analysis) {
        analysis.sort(new AnalizeDumpComparator())
        return analysis
    }

    void saveResult(AllClasspathAnalysis analysis) {
        List usedHuman1 = new ArrayList(analysis.getUsedLocationsHuman().keySet())
        if (doSort) {
            usedHuman1 = doSortMy(usedHuman1)
        }
        ClassPathJrrStore writer7Sub = new ClassPathJrrStore()

        String s = writer7Sub.saveComplexObject(usedHuman1)
        File f = CompilerDumpStatEnum.usedFileAnalyzed.resolveChild(basDir)
        FileRotate.rotateFile(f, rorateCount)
        f.text = s
    }

    int rorateCount = 30

}
