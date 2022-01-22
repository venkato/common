package net.sf.jremoterun.utilities.nonjdk.serviceloader.dumpservices

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.AllClasspathAnalysis
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ExactChildPattern
import net.sf.jremoterun.utilities.nonjdk.store.MapStore2;

import java.util.logging.Logger;

@CompileStatic
abstract class ServiceLoaderAnalizer {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public AllClasspathAnalysis analysis;

    public net.sf.jremoterun.utilities.nonjdk.serviceloader.dumpservices.ServicesDDumper servicesDDumper = new ServicesDDumper();
    public Map<File, Map<ClRef, List<ClRef>>> servicesRaw
    public Map<Object, Map<ClRef, List<ClRef>>> servicesHuman = [:]
    public MapStore2 mapStore2 = new MapStore2()
    public MapStore2 mapStore3 = new MapStore2()

    public ExactChildPattern rawChild = new ExactChildPattern('servicesRaw.groovy')
    public ExactChildPattern summaryGoldenChild = new ExactChildPattern('servicesSummaryGolden.groovy')
    public ExactChildPattern summaryLatestChild = new ExactChildPattern('servicesSummaryLatest.groovy')

    ServiceLoaderAnalizer(AllClasspathAnalysis analysis) {
        this.analysis = analysis
        mapStore2.writeCreatedAt= false
        mapStore3.writeCreatedAt= false
    }


    void analyze(){
        servicesRaw = servicesDDumper.readServicesManyFiles(analysis.adder.locationMap.keySet())
        servicesRaw.each {
            servicesHuman.put( analysis.file2HumanMap.get(it.key),it.value);
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


    void saveServicesToFile(File dir, int rotateCount){
        analyze()
        String newDataRaw = mapStore2.save3(servicesHuman)
        //getServicesSummary()
//        new File(dir,'rawLatest.groovy').text = newDataRaw
        Map<ClRef, List<Object>> summaryM = getServicesSummary()

        String newDataSummary = mapStore3.save3(summaryM)
        new File(dir,summaryLatestChild.child).text = newDataSummary
        //Map dataBefore = [:]
        File rawDataFIle = new File(dir,rawChild.child)
        File summaryDataFIle = new File(dir,summaryGoldenChild.child)
//        RunnableWithParamsFactory.fromClass5(rawDataFIle,dataBefore)
//        GroovyConfigLoader2 configLoader2 = RunnableFactory.groovyClassLoader.parseClass(newDataRaw).newInstance()
//        Map dataAfter = [:]
//        configLoader2.loadConfig(dataAfter)

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
//        if(!summaryDataFIle.exists() || newDataSummary!=summaryDataFIle.text){
//            FileRotate.rotateFile(summaryDataFIle,rotateCount)
//            summaryDataFIle.text = newDataSummary
//        }
    }

    void analizeSummary(Map<ClRef, List<Object>> summaryNew,File fileFebore){
        GroovyConfigLoader2 configLoader2 = RunnableFactory.groovyClassLoader.parseClass(fileFebore).newInstance()
        Map<ClRef, List<Object>>  dataBeforte = [:]
        configLoader2.loadConfig(dataBeforte)
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
