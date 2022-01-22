package idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifierListOwner
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.CompletionProviderCommon
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldTypeCollection

import javax.swing.*

@CompileStatic
public class JrrCompletionProviderImpl extends CompletionProviderCommon {

    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static boolean filterStaticNonStatic = true

    @Override
    protected void addCompletions(
            CompletionParameters parameters, ProcessingContext context,  CompletionResultSet result) {
        PsiElement psiElement = parameters.position;
        if (!(psiElement instanceof LeafPsiElement)) {
            return;
        }
        JrrCompletionBean completionBean = MyAcceptJrrProviderImpl.isOkPsiElement((LeafPsiElement) psiElement);
//        String value4 = MyAcceptFileProviderImpl.getStringFromPsiLiteral(completionBean.literalElement);
//        int offset = value4.indexOf(IdeaMagic.addedConstant);
//        if(value4.length()==0){
//            offset = 0
//        }
        //String realValue = value4.replace(IdeaMagic.addedConstant, '');

//        if(offset==-1){
//            log.debug  "incorrect : ${value4}"
//            return
//        }
//        String valutoClac = value4.substring(0, offset);
//        if (offset < 0) {
//            log.error("invalid offset cp2 ${offset} , value = ${realValue} , value3 = ${value4}")
//            return;
//        }
//        log.debug("cp 9 : offset = ${offset} , value = ${realValue} , valutoClac = ${valutoClac}")
        if (StaticFieldTypeCollection.reFields.contains(completionBean.methodName)) {
            completionBean.onObjectClass.allFields
//                    .findAll {completionBean.onObjectStatic == null ||  it.hasModifierProperty('static') == completionBean.onObjectStatic }
                    .findAll { acceptField1(it,completionBean.onObjectStatic) }
                    .sort { it.name }.each {
                PsiField field = it;
                JavaGlobalMemberLookupElement globalMemberLookupElement = new JavaGlobalMemberLookupElement(field, field.containingClass, null, null, false) {
                    public void handleInsert(InsertionContext insertionContext) {

                    }
                };
                LookupElement element = globalMemberLookupElement;
                result.addElement(element);
            }
        } else {
            completionBean.onObjectClass.allMethods
                    .findAll { acceptField1(it,completionBean.onObjectStatic) }
//                    .findAll { completionBean.onObjectStatic == null ||it.hasModifierProperty('static') == completionBean.onObjectStatic }
                    .unique { "${it.name} ${it.parameterList.parametersCount}" }.sort {
                "${it.name} ${it.parameterList.parametersCount}"
            }.each {
                PsiMethod psiMethod222 = it;
                LookupElement element = new JavaGlobalMemberLookupElement(psiMethod222, psiMethod222.containingClass, null, null, false)
                result.addElement(element);
            }
        }
    }


    boolean acceptField1(PsiModifierListOwner it,Boolean onObjectStatic){
        if(!filterStaticNonStatic){
            return true
        }
        return onObjectStatic == null ||it.hasModifierProperty('static') == onObjectStatic
    }

    private void testNotUsed() {
        JButton testVar = null;
        JrrClassUtils.getFieldValue(JButton, "uiClassID") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(testVar, "ui") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(testVar, "uiClassID") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(testVar, "vetoableChangeSupport") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(testVar, "verifyInputWhenFocusTarget") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(testVar, "WHEN_IN_FOCUSED_WINDOW") //NOFIELDCHECK
        JrrClassUtils.findMethodByCount(testVar.getClass(), "computeVisibleRect",0) //NOFIELDCHECK
        JrrClassUtils.findMethodByCount(testVar.class, "requestFocus",0) //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(testVar, "requestFocus") //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(testVar, "") //NOFIELDCHECK
    }
}
