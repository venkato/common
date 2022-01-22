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

//	static MethodClosure addFileMethod = (MethodClosure)ClassPathCalculatorSup2Groovy.&addF
//    @Deprecated
//    static MethodClosure addMavenMethod = (MethodClosure) (Closure)AddFilesToClassLoaderCommon.&addM

//    @Deprecated
//    static MethodClosure addGenerecMethod = (MethodClosure) (Closure)AddFilesToClassLoaderCommon.&add

    //public String varName = 'b'

    public ClassPathJrrStore writer7Sub = new ClassPathJrrStore()

//    @Deprecated
//   public ObjectWriter objectWriter = writer7Sub.objectWriter

    public Map<MavenId, Object> mavenId2ObjectMap = [:]

//    @Deprecated
//    void onBodyCreated2(){
//        onBodyCreated()
//    }

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
//		assert res.size()>3
//		String classpath2 = res.join('\r\n')
        return classpath2
    }

//    @Deprecated
//    void buildHeader(Writer3 writer3) {
//        writer3.addCreatedAtHeader()
//    }

    void sortElements() {
        Collections.sort(filesAndMavenIds, new ObjectStringComparator());
    }


//    void buildImport(Writer3 writer3) {
//        //List<String> res = []
//        writer3.addImport(MavenId)
//        writer3.addImport(AddFilesToClassLoaderGroovy)
//        //writer3.addImport(ClasspathConfigurator2)
//        writer3.addImport(GroovyMethodRunnerParams)
//        writer3.addImport(BinaryWithSource)
//        writer3.addImport(MavenPath)
//    }

//	List<String> buildVar(Writer3 writer3) {
//		List<String> res = []
//		res.add "${AddFilesToClassLoaderGroovy.simpleName} b = ${ writer3.generateGetProperty('a')} as ${AddFilesToClassLoaderGroovy.simpleName};"  as String
//		return res
//	}

//    Writer3 createWriter() {
//        return writer7Sub;
//    }


    String saveClassPath7(List files) throws Exception {
        assert files != null
        return writer7Sub.saveComplexObject(files)
//        buildHeader(writer7Sub)
//        buildImport(writer7Sub)
//        int counttt = -1;
//        files.collect {
//            try {
//                counttt++
//                addElToBody(it)
//            } catch (Exception e) {
//                log.info("Failed write ${counttt} el from list : ${e}")
//                throw e
//            }
//        }
//        onBodyCreated()
//        return writer7Sub.buildResult()
    }

//    @Deprecated
//    void onBodyCreated(){
//
//    }

//    @Deprecated
//    void addElStrToBody(String s){
//        writer7Sub.body.add "${buildAddPrefix()} ${s};".toString()
//    }
//
//    @Deprecated
//    void addElToBody(Object obj){
//        writer7Sub.body.add(convertEl(obj,writer7Sub))
//    }
//
//    @Deprecated
//    String convertEl(Object el, Writer3 writer3) {
//        String s = writer7Sub.objectWriter.writeObject(writer3, el)
//        return "${buildAddPrefix()} ${s};"
//    }
//
//    @Deprecated
//    String buildAddPrefix(){
//        return "${writer7Sub.varNameThis}.${addGenerecMethod.method}"
//    }



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
