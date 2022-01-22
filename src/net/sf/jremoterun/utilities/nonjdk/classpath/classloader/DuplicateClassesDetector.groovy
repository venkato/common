package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class DuplicateClassesDetector {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public HashSet<String> classesNamesUsedInApp;
    public Map<File, List<String>> classesOnLocation = [:];
    public Map<File, Set<String>> classesOnLocationUsed = [:];
    public Map<String, List<ClassLocaltionInfo>> classesOnLocationReverse = [:];
    public Map<String, List<ClassLocaltionInfo>> problemClasses = [:];
    public List<String> missedClasses = [];
    public Map<String, ClassLocaltionInfo> foundClasses = [:];
    public boolean treatMutiAsDup = false;
    public HashMap<String,List<ClassLocaltionInfo>> classesNamesMultiReleaseUsedInApp =new HashMap<>()

    DuplicateClassesDetector(HashSet<String> classesNamesUsedInApp, Map<File, List<String>> classesOnLocation) {
        this.classesNamesUsedInApp = classesNamesUsedInApp
        this.classesOnLocation = classesOnLocation
        this.classesOnLocation.each {
            reverseOneEntry(it.getKey(), it.getValue());
        }
        check()
    }


    HashSet<ClassLocaltionInfo> convertClassesToLocation(Collection<String> classNames1) {
        HashSet<ClassLocaltionInfo> result = new HashSet<>()
        classNames1.each {
            List<ClassLocaltionInfo> files = classesOnLocationReverse.get(it)
            result.addAll(files)
        }
        return result;
    }


    HashSet<File> getUsedLocations() {
        Map<File, Set<String>> used = classesOnLocationUsed.findAll { it.value.size() > 0 }
        HashSet<File> result = new HashSet<>()
        result.addAll(used.keySet());
        return result;
    }

    HashSet<File> getNonUsedLocations() {
        HashSet<File> result = new HashSet<>()
        Map<File, Set<String>> unused = classesOnLocationUsed.findAll { it.value.size() == 0 }
        result.addAll(unused.keySet())
        HashSet<File> files2 = new HashSet<File>(classesOnLocation.keySet())
        files2.removeAll(classesOnLocationUsed.keySet())
        result.addAll(files2)
        return result
    }


    protected void reverseOneEntry(File file, List<String> classes) {
        classes.each {
            forEachClass(file, it)
        }
    }

    String metaPrefeix = 'META-INF/versions/'

    void forEachClass(File file, String className) {
        String classname2= className.replace('.','/')
        if(classname2.startsWith(metaPrefeix)){
            String aaa= classname2.substring(metaPrefeix.length())
            int of2 = aaa.indexOf('/')
            if(of2==-1){
                throw new IllegalStateException(className)
            }
            String className1 = aaa.substring(of2 + 1).replace('/','.')
            ClassLocaltionInfo  classLocaltionInfo=new ClassLocaltionInfo(file,classname2)
            List<ClassLocaltionInfo> files = classesOnLocationReverse.get(className1)
            if (files == null) {
                files = []
                classesOnLocationReverse.put(className1, files)
            }
            files.add(classLocaltionInfo)
        }else{
            ClassLocaltionInfo classLocaltionInfo=new ClassLocaltionInfo(file)
            List<ClassLocaltionInfo> files = classesOnLocationReverse.get(className)
            if (files == null) {
                files = []
                classesOnLocationReverse.put(className, files)
            }
            files.add(classLocaltionInfo)
        }

    }


    protected void check() {
        classesNamesUsedInApp.each { handleOneClass(it) }
        classesOnLocation.each {
            HashSet<String> classes = new HashSet<>(it.value)
            classes.retainAll(classesNamesUsedInApp)
            classesOnLocationUsed.put(it.key, classes)
        }
    }

    Map<String, List<String>> getClass2FilesMap() {
        Map<String, List<String>> json1 = new TreeMap<>();
        problemClasses.each {
            json1.put(it.key, convertFiles(it.value.f))
        }
        foundClasses.each {
            json1.put(it.key, [normalizeFile(it.value.f)])
        }
        missedClasses.each {
            json1.put(it, [])
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




    protected void handleOneClass(String oneClass) {
        List<ClassLocaltionInfo> files = classesOnLocationReverse.get(oneClass)
        if (files == null) {
            missedClasses.add(oneClass)
//            classesOnLocationReverseUsed.put(oneClass,[])
        } else {
//            classesOnLocationReverseUsed.put(oneClass,files)
            boolean  handle1= true
            if(files.size()!=0) {
                if (!treatMutiAsDup) {
                    List<ClassLocaltionInfo> muti1 = files.findAll { it.isMultiReleaseFile }
                    if(muti1.size()>0){
                        List<ClassLocaltionInfo> get1 = classesNamesMultiReleaseUsedInApp.get(oneClass)
                        if(get1==null){
                            get1=[]
                            classesNamesMultiReleaseUsedInApp.put(oneClass,get1)
                        }
                        get1.addAll(muti1)
                    }
                    files = files.findAll { !it.isMultiReleaseFile }
                    if(files.size()==0){
                        handle1=false

                    }
                }
            }
            if(handle1) {
                if (files.size() == 1) {
                    foundClasses.put(oneClass, files[0])
                } else {
                    problemClasses.put(oneClass, files)
                }
            }
        }
    }

}
