package net.sf.jremoterun.utilities.nonjdk.maven.launcher.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy
import org.apache.maven.execution.ExecutionEvent
import org.apache.maven.model.Dependency
import org.apache.maven.project.MavenProject;

import java.util.logging.Logger;

@CompileStatic
class MavenDumpDependencies {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassPathCalculatorSup2Groovy calculator = new ClassPathCalculatorSup2Groovy();

    public MavenProject mp;

    MavenDumpDependencies() {

    }
    MavenDumpDependencies(ExecutionEvent event) {
        mp = event.getProject()
    }

    MavenDumpDependencies(MavenProject mp) {
        this.mp = mp
    }

    void dumpDirectOnly(){
        dumpDependencies(mp.getDependencies())
    }

    void dumpWithDependenciesOnlyDeprecated(){
        dumpDependencies(mp.getCompileDependencies())
    }

    void dumpWithDependenciesOnlyEls(){
        dumpElements(mp.getCompileClasspathElements())
    }


    void dumpDependencies(List<Dependency> dependencies1){
        dependencies1.each {
            addDependency(it)
        }
    }


    void addDependency(String d){
        File f = new File(d);
        if( f.exists()) {
            calculator.addFilesToClassLoaderGroovySave.add f
        }else{
            log.info "file not found ${f}"
        }
    }

    static MavenId convertDependency(Dependency d){
        return new MavenId(d.getGroupId(),d.getArtifactId(),d.getVersion())
    }

    void addDependency(Dependency d){
        calculator.addFilesToClassLoaderGroovySave.add (convertDependency(d))
    }

    void dumpElements(List<String> dependencies1){
        dependencies1.each {
            addDependency(it)
        }
    }

    public String classpathFileName = 'classpath.groovy'

    void saveToFileProjectDir(int rotateCount) {
        saveToFileProjectDir2(classpathFileName,rotateCount)
    }
    void saveToFileProjectDir2(String suffix, int rotateCount) {
        if(calculator.filesAndMavenIds.size()>0) {
            File dir = calcDirToSave()
            assert dir.exists()
            File file23 = new File(dir, suffix)
            FileRotate.rotateFile(file23, rotateCount)
            saveToFile(file23)
        }
    }

    File calcDirToSave(){
        mp.getBasedir()
    }

    void saveToFile(File f) {
        f.text = calculator.calcAndSave()
    }
}
