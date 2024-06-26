package idea.plugins.thirdparty.filecompletion.jrr

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.IdeaLibManagerSwing
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.IdeaRuntimeClassRefrences
import idea.plugins.thirdparty.filecompletion.share.Ideasettings.IdeaJavaRunner2Settings
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.groovystarter.st.SetConsoleOut2
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.idea.set2.SettingsRef
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

@CompileStatic
class InitPlugin2 {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static volatile boolean inited = false;

    static boolean init() {
        if (inited) {

        } else {
            inited = true
            initImpl()
        }
        return true
    }

    static void initImpl() {
        try {
            initImpl2()
        } catch (Throwable e) {
            log.error("Failed init idea", e)
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed init idea", e)
        }
    }

    static void initImpl2() {
        log.info "about to init"
        SetConsoleOut2.setConsoleOutIfNotInited();
        SettingsRef.loadSettingsS2()
        IdeaJavaRunner2Settings.initIfExist()
        InitPlugin.init();
        if (MavenDefaultSettings.mavenDefaultSettings.mavenDependenciesResolver == null) {
            IvyDepResolver2.setDepResolver()
        }
        IndexReadyListener.listenersAfterProjectOpened.add {
            try {
                log.info "creating IdeaLibManagerSwing .."
                IdeaLibManagerSwing.createIdeaPanel12();
                log.info "created IdeaLibManagerSwing"
            }catch(Throwable e){
                log.error("Failed init idea", e)
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed init idea", e)
            }
        }
        IdeaRuntimeClassRefrences.addReferences()
        IndexReadyListener.runListenersWhenReady2()

    }


}
