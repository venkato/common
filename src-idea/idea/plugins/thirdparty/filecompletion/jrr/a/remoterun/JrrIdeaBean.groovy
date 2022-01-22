package idea.plugins.thirdparty.filecompletion.jrr.a.remoterun

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.javassist.jrrbean.JrrBeanMaker;

@CompileStatic
public class JrrIdeaBean {

    public static JrrIdeaBean bean = JrrBeanMaker
            .makeBeanAndRegisterMBeanNoEx(JrrIdeaBean);

    PsiElement psiElement2;
    PsiElement psiElement3;
    PsiField psiField;
    Object newTestObj2;

}
