package idea.plugins.thirdparty.filecompletion.jrr.a.actions.maven

import com.intellij.ide.BrowserUtil
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.text.CharArrayUtil
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.TryResolveMavenFromEnum
import idea.plugins.thirdparty.filecompletion.jrr.a.file.MyAcceptFileProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.maven.MyAcceptMavenProviderImpl
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepo
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.nonjdk.maven.MavenPomUtils
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrEnumConstant

@CompileStatic
public class OpenMavenPomImpl extends AnAction {


    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    void actionPerformed(AnActionEvent e) {
        try {
            actionPerformedImpl(e);
        } catch (Throwable e2) {
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException('Failed open maven id', e2)
        }
    }

    void actionPerformedImpl(AnActionEvent e) {
        log.debug "running ${e}"
        MavenIdAndRepoContains place = getPlace(e)
        if (place == null) {
            log.debug "can't find file name"
            return
        }
        openFile3(place, e);
    }

    private void openFile3(MavenIdAndRepoContains file, AnActionEvent e) {
        MavenIdAndRepo mavenIdAndRepo = file.getMavenIdAndRepo()
        URL url1
        if(mavenIdAndRepo.repo==null) {
            url1 = MavenPomUtils.contactURLs(MavenDefaultSettings.mavenDefaultSettings.mavenServer, MavenPomUtils.buildPomSuffix(mavenIdAndRepo.m))
        }else{
            url1 = MavenPomUtils.buildPomURL (mavenIdAndRepo)
        }
        log.info "opening : ${url1}"
        BrowserUtil.browse(url1)
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
        MavenIdAndRepoContains place = getPlace(e)
        boolean iok = place != null
        if (iok) {
//            log.debug place
            if (iok) {
                log.debug "found file method with path : ${place}"
            }
        }
        e.presentation.visible = iok
        e.presentation.enabled = iok
        //super.update(e)
    }


    @Nullable
    private static MavenIdAndRepoContains getPlace(AnActionEvent event) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return null;
        }

        PsiElement psiElement1 = null;
        Document document = null;
        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        }
        if (editor != null) {
            document = editor.getDocument();
//                log.debug "editor found "
            PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
            if (file != null) {
                final VirtualFile virtualFile = file.getVirtualFile();
                FileType fileType = virtualFile != null ? virtualFile.getFileType() : null;
                psiElement1 = findMethod(project, editor);
                log.debug "psiElement1 ${psiElement1?.class.name} ${psiElement1}"
            } else {
                // log.debug "file is null"
            }
        }
        if (psiElement1 instanceof LeafPsiElement) {
            PsiElement parent222 = psiElement1.getParent()
            if (parent222 instanceof GrEnumConstant) {
                GrEnumConstant aa = parent222 as GrEnumConstant
                MavenIdAndRepoContains mmmm = TryResolveMavenFromEnum.tryResolve2MavenIdAndRepoContains(aa)
                if (mmmm == null) {
//                    log.info "cannot resolve enum"
                } else {
                    return mmmm
                }
            }
            if (MyAcceptMavenProviderImpl.isOkPsiElement(psiElement1 as LeafPsiElement)) {
                String value4 = MyAcceptFileProviderImpl.getStringFromPsiLiteral(psiElement1.parent);
                //List<String> ids = value4.tokenize(':')
                if (!idea.plugins.thirdparty.filecompletion.jrr.a.maven.IsMavenToken.isMavenToken(value4)) {
                    log.debug "Not a maven token ${value4}"
                    return null
                }
                MavenId mavenId = new MavenId(value4)
                return new MavenIdAndRepo(mavenId,null);
            }

        }
        return null;
    }

    @Nullable
    private static PsiElement findMethod(Project project, Editor editor) {
        if (editor == null) {
            return null;
        }
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
        if (psiFile == null) {
            return null;
        }
        final int offset = CharArrayUtil.shiftForward(editor.getDocument().getCharsSequence(), editor.getCaretModel().getOffset(), " \t");
        return psiFile.findElementAt(offset)
    }


}
