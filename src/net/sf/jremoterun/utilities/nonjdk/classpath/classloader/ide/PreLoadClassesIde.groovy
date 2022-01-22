package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide

import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import net.sf.jremoterun.FindParentClassLoader
import net.sf.jremoterun.utilities.ContextClassLoaderWrapper;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.nonjdk.FileRotate
import net.sf.jremoterun.utilities.nonjdk.apprunner.JavaProcessInfoE
import net.sf.jremoterun.utilities.nonjdk.classpath.classloader.LoadClassesByClassloader

import java.util.logging.Logger;

@CompileStatic
class PreLoadClassesIde {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public boolean failedOnException = false;
    public Map<String, Throwable> failedPlugins = [:];
    public List<String> skipPlugins = [];
    public Map<String, LoadClassesByClassloader> loadClassesByClassloaders = [:];
    public int minClassesToLoad = 3;

    public boolean preloadCurrentClassLoader= false

    public FindParentClassLoader findParentClassLoader

    public volatile LoadClassesByClassloader currentLoader;

    PreLoadClassesIde(FindParentClassLoader findParentClassLoader) {
        this.findParentClassLoader = findParentClassLoader
    }


    public void loadClassesByLocationAndPrevious(JavaProcessInfoE e) throws Exception {
        loadClassesByLocationAndPrevious(e.getClassesDumpFile(), e.getClassesDumpRotateCount())
    }

    public void loadClassesByLocationAndPrevious(File f, int count) throws Exception {
        Date startDate=new Date()
        if (f.exists()) {
            loadClassesByLocation(f);
        } else {
            log.info("file not exist, skip loading" + f);
        }
        while (count > 0) {
            File fBefore = FileRotate.buildRotateFile(f, f.getParentFile(), count, false);
            log.info("loading " + count + " " + fBefore);
            if (fBefore.exists()) {
                loadClassesByLocation(fBefore);
                log.info(" loaded fine " + fBefore);
            } else {
                log.info("missed, ignore " + fBefore);
            }
            count--;
        }
        printStat();
        long duration = System.currentTimeMillis()-startDate.getTime()
        log.info "load time duration = ${duration/1000} sec"
        showFailedPluginsExceptions()
    }

    void showFailedPluginsExceptions(){
        failedPlugins.each {
            JrrUtilitiesShowE.showException("failed ${it.key}",it.value)
        }
    }

    public void printStat() {
       List<String> failedLoadDetailsAll = []
       List<String> noClassFoundErrorLoadDetailsAll = []
        for (Map.Entry<String, LoadClassesByClassloader> entry : loadClassesByClassloaders.entrySet()) {
            log.info(entry.getKey() + " " + entry.getValue().buildStat());
            noClassFoundErrorLoadDetailsAll.addAll( entry.getValue().noClassFoundErrorLoadDetails)
            failedLoadDetailsAll.addAll( entry.getValue().failedLoadDetails)
        }
        if(failedLoadDetailsAll.size()==0){
            log.info "no failedLoad"
        }else {
            failedLoadDetailsAll =  failedLoadDetailsAll.unique().sort()
            log.info "failedLoadDetailsAll ${failedLoadDetailsAll.size()}: \n${failedLoadDetailsAll.join('\n')}"
        }
        if(noClassFoundErrorLoadDetailsAll.size()==0){
            log.info "no noClassFoundErrorLoad"
        }else {
            noClassFoundErrorLoadDetailsAll =  noClassFoundErrorLoadDetailsAll.unique().sort()
            log.info "failedLoadDetailsAll ${noClassFoundErrorLoadDetailsAll.size()}: \n${noClassFoundErrorLoadDetailsAll.join('\n')}"
        }
        List<String> failedPluingsHuman = failedPlugins.entrySet().collect { "${it.key} ${it.value}".toString() }
        if(failedPluingsHuman.size()==0){
            log.info "no failed plugins"
        }else {
            log.info "failed plugins ${failedPluingsHuman.size()} : \n${failedPluingsHuman.join('\n')}"
        }
    }

    public void loadClassesByLocation(File f) throws Exception {

        Map<String, Collection<String>> data = (Map) new JsonSlurper().parse(f);
        preLoadClasses(data);
    }

    public void preLoadClasses(Map<String, Collection<String>> preloadData) throws Exception {
        createLoadClassesByClassloaders(preloadData.keySet());
        Set<Map.Entry<String, Collection<String>>> entrySet = preloadData.entrySet();
        for (Map.Entry<String, Collection<String>> entry : entrySet) {
            try {
                if (isPreloadPlugin(entry.getKey(), entry.getValue())) {
                    preloadPlugin(entry.getKey(), entry.getValue());
                }
            } catch (Throwable e) {
                onException(entry.getKey(), e);
            }
        }

    }


    public void createLoadClassesByClassloaders(Collection<String> plugins) {
        for (String pluginId : plugins) {
            try {
                if (!loadClassesByClassloaders.containsKey(pluginId)) {
                    ClassLoader findPluginModuleClassLoader
                    findPluginModuleClassLoader = findParentClassLoader.findClassLoader(pluginId)

                    if (findPluginModuleClassLoader == null) {
                        throw new Exception("failed find plugin " + pluginId);
                    }
                    LoadClassesByClassloader loadClassesByClassloader = createLoadClassesByClassloader(pluginId, findPluginModuleClassLoader)
                    loadClassesByClassloaders.put(pluginId, loadClassesByClassloader);
                }
            }catch (Throwable e){
                onException(pluginId,e)
            }
        }
    }

    public LoadClassesByClassloader createLoadClassesByClassloader(String plugin, ClassLoader findPluginModuleClassLoader) {
        return new LoadClassesByClassloader(findPluginModuleClassLoader){
            @Override
            void loadClassImpl(ClassLoader classLoader2, String className1) {
                loadClass2(classLoader2, className1)
            }
        };
    }


    void loadClass2(ClassLoader classLoader2, String className1){
        classLoader2.loadClass(className1)
    }

    public boolean isPreloadPlugin(String pluginId, Collection<String> classesToBeLoaded) {
        if (skipPlugins.contains(pluginId)) return false;
        if (classesToBeLoaded.size() < minClassesToLoad) return false;
        LoadClassesByClassloader loadClassesByClassloader = loadClassesByClassloaders.get(pluginId);
        if(loadClassesByClassloader==null){
            log.info "no loader for ${pluginId}"
            return false;
        }
        if(loadClassesByClassloader.classLoader1 == getClass().getClassLoader()){
            return preloadCurrentClassLoader
        }
        return true;
    }

    public void preloadPlugin(String pluginId, Collection<String> classes) {
        LoadClassesByClassloader loadClassesByClassloader = loadClassesByClassloaders.get(pluginId);
        ContextClassLoaderWrapper.wrap2(loadClassesByClassloader.classLoader1, {
            LoadClassesByClassloader get1 = loadClassesByClassloaders.get(pluginId)
            currentLoader = get1
            get1.loadClassesByLocation(classes);
            currentLoader = null
        });

    }

    public void onException(String pluginId, Throwable e) throws Exception {
        if(!failedPlugins.containsKey(pluginId)) {
            failedPlugins.put(pluginId, e);
        }
        if (failedOnException) {
            log.info("failed on " + pluginId);
            throw e;
        }else{
            log.warn("failed on " + pluginId,e);
        }
    }

}
