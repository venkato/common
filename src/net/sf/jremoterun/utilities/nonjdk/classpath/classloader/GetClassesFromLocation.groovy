package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.ClassNameSuffixes
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix2
import net.sf.jremoterun.utilities.nonjdk.io.JrrIoUtils
import net.sf.jremoterun.utilities.nonjdk.sftploader.settings.SftpConnectionSettings
import org.glavo.jimage.ImageReader
import org.zeroturnaround.zip.ZipEntryCallback
import org.zeroturnaround.zip.ZipUtil;

import java.util.logging.Logger
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream;

@CompileStatic
class GetClassesFromLocation {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    Map<File,List<ClassSlashNoSuffix2>> loadClassesOnLocation(Collection<File> files) {
        Map<File,List<ClassSlashNoSuffix2>> classesFromFile = [:]
        files.each {
            classesFromFile.put(it,handleOneFile(it));
        }
        return classesFromFile;
    }

    public static String modulesFileName ='modules'

    List<ClassSlashNoSuffix2> handleOneFile(File file){
        if (file.isDirectory()) {
            return handleDir(file).collect {new ClassSlashNoSuffix2(it)};
        }
        if(file.getName() ==modulesFileName){
            return handleModuleJava11(file)
        }
        return handleZip(file)
    }





    List<ClassSlashNoSuffix2> handleModuleJava11(File moduleFile) {
        ImageReader imageReader = ImageReader.open(moduleFile.toPath())
        try {
            List<ClassSlashNoSuffix2> entriess = imageReader.getEntryNames().toList().findAll { it.endsWith(ClassNameSuffixes.dotclass.customName) }.collect { UsedByAnalysis.removeDotClass(it) }.collect {new ClassSlashNoSuffix2(it)}
            return entriess
        }finally {
            JrrIoUtils.closeQuietly2(imageReader, log)
        }
    }

    List<ClassSlashNoSuffix2> handleZip(File zipFile) {
        return handleZipS(zipFile).collect {new ClassSlashNoSuffix2(UsedByAnalysis.removeDotClass(it) )}
    }

    static List<String> handleZipS(File zipFile) {
        assert zipFile.exists()
        List<String> result = []
        BufferedInputStream inputStream1 = zipFile.newInputStream()
        ZipInputStream zipInputStream = new ZipInputStream(inputStream1)
        try {
            while (true) {
                ZipEntry nextEntry = zipInputStream.getNextEntry()
                if (nextEntry == null) {
                    break
                }
                if (nextEntry.isDirectory()) {

                } else {
                    String entryName = nextEntry.getName();
                    if(entryName.endsWith(ClassNameSuffixes.dotclass.customName)) {
                        result.add(  (entryName))
                    }
                }
                zipInputStream.closeEntry()
            }
            return result
        } finally {
            JrrIoUtils.closeQuietly(zipInputStream)
            JrrIoUtils.closeQuietly(inputStream1)
        }
    }

//    static List<String> handleZipSOld(File zipFile) {
//        List<String> result = []
//        ZipEntryCallback zipEntryCallback = new ZipEntryCallback() {
//            @Override
//            void process(InputStream inputStream, ZipEntry zipEntry) throws IOException {
//                String entryName = zipEntry.getName();
//                if(entryName.endsWith('.class')) {
//                    String className1 = entryName.replace('/', '.');
//                    result.add(className1.substring(0,className1.length()-6))
//                }
//            }
//        }
//        ZipUtil.iterate(zipFile, zipEntryCallback)
//        return result
//    }



    List<String> handleDir(File dir) {
        List<String> result = []
        File[] files = dir.listFiles()
        files.toList().each {

            String fileName = it.getName()
            if (it.isFile()) {
                if (fileName.endsWith(ClassNameSuffixes.dotclass.customName)) {
                    String name1 = fileName.substring(0,fileName.length()-6)
                    result.add(name1)
                }
            }
            if (it.isDirectory()) {
                List<String> resTmp = handleDir(it);
                result.addAll(resTmp.collect { fileName + '/' + it })
//                result.addAll(resTmp.collect { fileName + '.' + it })
            }
        }
        return result
    }


}
