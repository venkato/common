package net.sf.jremoterun.utilities.nonjdk.groovyruntime.utils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.control.CompilerConfiguration;

import java.util.logging.Logger;

@CompileStatic
class GroovyCompileConfigReceiver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static CompilerConfiguration receiveConfig(GroovyClassLoader cll){
        CompilerConfiguration compilerConfiguration = JrrClassUtils.getFieldValue(cll,'config') as CompilerConfiguration
        return compilerConfiguration;
    }

}
