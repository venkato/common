package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class AllClasspathAnalysisEasy {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    /**
     * @see net.sf.jremoterun.utilities.nonjdk.classpath.classloader.DumpLoadedClasses#dumpLoadedClassesAndJarPaths(java.io.File, java.io.File)
     */
    static AllClasspathAnalysis easyCalcUsedFromDir(File dir){
        return         easyCalcUsed2(dir.child(DumpLoadedClasses.classesFileName),dir.child(DumpLoadedClasses.filesFileName))
    }

    static AllClasspathAnalysis easyCalcUsed2(File loadedClassesFile,File urlFile){
        assert loadedClassesFile.exists()
        assert urlFile.exists()
        AllClasspathAnalysis analysis = new AllClasspathAnalysis();
        analysis.classesAddFromFile(loadedClassesFile)
        List<File> files = urlFile.readLines().collect { new File(it) };
        assert files.size()>0
        analysis.adder.addAll(files)
        analysis.analise()
        return analysis;
    }



}
