package net.sf.jremoterun.utilities.nonjdk.gradle.cmds

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorSup2Groovy
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


    public String classpathAllWithDepFileName
    public String classpathAllDirectFileName

    public String classpathWithDepFileName
    public String classpathDirectFileName

    String acronym = 'dumpclasspath'

    ClassPathDumperGradleCmd() {
        initSuffixes('classpath')
    }

    @Override
    void addMe() {
        f1()
    }

    void initSuffixes(String prefix) {
        classpathAllWithDepFileName = prefix + 'AllWithDep.groovy'
        classpathAllDirectFileName = prefix + 'AllDirect.groovy'

        classpathWithDepFileName = prefix + 'WithDep.groovy'
        classpathDirectFileName = prefix + 'Direct.groovy'
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


    void saveCommonFile() {
        boolean manyChildWritten = projectsCount > 1
        if (writeDirectParent && (!writeDirectChild || manyChildWritten)) {
            if (calculatorDirect.filesAndMavenIds.size() > 0) {
                File f = new File(PropsEnum.user_dir.getValue() + '/' + classpathAllDirectFileName);
                FileRotate.rotateFile(f, rotateCount)
                f.text = calculatorDirect.calcAndSave()
            }
        }
        if (writeWithDepParent && (!writeWithDepChild || manyChildWritten)) {
            if (calculatorAll.filesAndMavenIds.size() > 0) {
                File f2 = new File(PropsEnum.user_dir.getValue() + '/' + classpathAllWithDepFileName);
                FileRotate.rotateFile(f2, rotateCount)
                f2.text = calculatorAll.calcAndSave()
            }
        }
    }

    public GradleConfigurations gc = GradleConfigurations.compileClasspath


    void onProjectImpl(Project mp) {
        log.warning "saving classpath ${mp.getPath()}"

        Configuration configuration = mp.getConfigurations().find { it.getName() == gc.customName }
        if (configuration == null) {
            log.warning "no ${gc} ${mp.getPath()}"
        } else {
            projectsCount++
            if (true) {
                PrintDependencies dpDierct = new PrintDependencies();

                dpDierct.findConfiguration(gc.customName, mp)
                DependencySet dependencies3 = dpDierct.configuration1.getAllDependencies()
                dependencies3.each {
                    MavenId mavenId2 = dpDierct.convertToMavenId(it)
                    if (mavenId2.version == null) {
                        log.warning "null version for ${mavenId2} ${it} for ${mp.getPath()}"
                    } else {
                        dpDierct.addDependency(it)
                        calculatorDirect.addFilesToClassLoaderGroovySave.add(mavenId2)
                    }
                }
                if (writeDirectChild) {
                    dpDierct.saveToFileProjectDir2(classpathDirectFileName, rotateCount)
                }
            }


            PrintDependencies dpWithDep = new PrintDependencies();
            dumpClasspathForDirctProject(dpWithDep, mp)
        }
    }


    void dumpClasspathForDirctProject(PrintDependencies dpWithDep, Project mp) {
        if (onExceptionAction != null) {
            dpWithDep.calculator.addFilesToClassLoaderGroovySave.onExceptionAction = onExceptionAction
        }
        dpWithDep.findConfiguration(gc.customName, mp)
        Collection<File> resolve1 = dpWithDep.configuration1.resolve().findAll { acceptFile(it) }
        if (resolve1.size() > 0) {
            dpWithDep.calculator.addFilesToClassLoaderGroovySave.addAll resolve1
            calculatorAll.addFilesToClassLoaderGroovySave.addAll resolve1
        }
        if (writeWithDepChild) {
            dpWithDep.saveToFileProjectDir2(classpathWithDepFileName, rotateCount)
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
        } catch (Exception e) {
            onException(e, mp)
        }
    }

    void onException(Exception e, Project mp) {
        log.log(Level.WARNING, "failed calc classpath ${mp.getPath()} ", e)
    }


}
