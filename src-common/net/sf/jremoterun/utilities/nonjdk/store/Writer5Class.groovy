package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator2
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
abstract class Writer5Class extends Writer3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    //static MethodClosure addCpMethod = (MethodClosure) (Closure) ClasspathConfigurator.&addCp;


    Writer5Class() {
//        addImport(GroovyRunnerConfigurator2)
//        addImport(CompileStatic)
        //addImport(ClasspathConfigurator2)
    }




//    @Deprecated
//    @Override
//    String generateGetProperty(String propName) {
//        return " getVar('${propName}') "
//    }
}
