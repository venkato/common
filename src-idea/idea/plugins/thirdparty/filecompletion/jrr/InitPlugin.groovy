package idea.plugins.thirdparty.filecompletion.jrr


import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.CompetionContributerRenew
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanFromJavaBean
import net.sf.jremoterun.utilities.log4j.Log4jConfigurator
import org.apache.log4j.Level
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

@CompileStatic
class InitPlugin {
    //private static final Logger log = LogManager.getLogger(JrrClassUtils.currentClass);

    public static boolean inited = false;

    public static OSIntegrationIdea osIntegrationIdea

    static void init() {
        if(inited){

        }else {
            inited = true
            initImpl()
        }
    }

    static void initImpl() {
        Logger logger = LogManager.getLogger('idea.plugins.thirdparty.filecompletion')
        Log4jConfigurator.setLevelForLogger1(logger.name, Level.DEBUG)
        CompetionContributerRenew.regDocumentation()
        CompetionContributerRenew.regGotto()
        osIntegrationIdea= new OSIntegrationIdea()
        MBeanFromJavaBean.registerMBean(osIntegrationIdea);
    }


}
