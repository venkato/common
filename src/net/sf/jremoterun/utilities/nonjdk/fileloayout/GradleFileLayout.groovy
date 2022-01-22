package net.sf.jremoterun.utilities.nonjdk.fileloayout

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildRedirect
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider

@CompileStatic
enum GradleFileLayout implements EnumNameProvider{
    gradlewWindows('gradlew.bat'),
    gradleWindows('gradle.bat'),
    gradleLinux('gradle'),
    gradlewLinux('gradlew'),

    jarsDir('.gradle/caches/modules-2/files-2.1')
    ;

    String customName;

    GradleFileLayout(String customName) {
        this.customName = customName
    }

}
