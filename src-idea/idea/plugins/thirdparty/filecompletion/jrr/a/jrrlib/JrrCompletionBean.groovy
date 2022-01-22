package idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib

import com.intellij.psi.PsiClass
import com.intellij.psi.PsiLiteral
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList

@CompileStatic
class JrrCompletionBean {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public boolean fakeFirstArg = false
    public StaticFieldType methodName
    public PsiClass onObjectClass
    public Boolean onObjectStatic
    public PsiLiteral literalElement
    public GrArgumentList args


//    String getValueInLiteral(){
//        Object value = literalElement.getValue();
//        if (!(value instanceof String)) {
//            return null;
//        }
//        String realValue = value.replace(IdeaMagic.addedConstant, '');
//        return realValue
//    }
}
