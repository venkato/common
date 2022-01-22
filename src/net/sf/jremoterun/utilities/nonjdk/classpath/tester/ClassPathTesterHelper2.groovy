package net.sf.jremoterun.utilities.nonjdk.classpath.tester

import groovy.transform.CompileStatic
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.UrlCLassLoaderUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.UrlCLassLoaderUtils2
import net.sf.jremoterun.utilities.nonjdk.dateutils.DurationConstants
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorI
import net.sf.jremoterun.utilities.nonjdk.problemchecker.ProblemCollectorIThrowImmediate

import java.util.logging.Logger

@CompileStatic
class ClassPathTesterHelper2 {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassLoader currentClassLoader = ClassPathTesterHelper2.getClassLoader();

    static MavenCommonUtils mavenCommonUtils = new MavenCommonUtils();

    ProblemCollectorI problemCollector

    ClassPathTesterHelper2(ProblemCollectorI problemCollector) {
        this.problemCollector = problemCollector
    }

    void checkClassInstanceOf5(Object object, Class instanceOf) {
        Class<?> aClass = object.getClass();
        URL location = JrrUtils.getClassLocation(aClass);
        String msg = instanceOf.getName() + " class : " + aClass.getName() + " , location " + location;
        if (aClass != instanceOf) {
            addProblem(instanceOf.name, msg);
        }
//        log.info(msg);
    }

//    public void checkNoSuchClass(ClRef className){
//        checkNoSuchClass(className.className)
//    }


    static  ClassPathTesterHelper2 createClassPathTesterHelper2(){
        return new ClassPathTesterHelper2(new ProblemCollectorIThrowImmediate());
    }

    void checkNoSuchClass(ClRef className) {
        checkNoSuchClass5(className, currentClassLoader);
    }


    Class checkClassLoaded(ClRef className, ClassLoader classLoader) {
        try {
            Class<?> clazz = className.loadClass(classLoader)
            return clazz
        } catch (Throwable e) {
            addProblem2(className.className, "failed load class : ${className}", e)
            return null
        }
    }


    boolean checkTheSameClassLoader5(Class clazz, ClassLoader classLoader) {
        ClassLoader classLoader1 = clazz.getClassLoader()
        if (classLoader1 != classLoader) {
            if (classLoader1 == null) {
                addProblem(clazz.name, "Class ${clazz.name} loaded by boot classloader");
            }
            addProblem(clazz.name, "Class ${clazz.name} loaded by diff classloader ${classLoader1.class.name} ${classLoader1}");
            return false
        }
        return true
    }

    void checkInstanceOf(Class obj, ClRef superCLass, ClassLoader classLoader) {
        Class<?> superCLass2 = classLoader.loadClass(superCLass.className)
        boolean  b = superCLass2.isAssignableFrom(obj)
        if(!b){
            addProblem(obj.getName(), "Class ${obj.name} failed cast to ${superCLass}");
        }
    }

    boolean checkTheSameClassLoader5(ClRef clName1, ClassLoader classLoader) {
        Class  clazz = classLoader.loadClass(clName1.className)
        ClassLoader classLoader1 = clazz.getClassLoader()
        if (classLoader1 != classLoader) {
            if (classLoader1 == null) {
                addProblem(clazz.name, "Class ${clazz.name} loaded by boot classloader");
            }
            addProblem(clazz.name, "Class ${clazz.name} loaded by diff classloader ${classLoader1.class.name} ${classLoader1}");
            return false
        }
        return true
    }


    void checkClassLoaderHierarchy(Class clazz, ClassLoader parentClassLoader, ClassLoader childClassLoader) {
        if (checkTheSameClassLoader5(clazz, parentClassLoader)) {
            checkNotSameClassLoader5(clazz, childClassLoader)
        }
    }


    void checkNotSameClassLoader5(ClRef clazz, ClassLoader classLoader) {
        Class clazz1 = checkClassLoaded(clazz, classLoader)
        if (clazz1 != null) {
            checkNotSameClassLoader5(clazz1, classLoader)
        }
    }

    void checkNotSameClassLoader5(Class clazz, ClassLoader classLoader) {
        Class<?> clazz1 = classLoader.loadClass(clazz.name)
        ClassLoader classLoader1 = clazz1.getClassLoader()
        if (classLoader1 == classLoader) {
            addProblem(clazz.name, "Class ${clazz.name} loaded by unwanted classloader ${classLoader1.class.name} ${classLoader1}");
        }
    }

