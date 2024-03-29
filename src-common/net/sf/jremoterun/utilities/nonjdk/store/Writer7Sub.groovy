package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
class Writer7Sub extends Writer6Sub {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Class configClass ;

    Writer7Sub(Class configClass) {
        this.configClass = configClass
    }

    @Override
    Class getConfigClass() {
        return configClass
    }
}
