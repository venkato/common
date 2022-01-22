package net.sf.jremoterun.utilities.nonjdk.idea

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPPattern
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum IdeaFilesLayout  implements ChildRedirect,EnumNameProvider{
    classPathIndex('classpath.index'),
    runnerWin64('bin/idea64.exe'),
    terminal("plugins/terminal/lib/terminal.jar"),
    lib("lib/"),
    groovyLib('plugins/Groovy/lib/'),
    kotlinLib('plugins/Kotlin/lib'),
    pluginJavaLib('plugins/java/lib/'),
    ideaJpsLauncher('plugins/java/lib/jps-launcher.jar'),
    idea64ExeOptions('bin/idea64.exe.vmoptions'),


    androidJar ( "plugins/android/lib/android.jar"),
    gradleLibDir ( "plugins/gradle/lib/"),
    gradleJar ( "plugins/gradle/lib/gradle.jar"),
    gradleToolingExtApi ( "plugins/gradle/lib/gradle-tooling-extension-api.jar"),
    gradleToolingExtImpl ( "plugins/gradle/lib/gradle-tooling-extension-impl.jar"),
    gradleDepsUpdater ( "plugins/gradle-dependencyUpdater/lib/gradle-dependencyUpdater.jar"),
    /**
     * Located in c:/Users/user/AppData/Local/JetBrains/IdeaIC2023.1
     */
    buildLog('log/build-log/build-log-jul.properties'),
    /**
     * Located in c:/Users/user/AppData/Roaming/JetBrains/IdeaIC2023.1
     */
    psiViewer3PartyPlugin("plugins/psiviewer/lib/PsiViewer.jar"),

    ;

    String customName;

    ExactChildPattern ref;

    IdeaFilesLayout(String customName) {
        this.customName = customName
        ref=new ExactChildPattern(customName)
    }

}