    URL checkResourceExists(String resource) {
        URL resourceUrl = JrrClassUtils.currentClassLoader.getResource(resource)
        if (resourceUrl == null) {
            addProblem(null, "Resource not found : ${resource}")
        }
        return resourceUrl;
    }

    URL checkResourceExistsOnce(String resource) {
        List<URL> resourceUrls = JrrClassUtils.currentClassLoader.getResources(resource).toList()
        int size = resourceUrls.size()
        if (size == 0) {
            addProblem(null, "Resource not found : ${resource}")
            return null
        }
        if (size > 1) {
            addProblem(null, "Resource found many : ${resourceUrls}")
            return null
        }
        return resourceUrls[0]
    }

    void checkResourceExistsOnceAndLocation(String resource, URL location) {
        URL resourceUrl = checkResourceExistsOnce(resource)
        if (resourceUrl != null) {
            if (resourceUrl != location) {
                addProblem(null, "Resource ${resource} location not matched : ${location} , found at = ${resourceUrl}")
            }
        }
    }

    void checkResourceLocation(String resource, URL location) {
        URL resourceUrl = checkResourceExists(resource)
        if (resourceUrl != null) {
            if (resourceUrl != location) {
                addProblem(null, "Resource ${resource} location not matched : ${location}")
            }
        }
    }

    void checkFileExist(File f) {
        if (!f.exists()) {
            addProblem(null, "File not found : ${f}")
        }
    }

    void checkNoSuchClass5(ClRef className, ClassLoader classLoader) {
        try {
            Class<?> clazz = classLoader.loadClass(className.className);
            checkTheSameClassLoader5(clazz, classLoader)
            File location = UrlCLassLoaderUtils.getClassLocation(clazz);
            addProblem(clazz.name, "${className.className} is present : ${location}");
        } catch (ClassNotFoundException e) {

        }
    }

    void checkFieldExists5(Class clazz, String field) {
        try {
            JrrClassUtils.findField(clazz, field); // NOFIELDCHECK
        } catch (NoSuchFieldException e) {
            File location = UrlCLassLoaderUtils.getClassLocation(clazz);
            addProblem2(clazz.name, "In class " + clazz.getName() + " field not found " + field + " , location " + location, e);
        }
    }


    void checkClassOnceIfPresent(ClRef clazz, MavenIdContains mavenId) {
        try {
            Class class2 = clazz.loadClass2()
            checkClassOnce5(class2,mavenId)
        }catch (ClassNotFoundException e){
            log.debug3(clazz,e)
        }
    }

    void checkClassOnce5(Class clazz, MavenIdContains mavenId) {
        checkClassOnce5(clazz, mavenCommonUtils.findMavenOrGradle(mavenId.getM()))
    }

    void checkClassOnce5(Class clazz) {
        checkClassOnce51(clazz,false)
    }

    void checkClassOnce51(Class clazz,boolean ignoreSameFiles) {
        List<File> files = UrlCLassLoaderUtils2.getClassLocationAll2(clazz);
        if (files.size() == 0) {
            addProblem(clazz.name, "class not found : ${clazz.getName()}")
        }
        if(ignoreSameFiles){
            files = files.unique()
        }
        if (files.size() > 1) {
            addProblem(clazz.name, "found many files for class ${clazz.getName()} : ${files}")
        }
    }

    public static long lastModifiedCacheMax = DurationConstants.oneWeek.timeInMsLong;

    void checkClassPresentInCache(Class clazz, File cachePath) {
        File classInCache = cachePath.child(clazz.getName().replace('.', '/') + ClassNameSuffixes.dotclass.customName)
        if (classInCache.exists()) {
            long lastModified1 = classInCache.lastModified()
            long lastModifiedDiff = System.currentTimeMillis() - lastModified1
            if(lastModifiedDiff>lastModifiedCacheMax){
                addProblem(clazz.getName(), "Class ${clazz.getName()} in cache too old : ${new Date(lastModified1)}")
            }
            List<File> all2 = UrlCLassLoaderUtils2.getClassLocationAll2(clazz)
            if (all2.size() == 0) {
                addProblem(clazz.getName(), "Failed find class ${clazz.getName()}")
            } else {
                if (cachePath.isChildFile(all2.first())) {
                    addProblem(clazz.getName(), "Class ${clazz.getName()} used from cache")
                } else {
                    File found = all2.find { cachePath.isChildFile(it) }
                    if (found == null) {
                        addProblem(clazz.getName(), "Class ${clazz.getName()} not found in cache, found in ${all2}")
                    }
                }
            }
        } else {
            addProblem(clazz.getName(), "Class not in cache :  ${classInCache}");
        }
    }


