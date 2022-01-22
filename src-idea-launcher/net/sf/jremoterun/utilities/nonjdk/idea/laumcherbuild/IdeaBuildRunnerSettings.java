package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URLClassLoader;
import java.util.Date;
import java.util.List;

/**
 * @see net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild2.IdeaBuildRunnerSettings2
 */
public class IdeaBuildRunnerSettings {
    public static String jrrUseOneJarS = "jrrUseOneJar";
    public static String jrrStartOriginalIfJrrMissingS = "jrrStartOriginalIfJrrMissing";
    public static String jrrStartShowExceptionInSwingS = "jrrStartShowExceptionInSwing";
    public static String jrrAddUserSettingsDirS = "jrrAddUserSettingsDir";
    public static String jrrIdeaRedirectOutToFileAuxS = "jrrIdeaRedirectOutToFileAux";
    public static String jrrIdeaDumpEnvAuxS = "jrrIdeaDumpEnvAux";
    public static String jrrIdeaForceUseStdS = "jrrIdeaForceUseStd";
    public static String jrrIdeaOutputPathS= "jrrIdeaOutputPath";
    public static String jrrPrintJavaHomeS= "jrrPrintJavaHome";
    public static String jrrpathS = "jrrpath";

    public static volatile File userHome = new File(System.getProperty("user.home"));
    public static File jrrlibpathF = new File(userHome, "jrr/configs/jrrlibpath.txt");
    public static File buildLogBefore = new File(userHome,  System.getProperty(jrrIdeaOutputPathS, "idea_build_jrr_log.txt"));

    public static volatile boolean jrrPrintJavaHome = ("true".equalsIgnoreCase(System.getProperty(jrrPrintJavaHomeS)));
    public static volatile boolean jrrIdeaDumpEnvAux = (!"false".equalsIgnoreCase(System.getProperty(jrrIdeaDumpEnvAuxS)));
    public static volatile boolean redirectOutToFileAux = ("true".equalsIgnoreCase(System.getProperty(jrrIdeaRedirectOutToFileAuxS)));
    public static volatile boolean jrrIdeaForceUseStd = ("true".equalsIgnoreCase(System.getProperty(jrrIdeaForceUseStdS)));
    public static volatile boolean useOneJar = !("false".equalsIgnoreCase(System.getProperty(jrrUseOneJarS)));
    public static volatile boolean startOriginal = !("false".equalsIgnoreCase(System.getProperty(jrrStartOriginalIfJrrMissingS)));
    public static volatile boolean originalTried = false;
    public static volatile boolean jrrStartShowExceptionInSwing = !("false".equalsIgnoreCase(System.getProperty(jrrStartShowExceptionInSwingS)));
    public static volatile boolean addUserSettingsDir = !("false".equalsIgnoreCase(System.getProperty(jrrAddUserSettingsDirS)));
    public static volatile URLClassLoader groovyCl;
    public static volatile File jrrpathF;
    public static volatile File outputFile;
    public static volatile Runnable beforeMainOriginalRun;

    @Deprecated
    public static volatile File ideaBuilderConfigDir = new File(userHome, "jrr/configs/");

    public static volatile String ideaBuilderConfigFileName = "ideaBuilder.groovy";

    @Deprecated
    public static volatile File ideaBuilderConfigFile = new File(userHome, "jrr/configs/"+ideaBuilderConfigFileName);



    @Deprecated
    public static volatile File userSettingsDir = new File(userHome, "jrr/configs2/classes/");
    public static Date startDate = new Date();


    public static volatile List<String> argsPv2;

    public static PrintStream originalOut = System.out;
    public static PrintStream originalErr = System.err;
    public static volatile PrintStream jrrOutStream;



}
