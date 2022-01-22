package net.sf.jremoterun.utilities.nonjdk.maven.launcher.cmds

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.fileloayout.ClassPathSaveSuffix
import net.sf.jremoterun.utilities.nonjdk.javalangutils.PropsEnum
import net.sf.jremoterun.utilities.nonjdk.maven.launcher.JrrMavenCliWrapper
import net.sf.jremoterun.utilities.nonjdk.maven.launcher.utils.MavenDumpDependencies
import org.apache.maven.execution.AbstractExecutionListener
import org.apache.maven.execution.ExecutionEvent
import org.apache.maven.project.MavenProject

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * use : mvn -Djrrdumpclasspath validate/compile
 */
@CompileStatic
class ClassPathDumperCmd implements MavenCmdI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MavenDumpDependencies calculatorDirect = new MavenDumpDependencies();
    public MavenDumpDependencies calculatorAll = new MavenDumpDependencies();

    public int rotateCount = 9
    public int projectsCount = 0
    public boolean writeDirectChild = true
    public boolean writeWithDepChild = true
    public boolean writeDirectParent = true
    public boolean writeWithDepParent = true

    public NewValueListener<Throwable> onExceptionAction;
//    public String classpathAllWithDepFileName = 'classpathAllWithDep.groovy'
//    public String classpathAllDirectFileName = 'classpathAllDirect.groovy'

//    public String classpathWithDepFileName = 'classpathWithDep.groovy'
//    public String classpathDirectFileName = 'classpathDirect.groovy'
    public String fileNamePrefix = 'classpath'

    String acronym = 'dumpclasspath'

    @Override
    void addMe() {
        f1()
    }

    void f1() {
        JrrMavenCliWrapper.cli.executionListenerProxy.nestedListeners.add(new AbstractExecutionListener() {
            @Override
            void projectSucceeded(ExecutionEvent event) {
                onProject(event.getProject())
            }

            @Override
            void projectFailed(ExecutionEvent event) {
                onProject(event.getProject())
            }

            @Override
            void projectSkipped(ExecutionEvent event) {
                super.projectSkipped(event)
            }

            @Override
            void sessionEnded(ExecutionEvent event) {
                log.info "end session"
                saveCommonFile()
            }
        })

    }


    String getSaveFileName(ClassPathSaveSuffix classPathSaveSuffix) {
        return fileNamePrefix + classPathSaveSuffix.name() + ClassNameSuffixes.dotgroovy.customName
    }

    public File saveUnderDir = new File(PropsEnum.user_dir.getValue())


    void saveCommonFile() {
        if(onExceptionAction!=null) {
            calculatorDirect.calculator.addFilesToClassLoaderGroovySave.onExceptionAction = onExceptionAction
            calculatorAll.calculator.addFilesToClassLoaderGroovySave.onExceptionAction = onExceptionAction
        }
        boolean manyChildWritten = projectsCount > 1

        if (writeDirectParent && (!writeDirectChild || manyChildWritten)) {
            if (calculatorDirect.calculator.filesAndMavenIds.size() > 0) {
                File f = new File(saveUnderDir,  getSaveFileName(ClassPathSaveSuffix.AllDirect));
                FileRotate.rotateFile(f, rotateCount)
                f.text = calculatorDirect.calculator.calcAndSave()
            }
        }
        if (writeWithDepParent && (!writeWithDepChild || manyChildWritten)) {
            if (writeWithDepParent) {
                if (calculatorAll.calculator.filesAndMavenIds.size() > 0) {
                    File f2 = new File(saveUnderDir, getSaveFileName(ClassPathSaveSuffix.AllWithDep));
                    FileRotate.rotateFile(f2, rotateCount)
                    f2.text = calculatorAll.calculator.calcAndSave()
                }
            }
        }

    }


    void onProjectImpl(MavenProject mp) {
        log.info "saving classpath ${mp.getName()}"
        projectsCount++

        MavenDumpDependencies dpDierct = new MavenDumpDependencies(mp);
        configureMavenDumpDeps(dpDierct)
        dpDierct.dumpDependencies(mp.getDependencies())
        if (writeDirectChild) {
            dpDierct.saveToFileProjectDir2(getSaveFileName(ClassPathSaveSuffix.Direct), rotateCount)
        }
        calculatorDirect.dumpDependencies(mp.getDependencies())

        if (writeWithDepChild) {
            MavenDumpDependencies dpAllDierct = new MavenDumpDependencies(mp);
            configureMavenDumpDeps(dpDierct)
            dpAllDierct.dumpDependencies(mp.getCompileDependencies())
            dpAllDierct.saveToFileProjectDir2(getSaveFileName(ClassPathSaveSuffix.WithDep), rotateCount)
        }

        calculatorAll.dumpDependencies(mp.getCompileDependencies())

        if (dpDierct.calculator.filesAndMavenIds.size() == 0) {
            log.info("no classpath for ${mp.getName()}")
        }
    }

    void configureMavenDumpDeps( MavenDumpDependencies dpDierct ){
        if(onExceptionAction!=null) {
            dpDierct.calculator.addFilesToClassLoaderGroovySave.onExceptionAction = onExceptionAction
        }
    }



    void onProject(MavenProject mp) {
        try {
            onProjectImpl(mp)
        } catch (Exception e) {
            onException(e, mp)
        }
    }

    void onException(Exception e, MavenProject mp) {
        log.log(Level.WARNING, "failed calc classpath ${mp.getName()}", e)
    }


}
