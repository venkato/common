package idea.plugins.thirdparty.filecompletion.jrr.a.javassist

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.JavaMethodCallElement
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import idea.plugins.thirdparty.filecompletion.jrr.a.actions.CompletionProviderCommon
import idea.plugins.thirdparty.filecompletion.jrr.a.file.MyAcceptFileProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib.ReflectionElement
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType

import javax.swing.*

@CompileStatic
public class JavassistCompletionProviderImpl extends CompletionProviderCommon {

    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static EnumSet<StaticFieldType> reFields = EnumSet.of(StaticFieldType.setFieldValue,
            StaticFieldType.getFieldValue, StaticFieldType.findField);

    public static EnumSet<StaticFieldType> reMethods = EnumSet.of(StaticFieldType.invokeJavaMethod,
            StaticFieldType.findMethodByCount,StaticFieldType.findMethodByParamTypes1,
            StaticFieldType.findMethodByParamTypes2,);

    @Override
    protected void addCompletions(
            CompletionParameters parameters, ProcessingContext context, CompletionResultSet result) {
        PsiElement psiElement = parameters.position;
        if (!(psiElement instanceof LeafPsiElement)) {
            return;
        }
        JavassistCompletionBean completionBean = MyAcceptJavassistProviderImpl.isOkPsiElement((LeafPsiElement) psiElement);
//        String value4 = MyAcceptFileProviderImpl.getStringFromPsiLiteral(completionBean.literalElement);
//        String realValue = value4.replace(IdeaMagic.addedConstant, '');

        String realValue = completionBean.getValueInLiteral();
        assert realValue!=null
        // int offset = value4.indexOf(IdeaMagic.addedConstant);
        // String valutoClac = value4.substring(0, offset);
        log.debug("cp 8 : value = ${realValue}")
        completionBean.onObjectClass.allMethods.sort { "${it.name} ${it.parameterList.parametersCount}" }.each {
            PsiMethod psiMethod222 = (PsiMethod) it;
            LookupElement element = new JavaMethodCallElement(psiMethod222)
            result.addElement(element);
        }
    }

    private void testNotUsed() {
        JButton testVar = null;
        JrrClassUtils.getFieldValue(testVar, "vetoableChangeSupport") //NOFIELDCHECK
        JrrClassUtils.findMethodByCount(testVar.class, "requestFocus",0) //NOFIELDCHECK
        JrrClassUtils.findMethodByCount(testVar.class, "requestFocus",0) //NOFIELDCHECK
        JrrClassUtils.findMethodByParamTypes1(testVar.class, "requestFocus") //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(testVar, "requestFocus") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(testVar, "verifyInputWhenFocusTarget") //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(testVar, "focu") //NOFIELDCHECK
    }
}
