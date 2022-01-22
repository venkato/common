package idea.plugins.thirdparty.filecompletion.jrr.a.javassist

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiExpressionList
import com.intellij.psi.PsiLiteral
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import net.sf.jremoterun.utilities.JrrClassUtils

@CompileStatic
class JavassistCompletionBean {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    PsiClass onObjectClass
    PsiLiteral literalElement
    PsiExpressionList args


    String getValueInLiteral(){
        Object value = literalElement.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        // ;
        //int offset = value3.indexOf(addedConstant);
        String realValue = value.replace(IdeaMagic.addedConstant, '');
        return realValue
    }

}
