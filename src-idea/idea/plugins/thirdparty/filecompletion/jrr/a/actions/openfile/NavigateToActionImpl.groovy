package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.ide.impl.ProjectViewSelectInTarget
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.ActionCallback
import com.intellij.openapi.vfs.JarFileSystem
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiManager
import com.intellij.psi.impl.file.PsiJavaDirectoryFactory
import com.intellij.psi.impl.file.PsiJavaDirectoryImpl
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.*
import java.awt.*
import java.awt.event.InputEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

@CompileStatic
class NavigateToActionImpl extends AnAction {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    void actionPerformed(AnActionEvent e) {
        log.debug "running ${e}"
        FileCompletionBean place = IdeaOpenFileUtils.getPlace(e, true,false)
        if (place.value == null) {
            log.debug "can't find file name"
            return
        }
        File file;
        if (place.parentFilePath == null) {
            file = new File(place.value)
        } else {
            file = new File(place.parentFilePath, place.value);
        }
        log.debug "file : ${file}"
        openFile(file, e);
        //runTool(myActionId, e.getDataContext(), e, 0L, null);
    }


    private void openFile(File file, AnActionEvent e) {
        log.info "navigating to ${file} .."
        if(file.isFile() && file.getName().endsWith('.jar')){
            VirtualFile path1 = JarFileSystem.getInstance().findFileByPath(file.getAbsolutePath().replace('\\', '/') + '!/')
            //PsiManager instance1 = PsiManager.getInstance(OSIntegrationIdea.getOpenedProject())
            PsiDirectory directory1 = new PsiJavaDirectoryFactory(OSIntegrationIdea.getOpenedProject()).createDirectory(path1)
            directory1.navigate(true)
        }else{
            VirtualFile vf = LocalFileSystem.getInstance().findFileByIoFile(file)
            ActionCallback select1 = ProjectViewSelectInTarget.select(OSIntegrationIdea.getOpenedProject(), null, 'ProjectPane', null, vf, true)
            log.info "${select1}"
        }
        log.info "navigated to ${file} .."
    }

    @Override
    void setInjectedContext(boolean worksInInjected) {
        super.setInjectedContext(worksInInjected)
    }

    @Override
    void update(AnActionEvent e) {
        // log.debug e
        final Project project = e.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return;
        }
        FileCompletionBean place = IdeaOpenFileUtils.getPlace(e, true,false)
        boolean iok = place != null
        if (iok) {
//            log.debug place
            if (iok) {
                log.debug "found file method with path : ${place.value}"
            }
        }
        e.presentation.visible = iok
        e.presentation.enabled = iok
        //super.update(e)
    }

}
