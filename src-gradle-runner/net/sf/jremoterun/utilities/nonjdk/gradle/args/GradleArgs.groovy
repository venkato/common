package net.sf.jremoterun.utilities.nonjdk.gradle.args

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.enumutils.EnumNameProvider;

import java.util.logging.Logger;

@CompileStatic
enum GradleArgs implements EnumNameProvider {
    org_gradle_java_home,
    org_gradle_jvmargs,
    plain,
    _info,
    __no_daemon,
    __console,
    __stacktrace,
    ;


    String customName;

    GradleArgs() {
        String name1 = name()
        if (name1.startsWith('_')) {
            customName = name1.replace('_', '-');
        } else {
            customName = name1.replace('_', '.');
        }

    }


}
