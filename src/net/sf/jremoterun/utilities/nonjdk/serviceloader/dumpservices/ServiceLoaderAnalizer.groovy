package net.sf.jremoterun.utilities.nonjdk.serviceloader.dumpservices

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.UsedByAnalysis
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.MapStore2;

import java.util.logging.Logger;

@CompileStatic
abstract class ServiceLoaderAnalizer {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AllClasspathAnalysis analysis;

    public net.sf.jremoterun.utilities.nonjdk.serviceloader.dumpservices.ServicesDDumper servicesDDumper = new ServicesDDumper();
    public Map<File, Map<ClRef, List<ClRef>>> servicesRaw
    public Map<Object, Map<ClRef, List<ClRef>>> servicesHuman = [:]
    public MapStore2 mapStore2 = new MapStore2()
    public MapStore2 mapStoreMutiRelease = new MapStore2()
    public MapStore2 mapStore3 = new MapStore2()

//    public ExactChildPattern rawChild = new ExactChildPattern('servicesRaw.groovy')
//    public ExactChildPattern summaryGoldenChild = new ExactChildPattern('servicesSummaryGolden.groovy')
//    public ExactChildPattern summaryLatestChild = new ExactChildPattern('servicesSummaryLatest.groovy')
//    public ExactChildPattern mutireleaseJarLatestChild = new ExactChildPattern('mutireleaseJarLatest.groovy')
//    public ExactChildPattern mutireleaseJarGoldenChild = new ExactChildPattern('mutireleaseJarGolden.groovy')

    ServiceLoaderAnalizer(AllClasspathAnalysis analysis) {
        this.analysis = analysis
        mapStore2.writeCreatedAt= false
        mapStore3.writeCreatedAt= false
    }


    void analyze(){
        servicesRaw = servicesDDumper.readServicesManyFiles(analysis.adder.locationMap.keySet())
        servicesRaw.each {
            servicesHuman.put( convertFileToMavenId(it.key),it.value);
        }

    }

    Map<ClRef, List<Object>> getServicesSummary(){
        Map<ClRef, List<Object>> a = [:]
        servicesHuman.each {
            a.get(it.value.keySet())
            handleService(it.key,it.value.keySet(),a)
        }
        return new TreeMap(a)
    }

    TreeMap<ClRef, List<Object>> getMutiRelease(){
        Map<ClRef, List<Object>> a = [:]
        analysis.duplicateClassesDetector.classesNamesMultiReleaseUsedInApp.each {
            if(!it.key.clName.contains('$')) {
                List<Object> unique1 = it.value.collect { convertFileToMavenId(it.f.getCanonicalFile().getAbsoluteFile()) }.unique().sort()
                a.put(new ClRef(UsedByAnalysis.convertClassName(it.key.clName)), unique1)
            }
        }
        return new TreeMap(a)
    }


    void saveServicesToFile(File dir, int rotateCount){
        analyze()
        saveMutiReleaseToFile(dir,rotateCount)
        String newDataRaw = mapStore2.saveComplexObject(servicesHuman)
        //getServicesSummary()
//        new File(dir,'rawLatest.groovy').text = newDataRaw
        Map<ClRef, List<Object>> summaryM = getServicesSummary()

        String newDataSummary = mapStore3.saveComplexObject(summaryM)
        getFileToSave(dir,DumpClassLoaderInfoFIleLayout.servicesSummaryLatest).text = newDataSummary
        //Map dataBefore = [:]
        File rawDataFIle = getFileToSave(dir,DumpClassLoaderInfoFIleLayout.servicesRaw)
        File summaryDataFIle = getFileToSave(dir,DumpClassLoaderInfoFIleLayout.servicesSummaryGolden)
        if(!rawDataFIle.exists() || newDataRaw!=rawDataFIle.text){
            FileRotate.rotateFile(rawDataFIle,rotateCount)
            rawDataFIle.text = newDataRaw
        }else{
            log.info "raw file equals"
        }
        if(summaryDataFIle.exists()) {
            analizeSummary(summaryM, summaryDataFIle)
        }else{
            summaryDataFIle.text = newDataSummary
        }
    }

    File getFileToSave(File dir,DumpClassLoaderInfoFIleLayout layout){
        return new File(dir,layout.customName)
    }

    HashSet<String> multiReleaseIgnoreStartWith = new HashSet<>()

