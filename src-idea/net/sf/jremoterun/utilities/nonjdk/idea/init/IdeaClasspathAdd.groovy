package net.sf.jremoterun.utilities.nonjdk.idea.init

import com.intellij.ide.plugins.cl.PluginClassLoader
import groovy.transform.CompileStatic
import net.sf.jremoterun.SharedObjectsUtils
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.GroovyMethodRunner

import net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderGeneric
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2


import java.lang.reflect.Method
import java.nio.file.Path
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public class IdeaClasspathAdd implements GroovyObject{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static volatile boolean inited = false;

    public static volatile Thread initThread;

    public static PluginClassLoader pluginClassLoader = (PluginClassLoader) JrrClassUtils.getCurrentClassLoader();

    public static List<Integer> traceFlags = [];
    public static File ideaInitScriptDebug;

    public static AddFilesToClassLoaderGroovyIdea addCl = new AddFilesToClassLoaderGroovyIdea();
    public static boolean addFileMethodTriedFind  =false;
    public static Method addFileMethod;
    public static IdeaClassPathStoreI ideaClassPathStore;

    static void addFileToClassLoader(File file){
        if(addFileMethod==null){
            if(addFileMethodTriedFind){

            }else {
                addFileMethodTriedFind = true
                try {
                    addFileMethod = JrrClassUtils.findMethodByCount(pluginClassLoader.getClass(), 'addFiles', 1);
                }catch(NoSuchMethodException e){
                    log.log(Level.INFO,"failed find addFiles method",e)
                }
            }
        }
        if(addFileMethod==null){
            pluginClassLoader.addURL(file.toURL());
        }else {
            List<Path> paths = [file.toPath()]
            addFileMethod.invoke(pluginClassLoader,paths)
        }
    }

    public static void init() throws Exception {
        inited = true;
        traceFlags.add  710;
        //net.sf.jremoterun.utilities.java11.Java11ModuleSetDisable.doIfNeeded()
        traceFlags.add  711;
        SharedObjectsUtils.getClassLoaders().put(IdeaClassPathSettings.pluginCLassloaderId, pluginClassLoader);
        traceFlags.add  720;
        SimpleFindParentClassLoader.setDefaultClassLoader(pluginClassLoader);
        traceFlags.add  730;
        log.info("classes added fine");
        initThread = Thread.currentThread();
        SetConsoleOut2.setConsoleOutIfNotInited();
        traceFlags.add  740;
        try {
            Runnable r = new ClRef('net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClassPathStoreImpl').newInstance3() as Runnable
            r.run()
            ideaClassPathStore= r as IdeaClassPathStoreI
            ideaClassPathStore.useCache(false)
            traceFlags.add 743;
        }catch (Throwable e){
            log.log(Level.WARNING,"failed disable cache",e)
            traceFlags.add 744;
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed disable cache",e);
        }
        traceFlags.add  745;
        runScriptInUserDir()
        traceFlags.add  750;
        runCustom()
        traceFlags.add  760;
    }

    static void runScriptInUserDir() {
        if(JrrStarterVariables2.getInstance().filesDir==null){
            traceFlags.add  410;
            log.severe "files dir is null"
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("files dir is null",new Exception("files dir is null"));
        }else{
            ideaInitScriptDebug = new File(JrrStarterVariables2.getInstance().filesDir,IdeaClassPathSettings.ideaInitGroovyScriptName);
            if(ideaInitScriptDebug.exists()){
                traceFlags.add 420;
                if(JrrStarterVariables2.getInstance().classesDir!=null){
                    traceFlags.add 430;
                    if(JrrStarterVariables2.getInstance().classesDir.exists()){
                        addCl.addF JrrStarterVariables2.getInstance().classesDir
                    }else{
                        log.error("file not exit : ${JrrStarterVariables2.getInstance().classesDir}")
                    }

                }
                log.info("running ${ideaInitScriptDebug} ..");
                runFile(ideaInitScriptDebug)
                log.info("finished ${ideaInitScriptDebug}");
            }else{
                log.severe "file not exist : ${ideaInitScriptDebug}"
                traceFlags.add 440;
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("file not exist : ${ideaInitScriptDebug}",new Exception("file not exist : ${ideaInitScriptDebug}"));
            }
            traceFlags.add 450;
        }
    }


    static void runFile(File f){
        RunnableFactory.runRunner(f)
//        net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory.receiveGroovyClassLoader()
//        GroovyConfigLoaderGeneric groovyConfigLoaderJrr=new GroovyConfigLoaderGeneric()
//        Object obj1 = groovyConfigLoaderJrr.parseConfig(f)
//        if (obj1 instanceof Script) {
//            Script parse = (Script) obj1;
//            parse.run()
//        }else{
//            Runnable runnable = obj1 as Runnable
//            runnable.run()
//        }
    }


    static void runCustom() {
        traceFlags.add 460;
        String customScript = System.getProperty(IdeaClassPathSettings.customScriptProperty)
        if (customScript == null) {
            traceFlags.add 470;
            log.info("property not set : ${IdeaClassPathSettings.customScriptProperty}");
        }else{
            traceFlags.add 480;
            File customScriptF = customScript as File
            if (customScriptF.exists()) {
                traceFlags.add 490;
                log.info("running ${customScriptF} ..");
                runFile(customScriptF)
                log.info("finished ${customScriptF}");
            }else{
                traceFlags.add 500;
                log.info "File not found ${customScript}"
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("File not found ${customScript}",new FileNotFoundException("${customScript}"))
            }
            traceFlags.add 510;
        }
    }



}
