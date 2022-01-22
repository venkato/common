package net.sf.jremoterun.utilities.nonjdk.serviceloader.dumpservices

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr
import net.sf.jremoterun.utilities.nonjdk.FileUtilsJrr
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.UsedByAnalysis
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.MapStore2
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.StoreComplex
import net.sf.jremoterun.utilities.nonjdk.store.configloader.a2.BackupLocationI
import net.sf.jremoterun.utilities.nonjdk.store.configloader.a2.BackupLocationRotation
import net.sf.jremoterun.utilities.nonjdk.store.configloader.a2.GroovyStoreLoad;

import java.util.logging.Logger;

@CompileStatic
abstract class ServiceLoaderAnalizer {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AllClasspathAnalysis analysis;

    public net.sf.jremoterun.utilities.nonjdk.serviceloader.dumpservices.ServicesDDumper servicesDDumper = new ServicesDDumper();
    public Map<File, Map<ClRef, List<ClRef>>> servicesRaw
    public Map<Object, Map<ClRef, List<ClRef>>> servicesHuman = [:]
    // TODO 3 fields MapStore2 are not used : remove
    public MapStore2 servicesStoreRaw = new MapStore2()
    public MapStore2 mapStoreMutiRelease = new MapStore2()
    public MapStore2 servicesStoreSummary = new MapStore2()
    public File dir
    public BackupLocationI backupLocation

    HashSet<String> multiReleaseIgnoreStartWith = new HashSet<>()

    ServiceLoaderAnalizer(AllClasspathAnalysis analysis, File dir1, BackupLocationI backupLocation1) {
        this.analysis = analysis
        dir = dir1
        backupLocation = backupLocation1
        servicesStoreRaw.writeCreatedAt = false
        servicesStoreSummary.writeCreatedAt = false
    }

    ServiceLoaderAnalizer(AllClasspathAnalysis analysis, File dir1, int rotateCount) {
        this.analysis = analysis
        dir = dir1
        backupLocation = new BackupLocationRotation(new FileToFileRef(dir1), rotateCount)
    }


    void analyze() {
        servicesRaw = servicesDDumper.readServicesManyFiles(analysis.adder.locationMap.keySet())
        servicesRaw.each {
            servicesHuman.put(convertFileToMavenId(it.key), it.value);
        }

    }

    Map<ClRef, List<Object>> getServicesSummary() {
        Map<ClRef, List<Object>> a = [:]
        servicesHuman.each {
            a.get(it.value.keySet())
            handleService(it.key, it.value.keySet(), a)
        }
        return new TreeMap(a)
    }

    TreeMap<ClRef, List<Object>> getMutiRelease() {
        Map<ClRef, List<Object>> a = [:]
        analysis.duplicateClassesDetector.classesNamesMultiReleaseUsedInApp.each {
            if (!it.key.clName.contains('$')) {
                List<Object> unique1 = it.value.collect { convertFileToMavenId(it.f.getCanonicalFile().getAbsoluteFile()) }.unique().sort()
                a.put(new ClRef(UsedByAnalysis.convertClassName(it.key.clName)), unique1)
            }
        }
        return new TreeMap(a)
    }


    void saveServicesToFile() {
        analyze()
        saveMutiReleaseToFile()
        //getServicesSummary()
//        new File(dir,'rawLatest.groovy').text = newDataRaw
        Map<ClRef, List<Object>> summaryM = getServicesSummary()
        GroovyStoreLoad summarySaver = saveDataIfNeeded( DumpClassLoaderInfoFIleLayout.servicesSummaryLatest, servicesStoreSummary, summaryM)
        //Map dataBefore = [:]
        saveDataIfNeeded( DumpClassLoaderInfoFIleLayout.servicesRaw, servicesStoreRaw, servicesHuman)
//        if(!rawDataFIle.exists() || newDataRaw!=rawDataFIle.text){
//            FileRotate.rotateFile(rawDataFIle,rotateCount)
//            rawDataFIle.text = newDataRaw
//        }else{
//            log.info "raw file equals"
//        }
        File summaryGoldenDataFIle = getFileToSave(DumpClassLoaderInfoFIleLayout.servicesSummaryGolden)
        if (summaryGoldenDataFIle.exists()) {
            analizeServiceSummary(summaryM, summaryGoldenDataFIle)
        } else {
            FileUtilsJrr.copyFile(summarySaver.latestFile, summaryGoldenDataFIle)
            //summaryDataFIle.text = summarySaver.latestFile
        }
    }


    File getFileToSave(DumpClassLoaderInfoFIleLayout layout) {
        return new File(dir, layout.customName)
    }

