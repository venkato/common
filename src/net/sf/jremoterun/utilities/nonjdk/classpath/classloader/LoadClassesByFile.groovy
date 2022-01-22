package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.starter.JrrStarterJarRefs2;

import java.util.logging.Logger;

@CompileStatic
class LoadClassesByFile {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String, List<String>> map1;
    public Map<File, LoadClassesByClassloader> resultLoaded = [:];
    public ClassLoader classLoader1 = JrrClassUtils.getCurrentClassLoader()

    LoadClassesByFile(File f) {
        map1 = (Map)new JsonSlurper().parseText(f.text)
    }

    LoadClassesByFile(Map<String, List<String>> map1) {
        this.map1 = map1
    }

    static LoadClassesByFile loadJrrClassesPrevious(File file1, int count1){
        LoadClassesByFile loadClassesByFile =new LoadClassesByFile(file1)
        loadClassesByFile.loadJrrClasses()
        log.info "loading classes before ${file1}"
        while (count1>0){
            File fileBefore2 = FileRotate.buildRotateFile(file1, file1.getParentFile(), count1,false)
            if(fileBefore2.exists()) {
                log.info "loading ${fileBefore2} .."
                LoadClassesByFile loadClassesByFileBefore = new LoadClassesByFile(fileBefore2)
                loadClassesByFileBefore.loadJrrClasses()
            }else {
                log.info "skip file as absent : ${fileBefore2}"
            }
            count1--
        }
        return loadClassesByFile;
    }

    void loadJrrClasses(){
        JrrStarterJarRefs2.values().toList().each {
            loadClassesByLocation(it)
        }
        loadClassesByLocation(JrrStarterJarRefs.jrrutilitiesOneJar)
    }

    void loadClassesByLocation(ToFileRef2 file){
        File file1 = file.resolveToFile()
        List<String> classes = getClassesByLocation(file1)
        LoadClassesByClassloader classesByClassloader = new LoadClassesByClassloader(classLoader1)
        resultLoaded.put(file1,classesByClassloader)
        classesByClassloader.loadClassesByLocation(classes)
        log.info "${file1} ${classesByClassloader.buildStat()}"
    }

    List<String> getClassesByLocation(ToFileRef2 file){
        getClassesByLocation(file.resolveToFile())
    }

    List<String> getClassesByLocation(File file){
        String path1 = DuplicateClassesDetector.normalizeFile(file)
        List<String> result = []
        map1.each {
            if(it.value.contains(path1)){
                result.add(it.key)
            }
        }
        return  result
    }

}
