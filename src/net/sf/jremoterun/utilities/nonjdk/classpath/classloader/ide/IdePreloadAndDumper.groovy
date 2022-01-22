package net.sf.jremoterun.utilities.nonjdk.classpath.classloader.ide;

import groovy.transform.CompileStatic
import net.sf.jremoterun.SimpleFindParentClassLoader;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.apprunner.JavaProcessInfoE;

import java.util.logging.Logger;

@CompileStatic
class IdePreloadAndDumper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public DumpLoadedClassesIde dumpLoadedClassesIde;
    public PreLoadClassesIde preLoadClassesIde;
    public JavaProcessInfoE javaProcessInfoE
    public IdePluginsClassloaderInfo idePluginsClassloaderInfo
    public int periodInSec;
    public IdeFindParentClassLoader ideFindParentClassLoader

//    IdePreloadAndDumper() {
//    }

    public IdePreloadAndDumper(IdePluginsClassloaderInfo idePluginsClassloaderInfo, JavaProcessInfoE e, int periodInSec) {
        this.idePluginsClassloaderInfo = idePluginsClassloaderInfo
        this.periodInSec = periodInSec
        this.javaProcessInfoE = e
        ideFindParentClassLoader = new IdeFindParentClassLoader(idePluginsClassloaderInfo);
        assert javaProcessInfoE!=null
        assert idePluginsClassloaderInfo!=null
    }


    void init(){
        dumpLoadedClassesIde = new DumpLoadedClassesIde(javaProcessInfoE.getClassesDumpFile(), periodInSec, idePluginsClassloaderInfo)
        preLoadClassesIde = new PreLoadClassesIde(idePluginsClassloaderInfo);
    }

    void start() {
        init()
        try {
            preLoadClassesIde.loadClassesByLocationAndPrevious(javaProcessInfoE)
        }catch (Exception e){
            log.warn("failed load previous classes",e)
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed load previous classes",e)
        }
        dumpLoadedClassesIde.startClassDumping(javaProcessInfoE.getClassesDumpRotateCount())
        if(ideFindParentClassLoader!=null){
            ideFindParentClassLoader.selfSet()
            SimpleFindParentClassLoader.setDefaultClassLoader(getClass().getClassLoader());
        }
    }
}
