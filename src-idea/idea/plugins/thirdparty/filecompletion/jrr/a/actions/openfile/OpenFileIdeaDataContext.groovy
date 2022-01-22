package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.vfs.VirtualFile
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import org.jetbrains.annotations.NonNls

@CompileStatic
class OpenFileIdeaDataContext implements DataContext {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    DataContext deligateDc;

    VirtualFile file;

    OpenFileIdeaDataContext(DataContext deligateDc, VirtualFile file) {
        this.deligateDc = deligateDc
        this.file = file
    }

    @Override
    Object getData(@NonNls String dataId) {
        log.debug "dataId : ${dataId}"
        if (dataId == 'virtualFile') {
            return file
        }
        if(deligateDc==null){
            if(dataId =='project'){
                return OSIntegrationIdea.getOpenedProject()
            }
            throw new NullPointerException("conext is null for ${dataId} ")
        }
        return deligateDc.getData(dataId)
    }
}
