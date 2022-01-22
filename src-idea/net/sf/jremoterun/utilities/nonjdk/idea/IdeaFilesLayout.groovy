package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.console.auxp.AddFilesToClassLoader
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithAndEndsChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithAndEndsExcludeChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.StartWithChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum IdeaFilesLayout implements ChildRedirect, EnumNameProvider {
    classPathIndex('classpath.index'),
    runnerWin64('bin/idea64.exe'),
    runnerLinux('bin/idea.sh'),
    terminal("plugins/terminal/lib/terminal.jar"),
    jbr("jbr/"),
    lib("lib/"),
    groovyLib('plugins/Groovy/lib/'),
    kotlinLib('plugins/Kotlin/lib'),
    kotlinPlugin('plugins/Kotlin/lib/kotlin-plugin.jar'),
    pluginJavaLib('plugins/java/lib/'),
    pluginJavaJpsBuilder('plugins/java/lib/jps-builders.jar'),
    ideaJpsLauncher('plugins/java/lib/jps-launcher.jar'),
    idea64ExeOptions('bin/idea64.exe.vmoptions'),


    @Deprecated
    androidJar("plugins/android/lib/android.jar"),
    gradleLibDir("plugins/gradle/lib/"),
    gradleJar("plugins/gradle/lib/gradle.jar"),
    gradleToolingExtApi("plugins/gradle/lib/gradle-tooling-extension-api.jar"),
    gradleToolingExtImpl("plugins/gradle/lib/gradle-tooling-extension-impl.jar"),
    gradleDepsUpdater("plugins/gradle-dependencyUpdater/lib/gradle-dependencyUpdater.jar"),

    /**
     * Located in c:/Users/user/AppData/Local/JetBrains/IdeaIC2023.1
     */
    buildLog('log/build-log/build-log-jul.properties'),
    /**
     * Located in c:/Users/user/AppData/Roaming/JetBrains/IdeaIC2023.1
     */
    @Deprecated
    psiViewer3PartyPlugin("plugins/psiviewer/lib/PsiViewer.jar"),

    ;

    String customName;

    ExactChildPattern ref;

    IdeaFilesLayout(String customName) {
        this.customName = customName
        ref = new ExactChildPattern(customName)

    }

    public static ExactChildPPattern psiViewer3PartyPluginPath = new ExactChildPattern('plugins/psiviewer/lib').childP(new StartWithAndEndsChildPattern('instrumented-psiviewer', '.jar'));
    public static ExactChildPPattern psiViewer3PartyPluginPath2025 = new ExactChildPattern('plugins/psiviewer/lib').childP(new StartWithAndEndsExcludeChildPattern('psiviewer-', '.jar','-searchableOptions.jar'));
//    public static ExactChildPPattern gradleApi = new ExactChildPattern('plugins/Groovy/lib/').childP(new StartWithAndEndsChildPattern('gradle-api-','.jar'));


    public static List<IdeaFilesLayout> addJarsDefault = [
            pluginJavaJpsBuilder,
            gradleJar,
            gradleToolingExtApi,
            gradleToolingExtImpl,
            kotlinPlugin,
            terminal,
    ]

    static void addFilesFromIdeaInstallDir(AddFilesToClassLoaderCommon b, File ideaProductDir) {
        b.addAllJarsInDir IdeaFilesLayout.groovyLib.ref.resolveChild(ideaProductDir)
        List<File> files = net.sf.jremoterun.utilities.nonjdk.idea.IdeaFilesLayout.addJarsDefault.collect { new File(ideaProductDir, it.customName) }
        b.addAll files
    }

}
