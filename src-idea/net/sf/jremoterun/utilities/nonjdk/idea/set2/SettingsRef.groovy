package net.sf.jremoterun.utilities.nonjdk.idea.set2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr
import net.sf.jremoterun.utilities.nonjdk.BaseDirSetting

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class SettingsRef {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static IdeaLibManagerConfig config = new IdeaLibManagerConfig();

    public
    static ToFileRef2 location = BaseDirSetting.baseDirSetting.childL("idea/libmanager.groovy")

    public
    static ToFileRef2 locationEdit = BaseDirSetting.baseDirSetting.childL("idea/libmanager_edit.groovy");

    public static ToFileRef2 getLocation2(){
        return location;
    }



    static void loadSettingsS2() {
        try {
            if (location.resolveToFile().exists()) {
                SettingsRef.loadSettingsS(location.resolveToFile())
            }
        } catch (Throwable e) {
            log.log(Level.SEVERE, "failed load config", e)
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed load config", e)
        }
    }

    static void loadSettingsS(File configLocation) {
        log.info "file exist : ${configLocation} ? ${configLocation.exists()}"

        if (configLocation.exists()) {
//            try {
                GroovyConfigLoaderJrr.configLoaderAdvance.parseConfig(configLocation).loadConfig(config)
                //loadSettingsS(configLocation.text, configLocation.name, config)
//            } catch (Throwable e) {
//                log.info("failed load : ${configLocation}", e)
//                throw e
//            }
        } else {
            throw new FileNotFoundException("${configLocation}")
        }
    }

//    static void loadSettingsS(String scriptSource, String scriptName, IdeaLibManagerConfig config3) {
//        Binding binding = new Binding()
//        binding.setVariable(GroovyConfigLoader2.varName, config3)
//
//        Object script = RunnableFactory.groovyClassLoader.parseClass(scriptSource).newInstance()
//        //Script script = GroovySettingsLoader.groovySettingsLoader.createScript(scriptSource, scriptName, binding)
//        if (script instanceof GroovyConfigLoader2I) {
//            GroovyConfigLoader2I loader2 = (GroovyConfigLoader2I) script;
//            loader2.loadConfig(config3)
//        }else {
//            JrrUtilitiesShowE.showException("Strange loader",new JustStackTrace2())
//            ((Script)script).run();
//        }
//    }


}
