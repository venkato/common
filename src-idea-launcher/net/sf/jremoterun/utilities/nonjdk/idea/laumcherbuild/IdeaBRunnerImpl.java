package net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild;

import groovy.lang.GroovyClassLoader;
import net.sf.jremoterun.utilities.java11.sep1.Java11ModuleAccessF;
import org.jetbrains.jps.cmdline.LauncherOriginal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class IdeaBRunnerImpl implements Runnable {

    private static final Logger log = Logger.getLogger(IdeaBRunnerImpl.class.getName());

    @Override
    public void run() {
        try {
            Java11ModuleAccessF.disableJavaModuleCheckIfNeeded();
            f1();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    void f1() throws Exception {
        log.info("cp5");
        GroovyClassLoader groovyClassLoader = (GroovyClassLoader) IdeaBuildRunnerSettings.groovyCl;

        Map ee= (Map) groovyClassLoader.loadClass("net.sf.jremoterun.utilities.groovystarter.JrrStarterVariables2ClassesGetter").newInstance();
        List<File> f = (List<File>) ee.get(null);
        log.info("configs = "+f);
        if(f.get(0)!=null&& f.get(0).exists()){
            IdeaBuilderAddGroovyRuntime.addUrlToCl( IdeaBuildRunnerSettings.groovyCl,f.get(0));
        }

//        try {
//            Runnable r23 = (Runnable) groovyClassLoader.loadClass("net.sf.jremoterun.utilities.nonjdk.idea.laumcherbuild.IdeaBRunnerSetJrrVars2").newInstance();
//            r23.run();
//        }catch (Exception e){
//            log.log(Level.SEVERE,"failed run jrr lib std",e);
//        }
        File config4 = f.get(2);
        if(config4==null||!config4.exists()){
            loadDefault5(config4);
        }else {
            File ideaBuilderConfigFile = new File(config4, IdeaBuildRunnerSettings.ideaBuilderConfigFileName);
            log.info("loading config : " + ideaBuilderConfigFile);
            if (ideaBuilderConfigFile.exists()) {
                Class aClass = groovyClassLoader.parseClass(ideaBuilderConfigFile);
                Runnable instance = (Runnable) aClass.newInstance();
                log.info("running : " + aClass);
                instance.run();
                log.info("cp7");
            } else {

            }
        }


    }

    void loadDefault5(File ideaBuilderConfigFile)throws Exception {
        log.info("idea config file not exists : " + ideaBuilderConfigFile);
        if (IdeaBuildRunnerSettings.startOriginal) {
            LauncherImpl.runOriginal();
        } else {
            throw new FileNotFoundException(ideaBuilderConfigFile.getAbsolutePath());
        }

    }

}