    GroovyStoreLoad saveDataIfNeeded(DumpClassLoaderInfoFIleLayout layout, StoreComplex storeComplex, Object saveObject) {
        File f = getFileToSave(layout)
        GroovyStoreLoad groovyStoreLoad = new GroovyStoreLoad(saveObject, f)
        groovyStoreLoad.backupLocation = backupLocation
        groovyStoreLoad.storeComplex = storeComplex
        saveIfNeeded(groovyStoreLoad, layout, storeComplex, saveObject)
        return groovyStoreLoad
    }

    void saveIfNeeded(GroovyStoreLoad groovyStoreLoad, DumpClassLoaderInfoFIleLayout layout, StoreComplex storeComplex, Object saveObject) {
        groovyStoreLoad.storeComplex = new MapStore2()
        groovyStoreLoad.saveIfNeeded()
    }


    boolean isCompareClRefForMultiRelese(ClRef clRef) {
        String find1 = multiReleaseIgnoreStartWith.find { it.length() > 0 && clRef.className.startsWith(it) }
        return find1 == null
    }

    void saveMutiReleaseToFile() {
        TreeMap<ClRef, List<Object>> new1 = getMutiRelease()
        //String newData = mapStoreMutiRelease.saveComplexObject(new1)

        GroovyStoreLoad multiReleaseSaver = saveDataIfNeeded(DumpClassLoaderInfoFIleLayout.mutireleaseJarLatest, mapStoreMutiRelease, new1)
        File goldenDataDataFIle = getFileToSave(DumpClassLoaderInfoFIleLayout.mutireleaseJarGolden)
        if (goldenDataDataFIle.exists()) {
            analizeMultiRelease(new1, goldenDataDataFIle)
        } else {
            FileUtilsJrr.copyFile(multiReleaseSaver.latestFile, goldenDataDataFIle)
            //goldenDataDataFIle.text = newData
        }
    }

    Object convertFileToMavenId(File f) {
        Object obj = analysis.file2HumanMap.get(f)
        if (obj == null) {
            return f
        }
        return obj;
    }

    abstract void onDifferentMutiRelease(Map<ClRef, List<Object>> before3, Map<ClRef, List<Object>> newO)

    void parseConfig(File goldenDataDataFIle, Object store) {
        GroovyConfigLoaderJrr.configLoaderAdvance.parseConfig(goldenDataDataFIle).loadConfig(store)
    }

    void analizeMultiRelease(TreeMap<ClRef, List<Object>> new1, File goldenDataDataFIle) {
        Map<ClRef, List<Object>> dataBeforte = new TreeMap()
        parseConfig(goldenDataDataFIle, dataBeforte)
        TreeSet<ClRef> keysBefore = new TreeSet<ClRef>(((Set<ClRef>) dataBeforte.keySet()).findAll { isCompareClRefForMultiRelese(it) })
        TreeSet<ClRef> keysAfter = new TreeSet<ClRef>(((Set<ClRef>) new1.keySet()).findAll { isCompareClRefForMultiRelese(it) })
        if (keysAfter == keysBefore) {

        } else {
            onDifferentMutiRelease(dataBeforte, new1)
        }

    }


    void analizeServiceSummary(Map<ClRef, List<Object>> summaryNew, File fileFebore) {
        Map<ClRef, List<Object>> dataBeforte = [:]
        parseConfig(fileFebore, dataBeforte)
        Map<ClRef, List<Object>> before3 = removeMavenVersion(dataBeforte)
        Map<ClRef, List<Object>> after3 = removeMavenVersion(summaryNew)
        if (before3 == after3) {
            log.info "summary file equal"
        } else {
            onDifferent(before3, after3)
        }
    }

    /**
     * if new is good, replace summaryGolden.groovy from summaryLatest.groovy
     */
    abstract void onDifferent(Map<ClRef, List<Object>> before3, Map<ClRef, List<Object>> newO);

    List<Object> removeMavenVersion2(List<Object> aa) {
        return aa.collect {
            if (it instanceof MavenId) {
                return it.groupId + ':' + it.artifactId
            }
            return it
        }
    }

    Map<ClRef, List<Object>> removeMavenVersion(Map<ClRef, List<Object>> aa) {
        Map<ClRef, List<Object>> r = [:]
        aa.each { r.put(it.key, removeMavenVersion2(it.value)) }
        return r
    }

    void handleService(Object location1, Collection<ClRef> services, Map<ClRef, List<Object>> result) {
        services.each {
            List<Object> get1 = result.get(it)
            if (get1 == null) {
                get1 = []
                result.put(it, get1)
            }
            get1.add(location1)
        }
    }
}
