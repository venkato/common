package net.sf.jremoterun.utilities.nonjdk.classpath.classloader

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.AddFilesToClassLoaderCommon
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorAbstract
import net.sf.jremoterun.utilities.classpath.ClassPathCalculatorWithAdder
import net.sf.jremoterun.utilities.nonjdk.classpath.AddDirectoryWithFiles
import net.sf.jremoterun.utilities.nonjdk.classpath.AddDirectoryWithFiles2
import net.sf.jremoterun.utilities.nonjdk.classpath.CustomObjectHandlerImpl
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.types.ClassSlashNoSuffix2
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace
import org.apache.commons.collections4.MapUtils

import java.util.logging.Logger;

@CompileStatic
class AllClasspathAnalysis {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AddedLocationDetector adder = new AddedLocationDetector();
    public GetClassesFromLocation classesFromLocation;
    public DuplicateClassesDetector duplicateClassesDetector;
    public Map<File, Object> file2HumanMap = [:]
    public Map<File, List<JustStackTrace>> dupsFilesMap = [:]
    public Map<ClassSlashNoSuffix, List<JustStackTrace>> dupsClassMap = [:]
    public Map<File, JustStackTrace> locationMap12 = [:]
    public HashSet<String> classes = [];
    public HashSet<File> nonUsedLocations;
    public HashSet<File> usedLocations;
    /**
     * @see net.sf.jremoterun.utilities.nonjdk.classpath.classloader.exceptioncollector.ExceptionLocationCollector#stackTraceIgnoreClassName
     */
    public HashSet<String> stackTraceIgnoreClassName = new HashSet<>()
    public volatile UsedByAnalysis usedByAnalysis;
    public final Date startDate = new Date();
    public boolean humanReturnStackTraceElement = true


    public List<String> dupClassesIgnore = []

    public List<String> missedIgnore = []

    AllClasspathAnalysis() {
        addClassToStackTraceIgnoreClassName(AddedLocationDetector)
        addClassToStackTraceIgnoreClassName(AddFilesToClassLoaderCommon)
        addClassToStackTraceIgnoreClassName(CustomObjectHandlerImpl)
        addClassToStackTraceIgnoreClassName(AddDirectoryWithFiles2)
        stackTraceIgnoreClassName.add('org.codehaus.groovy.runtime.')
    }

    void classesAddFromFile(File f) {
        assert f.exists()
        List<String> linesFromFile = f.readLines()
        assert linesFromFile.size() > 0
        classes.addAll(linesFromFile)
    }

    List<String> easyCalcUsed() {
        addClassesFromCurrentClassLoader()
        adder.addClassPathFromURLClassLoader(JrrClassUtils.getCurrentClassLoaderUrl())
        analise();
        Set<Object> usedd = getUsedLocationsHuman().keySet()
        List<String> sorted = usedd.collect { it.toString() }.sort()
        return sorted

    }

    void addClassToStackTraceIgnoreClassName(Class clazz) {
        stackTraceIgnoreClassName.add(clazz.getName())
    }

    void addClassesFromCurrentClassLoader() {
        addClassesFromClassLoader(AllClasspathAnalysis.getClassLoader())
    }

    void addClassesFromClassLoader(ClassLoader classLoader) {
        classes.addAll(DumpLoadedClasses.dumpLoadedClassesNames(classLoader))
    }

    UsedByAnalysis createUsedByAnalysis() {
        if (usedByAnalysis == null) {
            usedByAnalysis = new UsedByAnalysis(duplicateClassesDetector)
        }
        return usedByAnalysis;
    }

    void analise() {
        if (classes.size() == 0) {
            throw new Exception("No classes")
        }
        if (adder.locationMap.size() == 0) {
            throw new Exception("No files")
        }
        dupsFilesMap = adder.findDuplicates()
        classesFromLocation = new GetClassesFromLocation()
        adder.locationMap.each { locationMap12.put(it.key, it.value[0]) }
        Map<File, List<ClassSlashNoSuffix2>> files = classesFromLocation.loadClassesOnLocation(adder.getAddedFiles4())
        duplicateClassesDetector = new DuplicateClassesDetector(new HashSet<>(classes.collect { new ClassSlashNoSuffix(UsedByAnalysis.convertClassNameToSlash(it)) }), files);
        duplicateClassesDetector.problemClasses.each { dupsClassMap.put(it.key, getLocations(it.value)) }
        resolveHumanLocation()
        nonUsedLocations = duplicateClassesDetector.getNonUsedLocations()
        usedLocations = duplicateClassesDetector.getUsedLocations()
    }