    boolean isCompareClRefForMultiRelese(ClRef clRef){
        String find1 = multiReleaseIgnoreStartWith.find { it.length() > 0 && clRef.className.startsWith(it) }
        return find1==null
    }

    void saveMutiReleaseToFile(File dir, int rotateCount){
        TreeMap<ClRef, List<Object>> new1 = getMutiRelease()
        String newData = mapStoreMutiRelease.saveComplexObject(new1)

        File rawDataFIle = getFileToSave(dir,DumpClassLoaderInfoFIleLayout.mutireleaseJarLatest)
        if(!rawDataFIle.exists() || newData!=rawDataFIle.text){
            FileRotate.rotateFile(rawDataFIle,rotateCount)
            rawDataFIle.text = newData
        }else{
            log.info "raw file equals"
        }
        File goldenDataDataFIle = getFileToSave(dir,DumpClassLoaderInfoFIleLayout.mutireleaseJarGolden)
        if(goldenDataDataFIle.exists()) {
            //GroovyConfigLoader2I configLoader2 = GroovyConfigLoaderJrr.configLoader.parseConfig(goldenDataDataFIle)
//            GroovyConfigLoader2I configLoader2 = RunnableFactory.groovyClassLoader.parseClass(goldenDataDataFIle).newInstance()
            Map<ClRef, List<Object>>  dataBeforte = new TreeMap()
            GroovyConfigLoaderJrr.configLoaderAdvance.parseConfig(goldenDataDataFIle).loadConfig(dataBeforte)
            TreeSet<ClRef> keysBefore = new TreeSet<ClRef>(((Set<ClRef>)dataBeforte.keySet()).findAll {isCompareClRefForMultiRelese(it)})
            TreeSet<ClRef> keysAfter = new TreeSet<ClRef>(((Set<ClRef>)new1.keySet()).findAll {isCompareClRefForMultiRelese(it)})
            if(keysAfter==keysBefore){

            }else{
//                TreeSet<ClRef>  diffFiles = new TreeSet<>()
//                TreeMap<ClRef, List<File>>  newM = new TreeMap<>(new1)
//                TreeMap<ClRef, List<File>>  oldM = new TreeMap<>(dataBeforte)
//                new1.keySet().each {dataBeforte.remove(it)}
//                oldM.keySet().each {newM.remove(it)}
                onDifferentMutiRelease(dataBeforte,new1)
            }

        }else{
            goldenDataDataFIle.text = newData
        }
    }

    Object convertFileToMavenId(File f){
        Object obj=analysis.file2HumanMap.get(f)
        if(obj==null){
            return f
        }
        return obj;
    }

    abstract void onDifferentMutiRelease(Map<ClRef, List<Object>> before3,Map<ClRef, List<Object>> newO)


    void analizeSummary(Map<ClRef, List<Object>> summaryNew,File fileFebore){
//        GroovyConfigLoader2I configLoader2 =
        Map<ClRef, List<Object>>  dataBeforte = [:]
        GroovyConfigLoaderJrr.configLoaderAdvance.parseConfig(fileFebore).loadConfig(dataBeforte)
        Map<ClRef, List<Object>> before3 = removeMavenVersion(dataBeforte)
        Map<ClRef, List<Object>> after3 = removeMavenVersion(summaryNew)
//        Map<ClRef, List<Object>> before3Dup = new TreeMap<>(before3)
        if(before3 == after3){
            log.info "summary file equal"
        }else{
            onDifferent(before3,after3)
        }
    }

    /**
     * if new is good, replace summaryGolden.groovy from summaryLatest.groovy
     */
    abstract void onDifferent(Map<ClRef, List<Object>> before3,Map<ClRef, List<Object>> newO);

    List<Object> removeMavenVersion2(List<Object> aa){
        return aa.collect {
            if(it instanceof MavenId){
                return it.groupId+':'+it.artifactId
            }
            return it
        }
    }

    Map<ClRef, List<Object>> removeMavenVersion(Map<ClRef, List<Object>> aa){
        Map<ClRef, List<Object>> r = [:]
        aa.each {r.put(it.key,removeMavenVersion2( it.value))}
        return r
    }

    void handleService(Object location1, Collection<ClRef> services,Map<ClRef, List<Object>> result){
        services.each {
            List<Object> get1 = result.get(it)
            if(get1==null){
                get1 = []
                result.put(it, get1)
            }
            get1.add(location1)
        }
    }
}
