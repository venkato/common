package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator2
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2
import net.sf.jremoterun.utilities.groovystarter.GroovyRunnerConfigurator2
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
abstract class Writer6Sub extends Writer5Class {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String className = 'Config'
    public static String varName = 'b'
    public String varNameThis = varName

    Writer6Sub() {
        addImport(GroovyRunnerConfigurator2)
        addImport(CompileStatic)
        addImport(ClasspathConfigurator2)
        addImport(GroovyConfigLoader2)
        addImport(AddFilesToClassLoaderGroovy)
    }


    abstract Class getConfigClass();

    @Override
    String buildResult() {
        addImport(getConfigClass())
        return super.buildResult()
    }

    @Override
    String getClassDeclarationName() {
        Class configClass2 = getConfigClass()
        if(configClass2 == AddFilesToClassLoaderGroovy){
            return "class ${classNameGenerated} extends ${ClasspathConfigurator2.simpleName}  {".toString()
        }
        return "class ${classNameGenerated} extends ${GroovyConfigLoader2.simpleName}<${configClass2.simpleName}> {".toString()
    }


    @Override
    String getMainMethod() {
        MethodClosure method = GroovyConfigLoader2.loadConfigMethod
        return "void ${method.getMethod()}(${getConfigClass().simpleName} ${varNameThis}){".toString()
    }

}
