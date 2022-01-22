package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix2
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.glavo.jimage.ImageReader

import java.util.logging.Logger

@CompileStatic
class DuplicateClassesDetector {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public HashSet<ClassSlashNoSuffix> classesNamesUsedInApp;
    public Map<File, List<ClassSlashNoSuffix2>> classesOnLocation = [:];
    public Map<File, Set<ClassSlashNoSuffix2>> classesOnLocationUsed = [:];
    public Map<ClassSlashNoSuffix, List<ClassLocaltionInfo>> classesOnLocationReverse = [:];
    public Map<ClassSlashNoSuffix, List<ClassLocaltionInfo>> problemClasses = [:];
    public List<ClassSlashNoSuffix> missedClasses = [];
    public Map<ClassSlashNoSuffix, ClassLocaltionInfo> foundClasses = [:];
    public boolean treatMutiAsDup = false;
    public HashMap<ClassSlashNoSuffix, List<ClassLocaltionInfo>> classesNamesMultiReleaseUsedInApp = new HashMap<>()

    DuplicateClassesDetector(HashSet<ClassSlashNoSuffix> classesNamesUsedInApp, Map<File, List<ClassSlashNoSuffix2>> classesOnLocation) {
        this.classesNamesUsedInApp = classesNamesUsedInApp
        this.classesOnLocation = classesOnLocation
        this.classesOnLocation.each {
            reverseOneEntry(it.getKey(), it.getValue());
        }
        check()
    }

    ClassLocaltionInfo getClassLocation(ClassSlashNoSuffix slashClass) {
        List<ClassLocaltionInfo> localtionInfos = classesOnLocationReverse.get(slashClass)
        if (localtionInfos != null && localtionInfos.size() > 0) {
            ClassLocaltionInfo localtionInfo
            List<ClassLocaltionInfo> notMultirelease = localtionInfos.findAll { !it.isMultiReleaseFile }
            if (notMultirelease.size() > 0) {
                localtionInfo = notMultirelease[0]
            } else {
                localtionInfo = localtionInfos[0]
            }
            return localtionInfo
        } else {
            //   log.info "failed find ${slashClass}"
        }
        return null
    }


    byte[] onFile(ClassLocaltionInfo localtionInfo) {
        //String suffix2 =
        if (localtionInfo.f.getName() == net.sf.jremoterun.utilities.nonjdk.classpath.classloader.GetClassesFromLocation.modulesFileName) {
            ImageReader imageReader = ImageReader.open(localtionInfo.f.toPath())
            try {

                byte[] bs = imageReader.getResource(localtionInfo.fullPathInJarSlash.convertClassNameToPath2())
                if (bs == null) {
                    log.info "failed get content for ${localtionInfo.fullPathInJarSlash}"
                }
                return bs
            } finally {
                JrrIoUtils.closeQuietly2(imageReader, log)
            }
        }

        if (localtionInfo.f.isDirectory()) {

            File cand = new File(localtionInfo.f, localtionInfo.fullPathInJarSlash.convertClassNameToPath2())
            if (cand.exists()) {
                return cand.bytes
            }
            return null
        }
        assert localtionInfo.f.isFile()
        return net.sf.jremoterun.utilities.nonjdk.ziputil.ZipReadEntry.extractOneEntry(localtionInfo.f, localtionInfo.fullPathInJarSlash.convertClassNameToPath2())
    }

    protected void reverseOneEntry(File file, List<ClassSlashNoSuffix2> classes) {
        classes.each {
            forEachClass(file, it)

        }
    }

    public static String metaPrefeix = 'META-INF/versions/'

    void forEachClass(File file, final ClassSlashNoSuffix2 className) {
        //String classname2 = className//.replace('.','/')
        //String locationSuffix = className //UsedByAnalysis.convertClassNameToPath2(className)

        ClassLocaltionInfo classLocaltionInfo
        if (file.getName() == GetClassesFromLocation.modulesFileName) {
            int moduleNameI = className.clName.indexOf('/', 1)
            assert moduleNameI > 0
            String moduleName = className.clName.substring(1, moduleNameI)
//            className1 = className.substring(moduleNameI + 1)
            classLocaltionInfo = new ClassLocaltionInfo(file, className)
        } else {
            if (className.clName.startsWith(metaPrefeix)) {
                String aaa = className.clName.substring(metaPrefeix.length())
                int of2 = aaa.indexOf('/')
                if (of2 == -1) {
                    throw new IllegalStateException(className.clName)
                }
//                className1 = aaa.substring(of2 + 1)//.replace('/','.')
                classLocaltionInfo = new ClassLocaltionInfo(file, className)
                classLocaltionInfo.isMultiReleaseFile = true
            } else {
                classLocaltionInfo = new ClassLocaltionInfo(file, className)
//                className1 = className
            }
        }
        ClassSlashNoSuffix className1 = getClassNameSlashFromJar(file, className)
        List<ClassLocaltionInfo> files = classesOnLocationReverse.get(className1)
        if (files == null) {
            files = []
            classesOnLocationReverse.put(className1, files)
        }
        files.add(classLocaltionInfo)

    }

