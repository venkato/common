package net.sf.jremoterun.utilities.nonjdk.gradle.utils

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.gradle.runner.JrrGradleEnv
import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.ProjectEvaluationListener
import org.gradle.api.ProjectState
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.invocation.DefaultGradle;

import java.util.logging.Logger;

@CompileStatic
class GradleEnvsUnsafe {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static org.gradle.invocation.DefaultGradle gradle = JrrGradleEnv.gradleEnv.initScript.getScriptTarget() as DefaultGradle;


    public static BuildResult br2;

    public static List<Runnable> buildFinishedListener = [];
    public static GradleBuildFailedListener buildFailedListener;
    public static Runnable actionFinally;
    public static boolean printExceptionDefault = true;
    public static boolean initedInDiffClassloader = false;


    static {
        registerMbeans()
        actionFinally = {
            br2 = null
            buildFinishedListener = null
            actionFinally = null
            gradle = null
            buildFailedListener = null
        }
        // TODO triggered for buildSrc project also ( if present )
        gradle.buildFinished { BuildResult br ->
            onBuildFinished(br);
        }
    }

    public static String sharedKey = 'jrrGradleUtils'

    static void registerMbeans(){
        JrrUtils.registerJRemoteRunMBeans()
        Date get1 = SharedObjectsUtils.globalMap.get(sharedKey) as Date
        if(get1==null){
            SharedObjectsUtils.globalMap.put(sharedKey,new Date())
        }else{
            log.warning("registered before!")
            initedInDiffClassloader = true
        }

    }

    static void onBuildFinished(BuildResult br ){
        try {
            // gradle.getSettings().getStartParameter().getExcludedTaskNames()
            br2 = br;
            if (buildFinishedListener.size()>0) {
                ContextClassLoaderWrapper.wrap2(JrrClassUtils.getCurrentClassLoaderGroovy(), {
                    buildFinishedListener.each {it.run()}
                })
            }
            if (br.getFailure() != null) {
                if (buildFailedListener != null) {
                    buildFailedListener.run()
                } else {
                    if (printExceptionDefault) {
                        GradleBuildFailedListener l1 = new GradleBuildFailedListener()
                        l1.addExcludeClasses()
                        l1.onException(br.getFailure())
                    }
                }

            }
        } finally {
            if (actionFinally != null) {
                actionFinally.run()
            }


        }
    }

    static Project fetchDefaultProject() {
        if(GradleEnvsUnsafe.gradle==null){
            throw new NullPointerException("gradle var is null")
        }
        return GradleEnvsUnsafe.gradle.getDefaultProject()
    }


    static void printExtProps1(Project p) {
        List<String> props = p.getProperties().keySet().sort()
        log.warning("${p} props = ${props}")
    }

    /**
     * this method print less properties then printExtProps1
     */
    static void printExtProps2(Project p) {
        List<String> props = p.getExtensions().getExtraProperties().getProperties().keySet().sort()
        log.warning("${p} ext props = ${props}")
    }


    private void notUsed() {
        log.warning("aaa default project = ${gradle.getDefaultProject()}")

//        gradle.addBuildListener(new BuildListener() {
//            @Override
//            void buildStarted(Gradle gradle) {
//
//            }
//
//            @Override
//            void settingsEvaluated(Settings settings) {
//                log.warning("new settings ${settings}")
//            }
//
//            @Override
//            void projectsLoaded(Gradle gradle) {
//                log.warning("new settings ${gradle}")
//            }
//
//            @Override
//            void projectsEvaluated(Gradle gradle) {
//
//            }
//
//            @Override
//            void buildFinished(BuildResult result) {
//
//            }
//        })
        gradle.addProjectEvaluationListener(new ProjectEvaluationListener() {
            @Override
            void beforeEvaluate(Project project) {
                log.warning("new project ${project}")
            }

            @Override
            void afterEvaluate(Project project, ProjectState state) {

            }
        })
    }


}
