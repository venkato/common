package idea.plugins.thirdparty.filecompletion.jrr.a.file

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiLiteral
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import net.sf.jremoterun.utilities.JrrClassUtils

@CompileStatic
class FileCompletionBean {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    PsiLiteral literalElemtnt
    String value
    File parentFilePath
    PsiElement wholeFileDeclaration


    String getValueInLiteral(){
        Object value = literalElemtnt.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        // ;
        //int offset = value3.indexOf(addedConstant);
        String realValue = value.replace(IdeaMagic.addedConstant, '');
        return realValue
    }
}
