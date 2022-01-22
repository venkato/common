package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef;

import java.util.logging.Logger;

@CompileStatic
class FieldResolvedDirectly {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    /**
     * Direct means - field is static.
     */
    public static FieldResolvedDirectly fieldResolvedDirectly = new FieldResolvedDirectly();


    public HashSet<String> directClasses = new HashSet<>()
    public HashSet<String> directEnumClasses = new HashSet<>()

    public ClassLoader classLoader = getClass().getClassLoader()




    void addDirectEnumClass(ClRef clazz){
        directEnumClasses.add(clazz.getName())
    }

    void addDirectEnumClass(Class clazz){
        directEnumClasses.add(clazz.getName())
    }

    boolean canResolveEnum(String className){
        log.info "checking can resolve enum : ${className} "
        return directEnumClasses.contains(className);
    }


    void addDirectClass(ClRef clazz){
        directClasses.add(clazz.getName())
    }

    void addDirectClass(Class clazz){
        directClasses.add(clazz.getName())
    }


    boolean canResolveAny(String className,String fieldName){
        return  canResolve(className,fieldName) || canResolveEnum(className)
    }

    boolean canResolve(String className,String fieldName){
        log.info "checking can resolve : ${className} ${fieldName}"
        return directClasses.contains(className)
    }

    Class loadClass(String className){
        Class<?> clazz = classLoader.loadClass(className)
        return clazz
    }




    private File resolveValueNotUsed(String className,String fieldName){
        Object fieldValue = resolveValue2(className,fieldName)
        if (fieldValue instanceof File) {
            File file1 = (File) fieldValue;
            return file1
        }
//        if (!(fieldValue instanceof GitSpec)) {
//            log.info "not git spec : ${fieldValue}"
//            return null
//        }
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        File f =  handler.resolveToFileIfDownloaded(fieldValue)
        if(f==null){
            log.info "seems not download ${fieldName} ${className} "
            return null
        }
        log.info "resolved ${fieldName} : ${f}"
        return f
    }


    ChildFileLazy resolveValue3(String className,String fieldName){
        Object value2 = resolveValue2(className, fieldName)
        if(value2==null){
            throw new NullPointerException("value is null for ${className} ${fieldName}")
        }

        if (value2 instanceof File) {
            return new FileToFileRef(value2)
        }
        if (value2 instanceof ChildFileLazy) {
            ChildFileLazy childdd = (ChildFileLazy) value2;
            return childdd
        }
        throw new UnsupportedOperationException("${className} ${fieldName}")
    }

    Object resolveValue2(String className,String fieldName){
        Thread thread = Thread.currentThread();
        ClassLoader loaderBefore = thread.getContextClassLoader()
        thread.setContextClassLoader(classLoader)
        try {
            Class<?> clazz = classLoader.loadClass(className)
            return JrrClassUtils.getFieldValue(clazz, fieldName)
        }finally{
            thread.setContextClassLoader(loaderBefore)
        }

    }





}
