package net.sf.jremoterun.utilities.nonjdk.idea.init2

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.InitPlugin2
import net.sf.jremoterun.SimpleFindParentClassLoader
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.javaonly.InitInfo
import net.sf.jremoterun.utilities.nonjdk.LogExitTimeHook
import net.sf.jremoterun.utilities.nonjdk.classpath.refs.AsmOw
import net.sf.jremoterun.utilities.nonjdk.idea.IdeaCommonInit8
import net.sf.jremoterun.utilities.nonjdk.idea.init.IdeaClasspathAdd

import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
class IdeaInit7 implements Runnable{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void run() {
        init()
    }

    //public static boolean inited = false;
    public static InitInfo initInfo = new InitInfo(IdeaInit7)

    static void init() {
        if (initInfo.isInited()) {

        }else {
            initInfo.setInited()
            try {
                initImpl()
            }catch(Throwable e){
                log.log(Level.SEVERE,"failed init idea ",e);
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("failed init idea ",e);
                throw e;
            }
        }
    }

    static void initImpl() {
        IdeaClasspathAdd.addCl.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rsta()
        IdeaClasspathAdd.addCl.add net.sf.jremoterun.utilities.nonjdk.classpath.RstaJars.rstaAutoCompetion()
        IdeaClasspathAdd.addCl.add AsmOw.asm_commons
        IdeaCommonInit8.init1()
        LogExitTimeHook.addShutDownHook();
        SimpleFindParentClassLoader.setDefaultClassLoader(JrrClassUtils.getCurrentClassLoader());

            Logger.getLogger("idea.plugins.thirdparty.filecompletion").setLevel(Level.FINE)

        InitPlugin2.init()
        log.info2(new Date());
    }



}
