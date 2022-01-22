package net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.AddFileToClassloaderDummy

import java.util.logging.Logger

@CompileStatic
class ClasspathAdderUtils {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean checkZipArchiveIsCorrect = false

    static Collection<File> copyFilesUnderDir(File groovyFile, File toDir){
        return copyFilesUnderDir2(groovyFile,MavenDefaultSettings.mavenDefaultSettings.grapeFileFinder.getMavenLocalDir2(),toDir)
    }

    static Collection<File> copyFilesUnderDir2(File groovyFile, File srcMavenDir,File toDir){
        AddFileToClassloaderDummy dummy = new AddFileToClassloaderDummy();
        dummy.checkJarFileIsZipArchive = checkZipArchiveIsCorrect
        dummy.addFromGroovyFile(groovyFile)
        return copyFilesUnderDir(dummy.addedFiles2, srcMavenDir,toDir)
    }

    static Collection<File> copyFilesUnderDir(Collection<File> files, File baseDir, File toDir){
        assert baseDir.exists()
        toDir.mkdir()
        assert toDir.exists()
        if(files.size()==0){
            throw new Exception("No added files")
        }
        Collection<File> childs = files.findAll { baseDir.isChildFile(it) };
        if(childs.size()==0){
            log.info("No children found for ${baseDir}")
            return []
        }
        List<File> copiedFiles = []
        childs.each {
            try {
                String path1 = baseDir.getPathToParent(it);
                File dest = toDir.child(path1)
                if(!dest.exists()) {
                    File parentFile = dest.getParentFile()
                    parentFile.mkdirs()
                    assert parentFile.exists()
                    FileUtilsJrr.copyFile(it, dest)
                    copiedFiles.add(dest)
                }
            }catch(Exception e){
                log.info("failed copy ${it}")
                throw e;
            }
        }
        return copiedFiles;
    }


}
