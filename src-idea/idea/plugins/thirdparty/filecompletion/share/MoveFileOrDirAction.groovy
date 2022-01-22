package idea.plugins.thirdparty.filecompletion.share

import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile.FindJarFileInMavenActionImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile.MoveFileOrDirActionImpl
import net.sf.jremoterun.utilities.JrrClassUtils

@CompileStatic
class MoveFileOrDirAction extends DeligateAction {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public static DeligateAction thisObject

    public MoveFileOrDirAction() {
        super(new MoveFileOrDirActionImpl())
        if (thisObject == null) {
            thisObject = this;
        } else {
            log.error("object already created")
        }
    }
}
