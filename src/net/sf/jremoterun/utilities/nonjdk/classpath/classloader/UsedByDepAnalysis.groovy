package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.usedclasses.UsedClasses
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import org.glavo.jimage.ImageReader
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
    public HashSet<ClassSlashNoSuffix> inClassesToAnailze = new HashSet<>()
    public HashSet<ClassSlashNoSuffix> needed = new HashSet<>()
    public HashSet<ClassSlashNoSuffix> skipped = new HashSet<>()
    public boolean throwErrorOnMaxDepthReached = true

    UsedByDepAnalysis(DuplicateClassesDetector det) {
        this.det = det
        clearResultVars()
    }

    void clearResultVars() {
        needed.clear()
        skipped.clear()
    }

    void addAnalisysClassesFromFile(FileToFileRef f) {
        addAnalisysClassesFromFile(f.resolveToFile())
    }

    void addAnalisysClassesFromFile(File f) {
        inClassesToAnailze.addAll(  det.classesOnLocationUsed.get(f).collect {det.getClassNameSlashFromJar(f,it)})
    }

    HashSet<ClassLocaltionInfo> analize(int maxDepth) {
        inClassesToAnailze.each {
            ClassSlashNoSuffix clName = it;
            if (needed.contains(it) || skipped.contains(clName)) {

            } else {
                getDependentClasses(clName, maxDepth);
            }
        }
        return det.convertClassesToLocation(needed);
    }

    public boolean handleMultiReleaseJar = false

    protected void getDependentClasses(ClassSlashNoSuffix toAnalize, int maxDepth) {
        if (!det.classesNamesUsedInApp.contains(toAnalize)) {
            throw new Exception("Not found : ${toAnalize}")
        }
        needed.add(toAnalize);
        List<ClassLocaltionInfo> files = det.classesOnLocationReverse.get(toAnalize);
        if (files == null || files.size() == 0) {
            throw new Exception("not class on location : ${toAnalize}")
        }
        ClassLocaltionInfo location = files.find { !it.isMultiReleaseFile }
        if (location == null) {
            location = files[0]
        }
        boolean handle1 = true
        if (location.isMultiReleaseFile) {
            if (handleMultiReleaseJar) {
                //classNameToPath=location.fullPathInJarSlash
            } else {
                handle1 = false
            }
        }
        if (handle1) {
            HashSet<ClassSlashNoSuffix> result = new HashSet<>()
            if (location.f.isDirectory()) {
                File classFile = location.f.child(location.fullPathInJarSlash.convertClassNameToPath2())
                if (!classFile.exists()) {
                    throw new FileNotFoundException(classFile.getAbsolutePath())
                }
                HashSet<ClassSlashNoSuffix> usedTypes = new HashSet<>( UsedClasses.remapClassNoRedefine(classFile.bytes).usedTypes.collect { new ClassSlashNoSuffix(it) })
                result.addAll(usedTypes)
            } else {
                assert location.f.isFile()

                if (location.f.getName() == GetClassesFromLocation.modulesFileName) {
                    ImageReader imageReader = ImageReader.open(location.f.toPath())
                    try {
                        //classNamesOnLocationUsed22.each {
                            // works ? need add .class suffix ?
                            byte[] bytes = imageReader.getResource(location.fullPathInJarSlash.convertClassNameToPath2())
                            if (bytes != null) {
                                HashSet<ClassSlashNoSuffix> usedTypes = new HashSet<>( UsedClasses.remapClassNoRedefine(bytes).usedTypes.collect { new ClassSlashNoSuffix(it) })
                                //usesClassesFromClass.put(it, usedTypes)
                                result.addAll(usedTypes)
                            }
                        //}
                    } finally {
                        JrrIoUtils.closeQuietly2(imageReader, log)
                    }
                } else {
                    ZipEntryCallback zipEntryCallback = new ZipEntryCallback() {
                        @Override
                        void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
                            if(zipEntry.getName().endsWith(ClassNameSuffixes.dotclass.customName)) {
                                if (zipEntry.getName() == location.fullPathInJarSlash.convertClassNameToPath2()) {
                                    byte[] bytes = inputStream.bytes
                                    HashSet<ClassSlashNoSuffix> usedTypes = new HashSet<>( UsedClasses.remapClassNoRedefine(bytes).usedTypes.collect { new ClassSlashNoSuffix(it) })
                                    result.addAll(usedTypes)
                                }
                            }
                        }
                    }
                    ZipUtil.iterate(location.f, zipEntryCallback)
                }
            }
            HashSet<ClassSlashNoSuffix> result2 = new HashSet<>(result)
//            HashSet<String> result2 = new HashSet<>(result.collect { it.replace('/', '.') })
            result2.each {
                if (needed.contains(it)) {

                } else {
                    if (det.classesNamesUsedInApp.contains(it)) {
                        needed.add(it);
                        if (maxDepth == 0) {
                            if (throwErrorOnMaxDepthReached) {
                                throw new Exception("max depth reached on class : ${toAnalize}")
                            }
                        } else {
                            getDependentClasses(it, maxDepth - 1);
                        }
                    } else {
                        skipped.add(it);
                    }
                }
            }
        }
    }


//    static String convertClassNameToPath(String className) {
//        return className.replace('.', '/') + ClassNameSuffixes.dotclass.customName;
//    }

}
