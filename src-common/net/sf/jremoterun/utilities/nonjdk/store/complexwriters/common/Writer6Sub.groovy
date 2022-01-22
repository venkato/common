package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderGroovy
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator2I
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2I
import org.codehaus.groovy.runtime.MethodClosure

import java.lang.reflect.TypeVariable
import java.util.logging.Logger

@CompileStatic
abstract class Writer6Sub extends Writer3 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static MethodClosure loadConfigMethod = (MethodClosure) (Closure) GroovyConfigLoader2I.&loadConfig;

    Writer6Sub() {
    }

    abstract Class getMainMethodArgType();

    @Override
    String getClassDeclarationName() {
        Class configClass2 = getMainMethodArgType()
        assert configClass2 != null
        Class class2 = getSuperClass2(configClass2)
//        addImport(class2)
//        addImport(configClass2)

        String typeSuffix
        if (!isWriteTemplate(class2)) {
            typeSuffix = ''
        } else {
            typeSuffix = "<${writeTemplate2()}>"
        }
        boolean implementsUsed
        String implOrEx
        if (class2.isInterface()) {
            implOrEx = "implements"
            implementsUsed = true
        } else {
            implOrEx = 'extends'
            implementsUsed = false
        }
        String interfaces34 = addInterfaces()
        if(interfaces34.length()>1){
            if(implementsUsed){
                interfaces34=', '+interfaces34
            }else{
                interfaces34=' implements '+interfaces34
            }
        }
        return "class ${classNameGenerated} ${implOrEx} ${addImportWithName(class2)}${typeSuffix} ${interfaces34}{"
    }

    String addInterfaces(){
        return ''
    }

    boolean isWriteTemplate(Class class2){
        TypeVariable[] parameters1 = class2.getTypeParameters()
        if (parameters1 == null || parameters1.length == 0) {
            return false
        }
        return true
    }

    Class getSuperClass2(Class configClass2) {
        if (configClass2 == AddFilesToClassLoaderGroovy) {
            return ClasspathConfigurator2I
        }
        return GroovyConfigLoader2I

    }

    String writeTemplate2() {
        return writeTemplate(getMainMethodArgType())
    }

    String writeTemplate(Class configClass2) {
        return addImportWithName(configClass2)
    }


    @Override
    String getMainMethod() {
        return generateSyntheticMethod(loadConfigMethod.getMethod())
    }

    @Override
    String generateSyntheticMethod(String methodName) {
        return "void ${methodName}(${writeTemplate2()} ${varNameThis}){".toString()
    }
}
