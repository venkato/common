package idea.plugins.thirdparty.filecompletion.share

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.maven.OpenMavenIdImpl
import net.sf.jremoterun.utilities.JrrClassUtils

@CompileStatic
class OpenMavenIdInExternalToolAction extends DeligateAction {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static DeligateAction thisObject

    public OpenMavenIdInExternalToolAction() {
        super(new OpenMavenIdImpl())
        if (thisObject == null) {
            thisObject = this;
        } else {
            log.error("object already created")
        }
    }
}