    HashSet<ClassLocaltionInfo> convertClassesToLocation(Collection<ClassSlashNoSuffix> classNames1) {
        HashSet<ClassLocaltionInfo> result = new HashSet<>()
        classNames1.each {
            List<ClassLocaltionInfo> files = classesOnLocationReverse.get(it)
            result.addAll(files)
        }
        return result;
    }


    HashSet<File> getUsedLocations() {
        Map<File, Set<ClassSlashNoSuffix2>> used = classesOnLocationUsed.findAll { it.value.size() > 0 }
        HashSet<File> result = new HashSet<>()
        result.addAll(used.keySet());
        return result;
    }

    HashSet<File> getNonUsedLocations() {
        HashSet<File> result = new HashSet<>()
        Map<File, Set<ClassSlashNoSuffix2>> unused = classesOnLocationUsed.findAll { it.value.size() == 0 }
        result.addAll(unused.keySet())
        HashSet<File> files2 = new HashSet<File>(classesOnLocation.keySet())
        files2.removeAll(classesOnLocationUsed.keySet())
        result.addAll(files2)
        return result
    }


    protected void check() {
        classesNamesUsedInApp.each { handleOneClass(it) }
        classesOnLocation.each {

            File file22 = it.key
            HashSet<ClassSlashNoSuffix2> classesUsed = new HashSet<>()
            HashSet<ClassSlashNoSuffix2> classes = new HashSet<>(it.value)
            classes.each {
                ClassSlashNoSuffix className2 = getClassNameSlashFromJar(file22, it)
                if (classesNamesUsedInApp.contains(className2)) {
                    classesUsed.add(it)
                }
            }
            //classes.retainAll(classesNamesUsedInApp)
//            classes.retainAll(classesNamesUsedInApp.collect{it.replace('.','/')})
            classesOnLocationUsed.put(file22, classesUsed)
        }
    }

    ClassSlashNoSuffix getClassNameSlashFromJar(File file, ClassSlashNoSuffix2 className) {
        final String className2
        if (file.getName() == GetClassesFromLocation.modulesFileName) {
            int moduleNameI = className.clName.indexOf('/', 1)
            assert moduleNameI > 0
            String moduleName = className.clName.substring(1, moduleNameI)
            className2 = className.clName.substring(moduleNameI + 1)

        } else if (className.clName.startsWith(metaPrefeix)) {
            String aaa = className.clName.substring(metaPrefeix.length())
            int of2 = aaa.indexOf('/')
            if (of2 == -1) {
                throw new IllegalStateException(className.clName)
            }
            className2 = aaa.substring(of2 + 1)
        } else {
            className2 = className
        }
        return new ClassSlashNoSuffix(className2)

    }

    Map<String, List<String>> getClass2FilesMap() {
        Map<String, List<String>> json1 = new TreeMap<>();
        problemClasses.each {
            json1.put(it.key.clName, convertFiles(it.value.f))
        }
        foundClasses.each {
            json1.put(it.key.clName, [normalizeFile(it.value.f)])
        }
        missedClasses.each {
            json1.put(it.clName, [])
        }
        return json1;
    }

    String getClass2FilesJson() {
        String json = JsonOutput.toJson(getClass2FilesMap());
        json = JsonOutput.prettyPrint(json);
        return json;
    }

    @Deprecated
    static Map<String, List<String>> readJson(String text) {
        (Map) new JsonSlurper().parseText(text)
    }

    @Deprecated
    static List<String> getClassesByLocation(File file, Map<String, List<String>> map1) {
        String path1 = normalizeFile(file)
        List<String> result = []
        map1.each {
            if (it.value.contains(path1)) {
                result.add(it.key)
            }
        }
        return result
    }

    protected List<String> convertFiles(List<File> files) {
        return files.collect { normalizeFile(it) }
    }

    static String normalizeFile(File f) {
        return f.getAbsoluteFile().getCanonicalFile().getAbsolutePath().replace('\\', '/')
    }


    protected void handleOneClass(ClassSlashNoSuffix oneClass) {
        List<ClassLocaltionInfo> files = classesOnLocationReverse.get(oneClass)
        if (files == null) {
            missedClasses.add(oneClass)
//            classesOnLocationReverseUsed.put(oneClass,[])
        } else {
//            classesOnLocationReverseUsed.put(oneClass,files)
            boolean handle1 = true
            if (files.size() != 0) {
                if (!treatMutiAsDup) {
                    List<ClassLocaltionInfo> muti1 = files.findAll { it.isMultiReleaseFile }
                    if (muti1.size() > 0) {
                        List<ClassLocaltionInfo> get1 = classesNamesMultiReleaseUsedInApp.get(oneClass)
                        if (get1 == null) {
                            get1 = []
                            classesNamesMultiReleaseUsedInApp.put(oneClass, get1)
                        }
                        get1.addAll(muti1)
                    }
                    files = files.findAll { !it.isMultiReleaseFile }
                    if (files.size() == 0) {
                        handle1 = false

                    }
                }
            }
            if (handle1) {
                if (files.size() == 1) {
                    foundClasses.put(oneClass, files[0])
                } else {
                    problemClasses.put(oneClass, files)
                }
            }
        }
    }

}
