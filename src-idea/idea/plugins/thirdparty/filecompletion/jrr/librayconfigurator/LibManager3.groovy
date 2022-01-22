package idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator

import com.intellij.openapi.Disposable
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.progress.ProgressManager
import com.intellij.openapi.progress.Task
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.OrderRootType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.libraries.LibraryTable
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IndexReadyListener
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.NewValueListener
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenDependenciesResolver
import net.sf.jremoterun.utilities.classpath.MavenFileType2
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.mdep.ivy.IvyDepResolver2
import net.sf.jremoterun.utilities.nonjdk.ideadep.LongTaskInfo
import net.sf.jremoterun.utilities.nonjdk.ivy.ManyReposDownloaderImpl

import javax.swing.JOptionPane
import javax.swing.SwingUtilities
import java.awt.Component
import java.util.logging.Level
import java.util.logging.Logger

@CompileStatic
public class LibManager3 extends LibManager33{


    private static final Logger log = Logger.getLogger(JrrClassUtils.getCurrentClass().getName());

    public IdeaLibManagerSwing dialog8;
//    Library library;
//    private TextFieldWithHistory txtDirectoryToSearch;

    LibManager3(IdeaLibManagerSwing dialog8) {
        this.dialog8 = dialog8
    }

    void addFileToLib(File fileToSave) {
        assert fileToSave.parentFile.exists()
        dialog8.txtDirectoryToSearch.addCurrentTextToHistory()
        dialog8.listStore.currentList.add(0, fileToSave)
        dialog8.listStore.currentList = dialog8.listStore.currentList.unique()
        dialog8.listStore.saveList6()
//        }
    }

    public void runImport(LibItem libItem) {
        File fileToSave = checkFile(dialog8.txtDirectoryToSearch.getText())
        if (fileToSave == null) {
            return
        }
        assert fileToSave.exists()
        assert fileToSave.file
        assert fileToSave.canRead()
        try {
            addFileToLib(fileToSave)
        } catch (Throwable e) {
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed remember choosed file ", e)
        }
        runImportImpl(libItem, fileToSave)
    }


    public void runExport(LibItem libItem) {
        try {
//            File fileToSave = dialog8.txtDirectoryToSearch.getText() as File
            File fileToSave = checkFile(dialog8.txtDirectoryToSearch.getText())
            if (fileToSave == null) {
                return
            }
            boolean pass = !fileToSave.exists()
            if (!pass) {
                pass = JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(null, "file exist, overwrite ? : ${fileToSave}")
            }
            log.info "pass ? ${pass}"
            if (pass) {

                assert fileToSave.parentFile.exists()
                try {
                    addFileToLib(fileToSave)
                } catch (Throwable e) {
                    net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed remember chosen file ", e)
                }
                runExportImpl(libItem, fileToSave)
            }
        } catch (Throwable e) {
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed export", e)
        }
    }

    @Override
    Component getCOmponentToLinkCoordination() {
        if (dialog8 != null) {
            return dialog8.txtDirectoryToSearch
        }
        return null
    }
}
