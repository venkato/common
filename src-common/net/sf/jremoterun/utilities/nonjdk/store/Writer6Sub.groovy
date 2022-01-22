package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator2I
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2I
import org.codehaus.groovy.runtime.MethodClosure

import java.lang.reflect.TypeVariable
import java.util.logging.Logger

@CompileStatic
abstract class Writer6Sub extends Writer5Class {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Deprecated
    public static String className = 'Config'


    public static MethodClosure loadConfigMethod = (MethodClosure) (Closure) GroovyConfigLoader2I.&loadConfig;

    Writer6Sub() {
        //addImport(GroovyRunnerConfigurator2)
        //addImport(CompileStatic)
//        addImport(ClasspathConfigurator2)
//        addImport(GroovyConfigLoader2)
//        addImport(GroovyConfigLoader2I)
        //addImport(AddFilesToClassLoaderGroovy)
    }


    abstract Class getConfigClass();

    @Override
    String getClassDeclarationName() {
        Class configClass2 = getConfigClass()
        assert configClass2 != null
        Class class2 = getSuperClass2(configClass2)
        addImport(class2)
        addImport(configClass2)

        String typeSuffix
        TypeVariable[] parameters1 = class2.getTypeParameters()
        if (parameters1 == null || parameters1.length == 0) {
            typeSuffix = ''
        } else {
            typeSuffix = "<${writeTemplate2()}>"
        }
        String implOrEx
        if (class2.isInterface()) {
            implOrEx = "implements"
        } else {
            implOrEx = 'extends'
        }
        return "class ${classNameGenerated} ${implOrEx} ${class2.getSimpleName()}${typeSuffix} {"
    }

    Class getSuperClass2(Class configClass2) {
        if (configClass2 == AddFilesToClassLoaderGroovy) {
            return ClasspathConfigurator2I
        }
        return GroovyConfigLoader2I

    }

    String writeTemplate2() {
        return writeTemplate(getConfigClass())
    }

    String writeTemplate(Class configClass2) {
        return configClass2.getSimpleName()
    }


    @Override
    String getMainMethod() {
        //MethodClosure method1 = loadConfigMethod
        return generateSyntheticMethod(loadConfigMethod.getMethod())
    }



    @Override
    String generateSyntheticMethod(String methodName) {
        return "void ${methodName}(${writeTemplate2()} ${varNameThis}){".toString()
    }
}
