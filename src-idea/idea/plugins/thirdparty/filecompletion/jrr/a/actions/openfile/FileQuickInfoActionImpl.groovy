package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.swing.JDialog
import javax.swing.JTextArea
import javax.swing.SwingUtilities
import java.awt.BorderLayout
import java.awt.Frame
import java.awt.event.InputEvent
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent

@CompileStatic
class FileQuickInfoActionImpl extends AnAction {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


//    static AddFilesToClassLoaderGroovy addFilesToClassLoader = new AddFilesToClassLoaderGroovy() {
//        @Override
//        void addFileImpl(File file) throws Exception {
//
//        }
//    }


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
        InputEvent event = e.inputEvent
//        List l = [e,event,e.presentation]

        SwingUtilities.invokeLater {
            JDialog dialog = new JDialog((Frame) null, "File info")
            String text = file.getAbsolutePath().replace('\\', '/');
            JTextArea textArea = new JTextArea(text)
            dialog.getContentPane().add(textArea, BorderLayout.CENTER)
            dialog.setLocation(200, 300)
            dialog.setSize(300, 100)
            dialog.setVisible(true)
            dialog.toFront()
            textArea.requestFocusInWindow()
            textArea.requestFocus()
            textArea.addKeyListener(new KeyAdapter() {
                @Override
                void keyPressed(KeyEvent keyEvent) {
                    if (keyEvent.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        log.info "disposing dialog"
                        dialog.dispose()
                    }
                }
            })
        }

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
