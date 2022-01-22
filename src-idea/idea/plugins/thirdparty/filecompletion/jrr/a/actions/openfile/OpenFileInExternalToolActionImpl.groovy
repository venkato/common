package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.tools.Tool
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.idea.set2.SettingsRef

import javax.swing.JOptionPane
import javax.swing.SwingUtilities

@CompileStatic
class OpenFileInExternalToolActionImpl extends AnAction {
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
        if (file.exists()) {
            openFile(file, e);
        } else {
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("${file.name} file not found", new FileNotFoundException(file.absolutePath))
        }
    }


    static Tool findTool() {
        Tool tool
        if (SettingsRef.config.openFileTool != null) {
            tool = SettingsRef.config.openFileTool.findTool();
        }
        if (tool != null) {
            return tool
        }
        SwingUtilities.invokeLater {
            JOptionPane.showMessageDialog(null, "Set openFileTool in Library manager tab")
        }

        return null

    }

    static void openFile(File file, AnActionEvent event) {
        VirtualFile virtualFile = getVirtualFile(file)
        if (virtualFile == null) {
            //log.info "virtual file is null for ${file}"
        }else {
            DataContext dataContext
            if(event!=null) {
                dataContext = event.getDataContext()
            }
            OpenFileIdeaDataContext myDataContext = new OpenFileIdeaDataContext(dataContext, virtualFile)
            Tool tool = OpenFileInExternalToolActionImpl.findTool();
            tool.execute(event, myDataContext, 0L, null);
            log.debug "run action done"
        }
    }

    static VirtualFile getVirtualFile(File file) {
        VirtualFile virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file);
        log.debug "virtualFile : ${virtualFile}"
        if (virtualFile == null) {
            VirtualFile parent = LocalFileSystem.getInstance().findFileByIoFile(file.parentFile)
            if (parent == null) {
                net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("can't find virtual file", new FileNotFoundException("vertual file not found : ${file.absolutePath}"))
                return null
            }
            virtualFile = parent.children.find { it.name == file.name }
            log.debug "found child : ${virtualFile}"
            if (virtualFile == null) {
                log.info "refreshing folder ${file.parentFile}"
                parent.refresh(false, false)
                log.info "refresh finished"
                virtualFile = parent.children.find { it.name == file.name }
                if (virtualFile == null) {
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("can't find virtual file 2", new FileNotFoundException(file.absolutePath))
                    return null
                }
            }
        }
        if (virtualFile == null) {
            log.info "virtual file is null for ${file}"
        }
        return virtualFile

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

        }else {
            FileCompletionBean place = IdeaOpenFileUtils.getPlace(e, true,false)
            boolean iok = place != null

//            log.debug place
            if (iok) {
                log.debug "found file method with path 2 : ${place.value}"
            } else {
//            log.info "place not found"
            }

            e.presentation.visible = iok
            e.presentation.enabled = iok
            //super.update(e)
        }
    }


}
