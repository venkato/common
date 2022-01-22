package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean

import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.calchelpers.ClassPathCalculatorGroovyWise
import net.sf.jremoterun.utilities.nonjdk.classpath.repohash.FileCheckSumCalc
@CompileStatic
class FindJarFileInMavenActionImpl extends AnAction {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    void actionPerformed(AnActionEvent e) {
        log.debug "running ${e}"
        FileCompletionBean place = IdeaOpenFileUtils.getPlace(e,false,false)
        if(place.value==null){
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

    private void openFile(File file, AnActionEvent e) {
        String hash = FileCheckSumCalc.calcSha1ForFile(file)
        hash = '"'+hash+'"'
        String url = """http://search.maven.org/#search|ga|1|1:${hash}"""
        log.info "url =${url}"
        BrowserUtil.browse(url)
//        String[] ppp = [SettingsRef.config.browserPath, url]
//        Process browserOpened = Runtime.runtime.exec(ppp);

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
        FileCompletionBean place = IdeaOpenFileUtils.getPlace(e,false,false)
        boolean iok = place != null
        if (iok) {
            log.debug place.value
            String value = place.value;
            iok = value.endsWith(".jar")
            if (iok) {
                log.debug "found file method with path : ${value}"
            }
        }
        e.presentation.visible = iok
        e.presentation.enabled = iok
        //super.update(e)
    }


}
