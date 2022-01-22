package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.DependencySet

import java.util.logging.Logger;

@CompileStatic
class PrintDependencies {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public org.gradle.api.artifacts.Configuration configuration1

    public ClassPathCalculatorSup2Groovy calculator = new ClassPathCalculatorSup2Groovy();
    public Project project2;

    org.gradle.api.artifacts.Configuration findConfiguration(GradleConfigurations type, Project project1) {
        findConfiguration(type.customName,project1)
    }

    org.gradle.api.artifacts.Configuration findConfiguration(String type, Project project1) {
        project2 = project1
        configuration1 = project1.getConfigurations().getByName(type)
        return configuration1
    }

    void findDefaultConfiguration(Project project) {
        findConfiguration(GradleConfigurations.compileClasspath, project);
    }

    void addAllResolvedFiles() {
        Set<File> resolve1 = configuration1.resolve()
        if(resolve1.size()>0) {
            calculator.addFilesToClassLoaderGroovySave.addAll resolve1
        }
    }

    void addDirectDependencies() {
        DependencySet dependencies3 = configuration1.getAllDependencies()
        dependencies3.each {
            addDependency(it)
        }
    }

    static MavenId convertToMavenId(Dependency d){
        String version13 = d.getVersion()
        if(version13==null){
            log.severe("${d.getGroup()}:${d.getName()}   ts=${d}")
            throw new NullPointerException("${d.getGroup()}:${d.getName()}   ts=${d}")
        }
        return new MavenId(d.getGroup(), d.getName(), version13)
    }

    void addDependency(Dependency d ){
        calculator.addFilesToClassLoaderGroovySave.add convertToMavenId(d)
    }

    public String classpathFileName = 'classpath.groovy'

    void saveToFileProjectDir(int rotateCount) {
        saveToFileProjectDir2(classpathFileName,rotateCount)
    }

    File calcWhereToSaveFile(String suffix ) {
        File dir = project2.getProjectDir()
        assert dir.exists()
        File file23 = new File(dir, suffix)
        return file23
    }

    File saveToFileProjectDir3(String suffix , int rotateCount) {
        File file23 = calcWhereToSaveFile(suffix)
        FileRotate.rotateFile(file23, rotateCount)
        saveToFile(file23)
        return file23;
    }

    void saveToFileProjectDir2(String suffix , int rotateCount) {
        if(calculator.filesAndMavenIds.size()>0) {
            saveToFileProjectDir3(suffix,rotateCount)
        }
    }

    void saveToFile(File f) {
        f.text = calculator.calcAndSave()
    }


}
