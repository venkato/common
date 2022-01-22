package net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.nonjdk.langutils.ObjectStringComparator
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.ClassPathJrrStore
import org.codehaus.groovy.runtime.MethodClosure

import java.util.logging.Logger

@CompileStatic
class ClassPathCalculatorSup2Groovy extends ClassPathCalculatorWithAdder {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ClassPathJrrStore writer7Sub = new ClassPathJrrStore()

    public Map<MavenId, Object> mavenId2ObjectMap = [:]


    void addEnumMavenIdsMap(Class enumMavenIds) {
        Object[] values = JrrClassUtils.getEnumValues(enumMavenIds)
        List<MavenIdContains> list = values.toList() as List<MavenIdContains>
        list.collect {
            MavenId m = it.getM()
            mavenId2ObjectMap.put(m, it)
        }
    }

    Object convertMavenIdToObject(MavenId mavenId) {
        Object mapTo = mavenId2ObjectMap.get(mavenId)
        if (mapTo == null) {
            return mavenId
        }
        return mapTo;
    }

    String saveClassPath9() {
        assert filesAndMavenIds.size() > 0
        String classpath2 = saveClassPath7(filesAndMavenIds)
        return classpath2
    }


    void sortElements() {
        Collections.sort(filesAndMavenIds, new ObjectStringComparator());
    }


    String saveClassPath7(List files) throws Exception {
        assert files != null
        return writer7Sub.saveComplexObject(files)
    }

    String saveClassPathFromURLClassLoader(URLClassLoader urlClassLoader) throws Exception {
        addClassPathFromURLClassLoader(urlClassLoader);
        calcClassPathFromFiles12()
        return saveClassPath9();
    }

    String saveClassPathFromJmx() throws Exception {
        addClassPathFromJmx()
        calcClassPathFromFiles12()
        return saveClassPath9()
    }


    String calcAndSave() throws Exception {
        calcClassPathFromFiles12()
        return saveClassPath9()
    }

    void saveClassPathFromUrlClassloaderToFile(File file) throws Exception {
        net.sf.jremoterun.utilities.JrrUtilitiesFile.checkFileExist(file.parentFile)
        file.text = saveClassPathFromURLClassLoader(JrrClassUtils.currentClassLoaderUrl)
        assert file.length() > 2
    }

    void saveClassPathFromJmx(File file) throws Exception {
        file = file.absoluteFile.canonicalFile
        net.sf.jremoterun.utilities.JrrUtilitiesFile.checkFileExist(file.parentFile)
        file.text = saveClassPathFromJmx()
        assert file.length() > 2
    }

    void excludeFilesWithNames(List<String> fileNamesToExclude) {
        List filesAndMavenIds2 = new ArrayList()
        filesAndMavenIds.each {
            boolean needPass = false
            if (it instanceof File) {
                File file1 = (File) it;
                if (fileNamesToExclude.contains(file1.getName())) {

                } else {
                    needPass = true
                }
            } else {
                needPass = true
            }
            if (needPass) {
                filesAndMavenIds2.add(it)
            }
        }
        filesAndMavenIds = filesAndMavenIds2
    }


}
