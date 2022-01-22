package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.usedclasses.UsedClasses
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil

import java.util.logging.Logger
import java.util.zip.ZipEntry


/**
 * This class give you jars, which your jar uses
 */
@CompileStatic
class UsedByDepAnalysis {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public DuplicateClassesDetector det;
    public HashSet<String> inClassesToAnailze = new HashSet<>()
    public HashSet<String> needed = new HashSet<>()
    public HashSet<String> skipped = new HashSet<>()
    public boolean throwErrorOnMaxDepthReached = true

    UsedByDepAnalysis(DuplicateClassesDetector det) {
        this.det = det
        clearResultVars()
    }

    void clearResultVars(){
        needed.clear()
        skipped.clear()
    }

    void addAnalisysClassesFromFile(FileToFileRef f ){
        addAnalisysClassesFromFile(f.resolveToFile())
    }

    void addAnalisysClassesFromFile(File f ){
        inClassesToAnailze.addAll( det.classesOnLocationUsed.get(f))
    }

    HashSet<File> analize(int maxDepth){
        inClassesToAnailze.each {
            String clName = it;
            if(needed.contains(it)||skipped.contains(clName)){

            }else{
                getDependentClasses(clName, maxDepth);
            }
        }
        return det.convertClassesToLocation(needed);
    }

    protected void getDependentClasses(String toAnalize, int maxDepth) {
        if (!det.classesNames.contains(toAnalize)) {
            throw new Exception("Not found : ${toAnalize}")
        }
        needed.add(toAnalize);
        List<File> files = det.classesOnLocationReverse.get(toAnalize);
        if (files == null || files.size() == 0) {
            throw new Exception("not class on location : ${toAnalize}")
        }
        String classNameToPath = convertClassNameToPath(toAnalize)
        File location = files[0]
        HashSet<String> result = new HashSet<>()
        if (location.isDirectory()) {
            File classFile = location.child(classNameToPath)
            if (!classFile.exists()) {
                throw new FileNotFoundException(classFile.getAbsolutePath())
            }
            HashSet<String> usedTypes = UsedClasses.remapClassNoRedefine(classFile.bytes).usedTypes
            result.addAll(usedTypes)
        } else {
            assert location.isFile()
            ZipEntryCallback zipEntryCallback = new ZipEntryCallback() {
                @Override
                void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
                    if (zipEntry.getName() == classNameToPath) {
                        byte[] bytes = inputStream.bytes
                        HashSet usedTypes = UsedClasses.remapClassNoRedefine(bytes).usedTypes
                        result.addAll(usedTypes)
                    }
                }
            }
            ZipUtil.iterate(location, zipEntryCallback)
        }
        HashSet<String> result2 = new HashSet<>(result.collect { it.replace('/', '.') })
        result2.each {
            if (needed.contains(it)) {

            } else {
                if (det.classesNames.contains(it)) {
                    needed.add(it);
                    if (maxDepth == 0) {
                        if(throwErrorOnMaxDepthReached){
                            throw new Exception("max depth reached on class : ${toAnalize}")
                        }
                    }else {
                        getDependentClasses(it, maxDepth - 1);
                    }
                } else {
                    skipped.add(it);
                }
            }
        }
    }
    

    static String convertClassNameToPath(String className) {
        return className.replace('.', '/') + '.class';
    }

}