    StackTraceElement resolveLocationByStackTrace(JustStackTrace justStackTrace) {
        StackTraceElement[] trace = justStackTrace.getStackTrace()
        return trace.toList().find { isGoodStackElement(it) }
    }

    UsedByDepAnalysis getDepAnalisys() {
        return new UsedByDepAnalysis(duplicateClassesDetector)
    }

    boolean isGoodStackElement(StackTraceElement el) {
        String find1 = stackTraceIgnoreClassName.find {
            return el.getClassName().startsWith(it)
        }
        return find1 == null
    }

    HashSet<File> getFilesFromHuman(Collection locations) {
        Map<Object, File> reverted = MapUtils.invertMap(file2HumanMap)
        List<File> collect1 = locations.collect {
            File get1 = reverted.get(it)
            if (get1 == null) {
                throw new Exception("Not found ${it}")
            }
            return get1
        }
        return new HashSet<File>(collect1)
    }

    void resolveHumanLocation() {
        ClassPathCalculatorAbstract calculator = createCalculator()
        adder.locationMap.each {
            Object object = calculator.convertFileToObject(it.key)
            file2HumanMap.put(it.key, object)
        }
    }

    Map<Object, Object> getUsedLocationsHuman() {
        return convertListToHuman(usedLocations);
    }

    Map<Object, Object> getNonUsedLocationsHuman() {
        return convertListToHuman(nonUsedLocations);
    }

    Map<Object, Object> convertListToHuman(Collection<File> list) {
        Map<Object, Object> result = [:]
        list.each {
            Object key1 = file2HumanMap.get(it)
            if (key1 == null) {
                log.info "${it} not found in file2HumanMap"
                key1 = it;
            }
            JustStackTrace justStackTrace = locationMap12.get(it)
            Object value2
            if (humanReturnStackTraceElement) {
                value2 = resolveLocationByStackTrace(justStackTrace)
            } else {
                value2 = justStackTrace
            }
            result.put(key1, value2);
        }
        return result
    }

    ClassPathCalculatorAbstract createCalculator() {
        return new ClassPathCalculatorWithAdder()
    }

    List<JustStackTrace> getLocations(List<ClassLocaltionInfo> files) {
        return files.collect { locationMap12.get(it.f) }
    }


    Map getFilteredDupsClassMap() {
        Map<ClassSlashNoSuffix, List<JustStackTrace>> all1 = dupsClassMap
//        Map<String, List<JustStackTrace>> all1 = dupsClassMap.collectEntries {[(UsedByAnalysis.convertClassName(it.key)):it.value]}
        all1 = all1.findAll { return needShowDupClass(it.key) };
        if (humanReturnStackTraceElement) {
            Map<ClassSlashNoSuffix, List<StackTraceElement>> rr = [:]
            all1.each {
                List<StackTraceElement> collect1 = it.value.collect { ss -> resolveLocationByStackTrace(ss) }
                rr.put(it.key, collect1)
            }
            Map<String,List<StackTraceElement>> rr2 =[:]
            rr.each {
                rr2.put(it.key.clName,it.value)
            }
            return rr2
            //return rr
        }
        Map<String,List<JustStackTrace>> rr3 =[:]
        all1.each {
            rr3.put(it.key.clName,it.value)
        }
        return rr3
    }

    Collection<String> getFilteredMissesClasses() {
        return new TreeSet<String>(duplicateClassesDetector.missedClasses.findAll { return needShowMissedClass(it) }.collect { it.clName })
    }

    boolean needShowDupClass(ClassSlashNoSuffix className) {
        if (className.clName.contains('$')) {
            return false;
        }
        String startWithFound = dupClassesIgnore.find { it.length() > 0 && className.clName.startsWith(UsedByAnalysis.convertClassNameToSlash(it)) }
        if (startWithFound != null) {
            return false
        }
        return true
    }

    boolean needShowMissedClass(ClassSlashNoSuffix className) {
        if (className.clName.contains('$')) {
            return false;
        }
        String startWithFound = missedIgnore.find { it.length() > 0 && className.clName.startsWith(UsedByAnalysis.convertClassNameToSlash(it)) }
        if (startWithFound != null) {
            return false
        }
        return true
    }

}
