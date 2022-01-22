package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

/**
 * @see net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBuildRunnerSettings
 */
@CompileStatic
class IdeaBuildRunnerSettings2 {
    private static final Logger log = Logger.getLogger(IdeaBuildRunnerSettings2.getName());


    public static volatile boolean launcherOriginal = true
    /**
     * Should be true, otherwise failed to find eclipse compiler
     * @see org.jetbrains.jps.builders.impl.java.EclipseCompilerTool#findCompiler
     */
    public static volatile boolean useJrrClassloader = true
    public static List<File> prependFilesClassLoader = []
    public static List<File> appendFilesClassLoader = []
    public static  URLClassLoader jpsLoader;
    public static  volatile Runnable justBeforeOrigRun;

    /**
     * @see org.jetbrains.jps.cmdline.BuildMain#PRELOAD_PROJECT_PATH
     */
    public static String preloadProjectPathS = "preload.project.path";

    public static IdeaBuiderSettings3 buiderSettings3 = new IdeaBuiderSettings3()

    public static File getProjectPath(){
        String projectPath2 = System.getProperty(preloadProjectPathS);
        if(projectPath2==null){
            log.severe "props not set ${preloadProjectPathS}"
        }
        return new File(projectPath2)
    }




}