    void checkClassNotFromPath5(Class clazz, File notPath) {
        if (!notPath.exists()) {
            addProblem(clazz.name, "File not found : ${notPath}")
        }
        List<File> files = UrlCLassLoaderUtils2.getClassLocationAll2(clazz);
        switch (files.size()) {
            case 0:
                throw new Exception("class was not found : ${clazz.name}")
            case 1:
                File actualFile = files[0]
                if (actualFile == null) {
                    throw new Exception("actual file is null for ${clazz}")
                }
                actualFile = actualFile.canonicalFile.absoluteFile
                String actualPathS = actualFile.absolutePath;
                String notPathS = notPath.absolutePath
                if (actualPathS == notPathS) {
                    addProblem2(clazz.name, "Class ${clazz.getName()} from undesired path : ${notPath}", new UndesiredClassLocationException("Class ${clazz.getName()} from undesired path : ${notPath}"))
                }
                break;
            default:
                files = files.collect { it.canonicalFile }
                addProblem(clazz.name, "found many path ${files.size()} : ${files.join(' , ')}")
        }

    }

    void checkClassOnce5(Class clazz, File expectedFile) {
        if (expectedFile == null) {
            throw new Exception("expectedFile is null for ${clazz}")
        }
        if (!expectedFile.exists()) {
            throw new Exception("expectedFile ${expectedFile} not exists for ${clazz}")
        }
        expectedFile = expectedFile.canonicalFile.absoluteFile
        List<File> files = UrlCLassLoaderUtils2.getClassLocationAll2(clazz);
        switch (files.size()) {
            case 0:
                addProblem(clazz.name, "class was not found : ${clazz.name}")
            case 1:
                File actualFile = files[0]
                if (actualFile == null) {
                    throw new Exception("actual file is null for ${clazz} ${expectedFile}")
                }
                actualFile = actualFile.canonicalFile.absoluteFile
                if (actualFile != expectedFile) {
                    addProblem(clazz.name, "actualFile != expectedFile : ${actualFile} != ${expectedFile}")
                }
                break;
            default:
                files = files.collect { it.canonicalFile }
                addProblem(clazz.name, "found many path ${files.size()} : ${files.join(' , ')}")
        }
    }

    void checkClassLocation5(Class clazz, MavenIdContains mavenId) {
        checkClassLocation5(clazz, mavenCommonUtils.findMavenOrGradle(mavenId.m));
    }

    void checkClassLocation5(ClRef clazz, File locationExpected,ClassLoader classLoader1) {
        if(classLoader1==null){
            classLoader1 = JrrClassUtils.getCurrentClassLoader();
        }
        Class loadedClass = checkClassLoaded(clazz, classLoader1)
        if(loadedClass!=null){
            checkClassLocation5(loadedClass,locationExpected)
        }
    }

    void checkClassLocation5(Class clazz, File locationExpected) {
        File currentLocation = UrlCLassLoaderUtils.getClassLocation(clazz);
        if (locationExpected != currentLocation) {
            addProblem(clazz.name, "class ${clazz.getName()} location != extected :  ${currentLocation} != ${locationExpected}")
//            throw new Exception("class " + clazz.getName() + " location strange " + currentLocation);
        }

    }

    void addProblem(String className, String msg) {
        ProblemInfoClass problemInfoClass = new ProblemInfoClass()
        problemInfoClass.clazz = className
        problemInfoClass.msg = msg
        problemInfoClass.stackTrace = new JustStackTrace()
        addProblemImpl(problemInfoClass)
    }


    void addProblemJustMsg(String msg) {
        ProblemInfoClass problemInfoClass = new ProblemInfoClass()
        problemInfoClass.msg = msg
        problemInfoClass.stackTrace = new JustStackTrace()
        addProblemImpl(problemInfoClass)
    }


    void addProblem2(String className, String msg, Throwable exception) {
        ProblemInfoClass problemInfoClass = new ProblemInfoClass()
        problemInfoClass.clazz = className
        problemInfoClass.msg = msg
        problemInfoClass.stackTrace = exception
        addProblemImpl(problemInfoClass)
    }

    void addProblemImpl(ProblemInfoClass problemInfoClass) {
        problemCollector.addProblemImpl(problemInfoClass)
    }



    void loadTester(ClRef classIfPresent,ClRef testerClass){
        try {
            classIfPresent.loadClass2()
            Runnable r= testerClass.loadClass2().newInstance(this)
            r.run()
        }catch (ClassNotFoundException e){
            log.debug3('',e)
        }
    }

}
