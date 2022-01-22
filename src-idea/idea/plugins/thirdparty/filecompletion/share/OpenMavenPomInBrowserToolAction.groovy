package idea.plugins.thirdparty.filecompletion.share

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.maven.OpenMavenIdImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.maven.OpenMavenPomImpl
import net.sf.jremoterun.utilities.JrrClassUtils

@CompileStatic
class OpenMavenPomInBrowserToolAction extends DeligateAction {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static DeligateAction thisObject

    public OpenMavenPomInBrowserToolAction() {
        super(new OpenMavenPomImpl())
        if (thisObject == null) {
            thisObject = this;
        } else {
            log.error("object already created")
        }
    }
}
