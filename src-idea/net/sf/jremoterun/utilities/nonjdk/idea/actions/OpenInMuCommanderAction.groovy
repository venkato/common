package net.sf.jremoterun.utilities.nonjdk.idea.actions

import com.intellij.ide.actions.RevealFileAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.vfs.VirtualFile
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.JrrUtilitiesShowE
import net.sf.jremoterun.utilities.classpath.ClRef
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

/**
 <action popup="true" class="net.sf.jremoterun.utilities.nonjdk.idea.actions.OpenInMuCommanderAction" id="net.sf.jremoterun.utilities.nonjdk.idea.actions.OpenInMuCommanderAction" text="Open in MuCommander" desciption="Open in MuCommander"  icon="/icons/open_file.png" >
 <add-to-group group-id="RunContextPopupGroup" anchor="last"/>
 </action>

 */
@CompileStatic
class OpenInMuCommanderAction extends AnAction {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    void actionPerformed(@NotNull AnActionEvent e) {
        try {
            actionPerformedImpl(e)
        } catch (Throwable e2) {
            log.info3('faile open file', e2)
            JrrUtilitiesShowE.showException('failed open file ', e2)
        }
    }

    void actionPerformedImpl(@NotNull AnActionEvent e) {

        new ClRef('com.intellij.ide.actions.OpenInAssociatedApplicationAction')
        new ClRef('com.intellij.ide.actions.RevealFileAction')
        VirtualFile file1 = getFile(e)
        if(file1==null){
            throw new NullPointerException("failed find file from event")
        }
        String canonicalPath = file1.getCanonicalPath()
//        File f = new File(canonicalPath)
        net.sf.jremoterun.utilities.nonjdk.mucom.OpenFolderInMuCommander.openFolderInMuCommander2.openInMuCommander3(canonicalPath)
    }


    static @Nullable
    VirtualFile getFile(AnActionEvent e) {
        return RevealFileAction.findLocalFile(e.getData(CommonDataKeys.VIRTUAL_FILE));
    }
}
