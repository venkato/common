package net.sf.jremoterun.utilities.nonjdk.gradle.runner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.classpath.ClRef
import org.gradle.api.initialization.dsl.ScriptHandler
import org.gradle.initialization.InitScript

@CompileStatic
class JrrGradleEnv {

    public static JrrGradleEnv gradleEnv = new JrrGradleEnv();


    public InitScript initScript;

    public ScriptHandler sh;
    public ClRef customHandler;

}
