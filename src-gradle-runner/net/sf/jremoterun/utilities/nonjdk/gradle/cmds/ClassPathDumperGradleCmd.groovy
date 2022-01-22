package net.sf.jremoterun.utilities.nonjdk.gradle.cmds

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy
import net.sf.jremoterun.utilities.nonjdk.fileloayout.ClassPathSaveSuffix
import net.sf.jremoterun.utilities.nonjdk.gradle.utils.GradleConfigurations
import net.sf.jremoterun.utilities.nonjdk.gradle.utils.GradleEnvsUnsafe
import net.sf.jremoterun.utilities.nonjdk.gradle.utils.PrintDependencies
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.DependencySet

import java.util.logging.Level
import java.util.logging.Logger

/**
 * use : gradle -Djrrdumpclasspath
 * gradle.properties: org.gradle.java.home=c:/prog/java_9/
 */
@CompileStatic
class ClassPathDumperGradleCmd implements GradleCmdI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassPathCalculatorSup2Groovy calculatorDirect = new ClassPathCalculatorSup2Groovy();
    public ClassPathCalculatorSup2Groovy calculatorAll = new ClassPathCalculatorSup2Groovy();

    public NewValueListener<Throwable> onExceptionAction;

    public int rotateCount = 9
    public int projectsCount = 0
    public boolean writeDirectChild = true
    public boolean writeWithDepChild = true
    public boolean writeDirectParent = true
    public boolean writeWithDepParent = true

    public GradleConfigurations gc = GradleConfigurations.compileClasspath

    String acronym = 'dumpclasspath'
    String fileNamePrefix = 'classpath'

    ClassPathDumperGradleCmd() {
    }

    @Override
    void addMe() {
        f1()
    }


    String getSaveFileName(ClassPathSaveSuffix classPathSaveSuffix) {
        return fileNamePrefix + classPathSaveSuffix.name() + ClassNameSuffixes.dotgroovy.customName
    }

    void f1() {
        GradleEnvsUnsafe.buildFinishedListener.add {
            onBuildFinished()
        }

    }

    void onBuildFinished() {
        if (onExceptionAction != null) {
            calculatorDirect.addFilesToClassLoaderGroovySave.onExceptionAction = onExceptionAction
            calculatorAll.addFilesToClassLoaderGroovySave.onExceptionAction = onExceptionAction
        }
        Project defaultProject1 = GradleEnvsUnsafe.fetchDefaultProject()
        if (defaultProject1 == null) {
            log.warn("failed get default project")
        } else {
            Set<Project> allprojects1 = defaultProject1.getAllprojects()
            if (allprojects1 == null) {
                log.warn("failed get all projects")
            } else {
                allprojects1
                        .each {
                            onProject(it)
                        };

            }
            saveCommonFile()
        }
    }

    boolean isWriteAll(){
        boolean manyChildWritten = projectsCount > 1
        return manyChildWritten
    }

    void saveCommonFile() {
        boolean manyChildWritten = isWriteAll()
        if (writeDirectParent && (!writeDirectChild || manyChildWritten)) {
            saveAll(calculatorDirect, ClassPathSaveSuffix.AllDirect)
        }
        if (writeWithDepParent && (!writeWithDepChild || manyChildWritten)) {
            saveAll(calculatorAll, ClassPathSaveSuffix.AllWithDep)
        }
    }

    File saveAll(ClassPathCalculatorSup2Groovy calc1, ClassPathSaveSuffix suffix) {
        if (calc1.filesAndMavenIds.size() > 0) {
            File f2 = new File(PropsEnum.user_dir.getValue() + '/' + getSaveFileName(suffix));
            FileRotate.rotateFile(f2, rotateCount)
            f2.text = calc1.calcAndSave()
            return f2;
        }
        return null
    }


    void onProjectImpl(Project mp) {
        log.warning "saving classpath ${mp.getPath()}"

        Configuration configuration = mp.getConfigurations().find { it.getName() == gc.customName }
        if (configuration == null) {
            log.warning "no ${gc} ${mp.getPath()}"
        } else {
            projectsCount++
            if (true) {
                PrintDependencies dpDierct = createPrintDependencies(mp);

                dpDierct.findConfiguration(gc.customName, mp)
                DependencySet dependencies3 = dpDierct.configuration1.getAllDependencies()
                dependencies3.each {
                    try {
                        onDependency(it,dpDierct,mp)
                    }catch (Throwable e){
                        log.warning("failed on ${it} ${e}")
                        throw e
                    }
                }
                if (writeDirectChild) {
                    dpDierct.saveToFileProjectDir2(getSaveFileName(ClassPathSaveSuffix.Direct), rotateCount)
                }
            }


            PrintDependencies dpWithDep = createPrintDependencies(mp);
            dumpClasspathForDirctProject(dpWithDep, mp)
        }
    }

    void onDependency(org.gradle.api.artifacts.Dependency dep1,PrintDependencies dpDierct,Project mp ){
        if (dep1.getGroup() == null) {
            if (dep1 instanceof org.gradle.api.artifacts.SelfResolvingDependency) {
                org.gradle.api.artifacts.SelfResolvingDependency self1 = (org.gradle.api.artifacts.SelfResolvingDependency) dep1;
                Set<File> resolve1 = self1.resolve(true)
                if(resolve1==null){
                    onBadDependency(dep1, mp)
                }else{
                    Set<File> all1 = resolve1.findAll { acceptFile(it) }
                    log.warning("${mp.getPath()} resolve self dep to ${all1}")
                    if(all1.size()>0) {
                        dpDierct.calculator.addFilesToClassLoaderGroovySave.addAll(all1)
                        calculatorDirect.addFilesToClassLoaderGroovySave.addAll(all1)
                    }
                }

            }else {
                onBadDependency(dep1, mp)
            }
            //dpDierct.addDependency(it)
        } else {
            MavenId mavenId2 = dpDierct.convertToMavenId(dep1)
            if (mavenId2.version == null) {
                log.warning "null version for ${mavenId2} ${dep1} for ${mp.getPath()}"
            } else {
                dpDierct.addDependency(dep1)
                calculatorDirect.addFilesToClassLoaderGroovySave.add(mavenId2)
            }
        }
    }

    PrintDependencies createPrintDependencies(Project mp){
        return new PrintDependencies()
    }

    Collection<File> resolveDep(PrintDependencies dpWithDep, Project mp){
        Collection<File> resolve1 = dpWithDep.configuration1.resolve()
        return resolve1
    }



    void dumpClasspathForDirctProject(PrintDependencies dpWithDep, Project mp) {
        if (onExceptionAction != null) {
            dpWithDep.calculator.addFilesToClassLoaderGroovySave.onExceptionAction = onExceptionAction
        }
        dpWithDep.findConfiguration(gc.customName, mp)
        Collection<File> resolve1 =  resolveDep(dpWithDep,mp).findAll { acceptFile(it) }
        if (resolve1.size() > 0) {
            dpWithDep.calculator.addFilesToClassLoaderGroovySave.addAll resolve1
            calculatorAll.addFilesToClassLoaderGroovySave.addAll resolve1
        }
        if (writeWithDepChild) {
            dpWithDep.saveToFileProjectDir2(getSaveFileName(ClassPathSaveSuffix.WithDep), rotateCount)
        }
        if (dpWithDep.calculator.filesAndMavenIds.size() == 0) {
            log.warning("no classpath for ${mp.getName()}")
        }
    }


    public continueOnNonExistedFile = true

    boolean acceptFile(File f) {
        if (!f.exists()) {
            if (continueOnNonExistedFile) {
                log.warning "skip file ${f}"
                return false
            } else {
                throw new FileNotFoundException(f.getAbsolutePath());
            }
        }
        return true
    }


    void onProject(Project mp) {
        try {
            onProjectImpl(mp)
        } catch (Throwable e) {
            onException(e, mp)
        }
    }

    void onException(Throwable e, Project mp) {
        log.log(Level.WARNING, "failed calc classpath ${mp.getPath()} ", e)
    }

    void onBadDependency(org.gradle.api.artifacts.Dependency dep1,Project mp ){
        log.warning("null group for ${dep1}")
    }

}
