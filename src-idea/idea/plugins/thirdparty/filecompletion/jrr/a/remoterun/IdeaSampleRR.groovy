package idea.plugins.thirdparty.filecompletion.jrr.a.remoterun

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.CompetionContributerRenew
import net.sf.jremoterun.jrrlauncher.JmxRmiParamsForRemoteSystem
import net.sf.jremoterun.jrrlauncher.JrrJmxRmiLauncher
import net.sf.jremoterun.utilities.JrrClassUtils


@CompileStatic
public class IdeaSampleRR extends JrrJmxRmiLauncher {

    // run idea with jmx enabled with the same port as here
    private static int jmxPort = 6092

    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    protected void jrrMethodSwitcher() throws Throwable {
        try {
            rrunTest1()
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    protected void jrrRunRemoteCodeBefore() throws Exception {
        JmxRmiParamsForRemoteSystem params3 = getParams() as JmxRmiParamsForRemoteSystem;
        params3.port = jmxPort;
        params3.classLoaderId = null;
    }


    public static void main(String[] args) {
        JrrJmxRmiLauncher.jrrRunRemoteCodeStatic();
    }


    public void rrunTest1() {
        System.out.println "hello world printed in idea";
        log.error "hello world printed in idea";

    }

    public void rrunRenewAll() {
        //Logg.getLogger('idea.plugins.thirdparty.filecompletion').setLevel(Level.DEBUG)
        CompetionContributerRenew.renewAll()
        CompetionContributerRenew.renewJavaContextAssist()
        log.info "${new Date()}"
    }

}
