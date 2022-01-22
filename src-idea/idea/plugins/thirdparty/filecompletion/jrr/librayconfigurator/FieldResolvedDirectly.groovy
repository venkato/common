package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.psi.PsiClass
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2

import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazyImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.MavenIdAndRepoCustomSourceContains
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ToFileRefIfDownload
import net.sf.jremoterun.utilities.nonjdk.git.GitBaseRef
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect
import net.sf.jremoterun.utilities.nonjdk.git.ToFileRefRedirect2;

import java.util.logging.Logger;

@CompileStatic
class FieldResolvedDirectly {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    /**
     * Direct means - field is static.
     */
    public static FieldResolvedDirectly fieldResolvedDirectly = new FieldResolvedDirectly();


    public HashSet<String> fileEnumInterafces = new HashSet<>()
    public HashSet<String> directClasses = new HashSet<>()
    public HashSet<String> directEnumClasses = new HashSet<>()

    public ClassLoader classLoader = getClass().getClassLoader()

    FieldResolvedDirectly() {
        fileEnumInterafces.add(ToFileRef2.getName())
        fileEnumInterafces.add(ToFileRefIfDownload.getName())
//        fileEnumInterafces.add(ToFileRefIfDownloaded.getName())
        fileEnumInterafces.add(GitBaseRef.getName())
        fileEnumInterafces.add(MavenIdContains.getName())
        fileEnumInterafces.add(MavenIdAndRepoContains.getName())
        fileEnumInterafces.add(MavenIdAndRepoCustomSourceContains.getName())
        fileEnumInterafces.add(ToFileRefRedirect.getName())
        fileEnumInterafces.add(net.sf.jremoterun.utilities.classpath.BinaryWithSourceI.getName())
    }

    void addDirectEnumClass(ClRef clazz){
        directEnumClasses.add(clazz.className)
    }

    void addDirectEnumClass(Class clazz){
        directEnumClasses.add(clazz.getName())
    }

    boolean canResolveEnum(String className){
        log.info "checking can resolve enum : ${className} "
        return directEnumClasses.contains(className);
    }


    void addDirectClass(ClRef clazz){
        directClasses.add(clazz.className)
    }

    void addDirectClass(Class clazz){
        directClasses.add(clazz.getName())
    }

    boolean tryResolveEnumIfSuperClass(PsiClass declaredType){
        PsiClass[] interfaces1 = declaredType.getInterfaces()
        PsiClass find23 = interfaces1.toList().find { fileEnumInterafces.contains(it.getQualifiedName()) }
        return find23!=null
    }

    Object tryResolveEnumIfCan(String className,String fieldName){
        return resolveValue2(className,fieldName)
    }


    boolean canResolveAny(String className,String fieldName){
        return  canResolve(className,fieldName) || canResolveEnum(className)
    }

    boolean canResolve(String className,String fieldName){

        boolean b =  directClasses.contains(className)
        log.info "can resolve ${b} for ${className} ${fieldName}"
        return b
    }

    Class loadClass(String className){
        Class<?> clazz = classLoader.loadClass(className)
        return clazz
    }




//    private File resolveValueNotUsed(String className,String fieldName){
//        Object fieldValue = resolveValue2(className,fieldName)
//        if (fieldValue instanceof File) {
//            File file1 = (File) fieldValue;
//            return file1
//        }
//        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
//        if(handler==null){
//            throw new IllegalStateException("customObjectHandler was not set")
//        }
//        File f =  handler.resolveToFileIfDownloaded(fieldValue)
//        if(f==null){
//            log.info "seems not download ${fieldName} ${className} "
//            return null
//        }
//        log.info "resolved ${fieldName} : ${f}"
//        return f
//    }


    ChildFileLazy resolveValue3(String className,String fieldName){
        Object value2 = resolveValue2(className, fieldName)
        if(value2==null){
            throw new NullPointerException("value is null for ${className} ${fieldName}")
        }
        return resolveValue4(value2)
//        if (value2 instanceof File) {
//            return new FileToFileRef(value2)
//        }
//        if (value2 instanceof ChildFileLazy) {
//            ChildFileLazy childdd = (ChildFileLazy) value2;
//            return childdd
//        }
//        if (value2 instanceof ToFileRef2) {
//            ToFileRef2 r = (ToFileRef2) value2;
//            return new ChildFileLazyImpl(r);
//        }
//        throw new UnsupportedOperationException("${className} ${fieldName}")
    }

    ChildFileLazy resolveValue4(Object value2){
        assert value2!=null
        if (value2 instanceof File) {
            return new FileToFileRef(value2)
        }
        if (value2 instanceof ChildFileLazy) {
            ChildFileLazy childdd = (ChildFileLazy) value2;
            return childdd
        }
        if (value2 instanceof ToFileRef2) {
            ToFileRef2 r = (ToFileRef2) value2;
            return new ChildFileLazyImpl(r);
        }
        if (value2 instanceof ToFileRefRedirect2) {
            ToFileRef2 redirect2 = value2.getRedirect()
            if(redirect2==null){
                throw new Exception("Redicrect is null")
            }
            return resolveValue4(redirect2);
        }
        throw new UnsupportedOperationException("${value2.getClass().getName()} ${value2}")
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
