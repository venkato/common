package net.sf.jremoterun.utilities.nonjdk.compile.stddirs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.File2FileRefWithSupportI
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.javacompiler.CompileFileLayout;

import java.util.logging.Logger;

@CompileStatic
class CompileSpec {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<ChildPattern> srcs = [];
    public boolean ignoreTest = true
    public ChildPattern outputDir;
    public net.sf.jremoterun.utilities.nonjdk.classpath.helpers.JavaSourceFinder javaSourceFinder = new net.sf.jremoterun.utilities.nonjdk.classpath.helpers.JavaSourceFinder()


    void setOut(String out1) {
        outputDir = new ExactChildPattern(out1)
    }

    void addSrc(String src) {
        srcs.add(new ExactChildPattern(src))
    }

    void findSources(ToFileRef2 ref1) {
        findSources(ref1.resolveToFile())
    }

    void findSources(File f) {
        List<File> dirs4 = javaSourceFinder.findAllJavaSrcDirs(f)
        List<String> pathToParent1 = dirs4.collect { f.getPathToParent(it) }
        pathToParent1 = pathToParent1.findAll { isOkPath(it) }
        log.info "found src dirs : ${pathToParent1.join(' ')}"
        srcs.addAll pathToParent1.collect { new ExactChildPattern(it) }
    }

    public List<String> ignorePath6 = ['test','tests']

    boolean isOkPath(String path) {
        if (ignoreTest) {
            if( ignorePath6.contains(path)){
                return false
            }
            if(ignorePath6.find {path.startsWith(it+'/')}!=null){
                return false
            }
//            if(path.contains('/test/')){
            if(ignorePath6.find {path.contains('/'+it+'/')}!=null){
                return false
            }
            //if(path.endsWith('/test')){
            if(ignorePath6.find {path.endsWith('/'+it)}!=null){
                return false
            }
        }
        return true
    }

    CompileFileLayout apply2(File f) {
        return apply2(new FileToFileRef(f))
    }

    CompileFileLayout applyFindSrc(File2FileRefWithSupportI fileRef2) {
        findSources(fileRef2.resolveToFile())
        return apply2(fileRef2)
    }

    CompileFileLayout apply2(ChildFileLazy fileRef2) {
        CompileFileLayout ll = new CompileFileLayout()
        apply1(fileRef2, ll)
        return ll
    }

    void apply1(ChildFileLazy fileRef2, CompileFileLayout ll) {
        srcs.each {
            try {
                ll.srcDirs.add fileRef2.childP(it)
            } catch (Throwable e) {
                log.info("failed on ${it}", e)
                throw e;
            }
        }
        if (outputDir != null) {
            ll.outputDir = fileRef2.childP(outputDir)
        }
    }


}
