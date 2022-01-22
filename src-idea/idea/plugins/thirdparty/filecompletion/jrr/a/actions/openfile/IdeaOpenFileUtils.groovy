package idea.plugins.thirdparty.filecompletion.jrr.a.actions.openfile

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.text.CharArrayUtil
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.TryResolveMavenFromEnum
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import idea.plugins.thirdparty.filecompletion.jrr.a.file.MyAcceptFileProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.maven.IsMavenToken
import idea.plugins.thirdparty.filecompletion.jrr.a.maven.MyAcceptMavenProviderImpl
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenCommonUtils
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.classpath.MavenId
import net.sf.jremoterun.utilities.classpath.MavenIdAndRepoContains
import net.sf.jremoterun.utilities.classpath.MavenIdContains
import net.sf.jremoterun.utilities.classpath.ToFileRef2
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.MavenIdAndRepoCustomSourceContains
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrEnumConstant

@CompileStatic
class IdeaOpenFileUtils {

    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static MavenCommonUtils mavenCommonUtils = new MavenCommonUtils()

    static FileCompletionBean getPlace(AnActionEvent event, boolean searchForMavenId,boolean doForFilesOnly) {
        PsiElement psiElement1 = getPlace3(event);
        if (psiElement1 == null) {
            return null
        }
        FileCompletionBean place2 = getPlace2(psiElement1, searchForMavenId,doForFilesOnly)
//        log.info "a pace2 : ${place2!=null}"
        return place2
    }

    static Editor findEditor(AnActionEvent event) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        if (project == null) {
            return null;
        }

        Editor editor = event.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        }
        return editor

    }

    private static PsiElement getPlace3(AnActionEvent event) {
        final Project project = event.getData(CommonDataKeys.PROJECT);
        Editor editor = findEditor(event)
        if (editor == null) {
//            log.info "a editor is null"
            return null
        }
        Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null) {
//            log.info "a file is null"
            return null
        }
//                final VirtualFile virtualFile = file.getVirtualFile();
//                FileType fileType = virtualFile != null ? virtualFile.getFileType() : null;
        PsiElement psiElement = findMethod(project, document, editor);
//                log.debug "psiElement1 ${psiElement1?.class.name} ${psiElement1}"
        log.info "a psi el found : ${psiElement!=null}"
        return psiElement
    }


    private static FileCompletionBean getPlace2(PsiElement psiElement1, boolean searchForMavenId,boolean doForFilesOnly) {
        if (!(psiElement1 instanceof LeafPsiElement)) {
            return null
        }
        if (searchForMavenId) {
            if(!doForFilesOnly) {
                if (psiElement1.getParent() instanceof GrEnumConstant) {

                    GrEnumConstant aa = psiElement1.getParent() as GrEnumConstant
                    Object mmmm2 = TryResolveMavenFromEnum.tryResolve2(aa)
                    if (mmmm2 != null) {
                        if (mmmm2 instanceof ToFileRef2 ||mmmm2 instanceof MavenIdAndRepoContains ||mmmm2 instanceof MavenIdContains ||mmmm2 instanceof MavenIdAndRepoCustomSourceContains ) {
//                        ToFileRef2 fileRef2 = mmmm2;
                            CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
                            File f3 = handler.resolveToFileIfDownloaded(mmmm2)
                            if (f3 != null) {
                                FileCompletionBean element = new FileCompletionBean()
                                element.value = f3.absolutePath
                                return element

                            }
                        }
                    }
                }
            }
        }
        if (searchForMavenId && MyAcceptMavenProviderImpl.isOkPsiElement(psiElement1)) {
            String value4 = MyAcceptFileProviderImpl.getStringFromPsiLiteral(psiElement1.parent);
            if (!IsMavenToken.isMavenToken(value4)) {
                log.debug "Not a maven token ${value4}"
                return null
            }
            MavenId mavenId = new MavenId(value4)
            File file = mavenCommonUtils.findMavenOrGradle(mavenId)
            if (file == null) {
                log.info "Maven token not found : ${value4}"
                return null
            }
            FileCompletionBean element = new FileCompletionBean()
            element.value = file.absolutePath
            return element
        }

        log.debug "cp4"
        FileCompletionBean element = MyAcceptFileProviderImpl.isOkJavaAndGroovyPsiElement(psiElement1)
        if (element != null) {
            return element
        }
        log.debug "cp2 ${psiElement1}"
        ChildFileLazy element99 = isVar(psiElement1);
        if (element99 == null) {
            log.debug "cp3 not found ${psiElement1}"
            return null
        }
        CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
        if(handler==null){
            throw new IllegalStateException("customObjectHandler was not set")
        }
        File f3341 =  handler.resolveToFileIfDownloaded(element99)
        if(f3341==null){
            log.info "seems not download ${element99} ${element99.getClass().getName()} "
            return null
        }
        FileCompletionBean completionBean = new FileCompletionBean()
        completionBean.value = f3341.name
        completionBean.parentFilePath = f3341.parentFile
        return completionBean;
    }

    private static ChildFileLazy isVar(PsiElement psiElement) {
        if (!(psiElement instanceof LeafPsiElement)) {
            return null
        }
        log.debug "cp 1"
        PsiElement parent3 = psiElement.parent
        if (parent3 instanceof GrReferenceExpression) {
            log.debug "cp 2"
            GrReferenceExpression e = (GrReferenceExpression) parent3;
            PsiType type = e.type;
            if (type instanceof PsiClassType) {
                log.debug "cp 3"
                PsiClass resolve = type.resolve()
                if (resolve == null || !(resolve.name.contains('File'))) {
                    log.debug "no a file"
                    return null
                }
                log.debug("accpted")
                if (!(e.sameNameVariants?.length == 1)) {
                    log.debug "args not 1"
                    return null;
                }
                PsiElement varRef = e.sameNameVariants[0].element;
                ChildFileLazy var3 = MyAcceptFileProviderImpl.findFileFromVarGeneric(varRef)
                log.debug "accteped : ${var3}"
                return var3

            }
            return null
        } else {
            ChildFileLazy file1 = MyAcceptFileProviderImpl.findFileFromVarGeneric(parent3)
            log.debug "found : ${file1} ${parent3}"
            return file1

        }


    }

    @Nullable
    private static PsiElement findMethod(Project project, Document document,Editor editor) {
        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (psiFile == null) {
//            log.info "a psi file found"
            return null;
        }
        final int offset = CharArrayUtil.shiftForward(document.getCharsSequence(), editor.getCaretModel().getOffset(), " \t");
        return psiFile.findElementAt(offset)
    }
}
