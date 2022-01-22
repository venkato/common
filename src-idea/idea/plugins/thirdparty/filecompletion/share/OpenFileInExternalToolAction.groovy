package idea.plugins.thirdparty.filecompletion.share

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile.OpenFileInExternalToolActionImpl
import net.sf.jremoterun.utilities.JrrClassUtils

@CompileStatic
class OpenFileInExternalToolAction extends DeligateAction {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static DeligateAction thisObject

    public OpenFileInExternalToolAction() {
        super(new OpenFileInExternalToolActionImpl())
        if (thisObject == null) {
            thisObject = this;
        } else {
            log.error("object already created")
        }
    }
}
