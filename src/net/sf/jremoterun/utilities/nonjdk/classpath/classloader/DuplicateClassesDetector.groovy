package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class DuplicateClassesDetector {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public HashSet<String> classesNames;
    public Map<File, List<String>> classesOnLocation = [:];
    public Map<File, Set<String>> classesOnLocationUsed = [:];
    public Map<String, List<File>> classesOnLocationReverse = [:];
    public Map<String, List<File>> problemClasses = [:];
    public List<String> missedClasses = [];
    public Map<String, File> foundClasses = [:];

    DuplicateClassesDetector(HashSet<String> classesNames, Map<File, List<String>> classesOnLocation) {
        this.classesNames = classesNames
        this.classesOnLocation = classesOnLocation
        this.classesOnLocation .each {
            reverseOneEntry(it.getKey(), it.getValue());
        }
        check()
    }


    HashSet<File> convertClassesToLocation(Collection<String> classNames1) {
        HashSet<File> result = new HashSet<>()
        classNames1.each {
            List<File> files = classesOnLocationReverse.get(it)
            result.addAll(files)
        }
        return result;
    }


    HashSet<File> getUsedLocations() {
        Map<File, Set<String>> used = classesOnLocationUsed.findAll { it.value.size() > 0 }
        HashSet<File> result = new HashSet<>()
        result.addAll (used.keySet());
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
            List<File> files = classesOnLocationReverse.get(it)
            if (files == null) {
                files = []
                classesOnLocationReverse.put(it, files)
            }
            files.add(file)
        }
    }


    protected void check() {
        classesNames.each { handleOneClass(it) }
        classesOnLocation.each {
            HashSet<String> classes = new HashSet<>(it.value)
            classes.retainAll(classesNames)
            classesOnLocationUsed.put(it.key, classes)
        }
    }

    Map<String, List<String>> getClass2FilesMap() {
        Map<String, List<String>> json1 = new TreeMap<>();
        problemClasses.each {
            json1.put(it.key, convertFiles(it.value))
        }
        foundClasses.each {
            json1.put(it.key, [normalizeFile(it.value)])
        }
        missedClasses.each {
            json1.put(it, [])
        }
        return json1;
    }

    String getClass2FilesJson(){
        String json = JsonOutput.toJson(getClass2FilesMap());
        json =  JsonOutput.prettyPrint(json);
        return json;
    }

    @Deprecated
    static Map<String, List<String>> readJson   (String text){
        (Map)new JsonSlurper().parseText(text)
    }

    @Deprecated
    static List<String> getClassesByLocation(File file,Map<String, List<String>> map1){
        String path1 = normalizeFile(file)
        List<String> result = []
        map1.each {
            if(it.value.contains(path1)){
                result.add(it.key)
            }
        }
        return  result
    }

    protected List<String> convertFiles(List<File> files) {
        return files.collect {normalizeFile(it) }
    }

    static  String normalizeFile(File f){
        return f.getAbsoluteFile().getCanonicalFile().getAbsolutePath().replace('\\', '/')
    }

    protected void handleOneClass(String oneClass) {
        List<File> files = classesOnLocationReverse.get(oneClass)
        if (files == null) {
            missedClasses.add(oneClass)
//            classesOnLocationReverseUsed.put(oneClass,[])
        } else {
//            classesOnLocationReverseUsed.put(oneClass,files)
            if (files.size() == 1) {
                foundClasses.put(oneClass, files[0])
            } else {
                problemClasses.put(oneClass, files)
            }
        }
    }

}
