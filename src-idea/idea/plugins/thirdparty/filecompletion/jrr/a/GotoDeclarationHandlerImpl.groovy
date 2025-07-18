package idea.plugins.thirdparty.filecompletion.jrr.a

import com.intellij.codeInsight.navigation.actions.GotoDeclarationHandler
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpressionList
import com.intellij.psi.PsiField
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiMethod
import com.intellij.psi.impl.source.tree.LeafPsiElement
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.file.FileCompletionBean
import idea.plugins.thirdparty.filecompletion.jrr.a.file.MyAcceptFileProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.file.MySyntheticFileSystemItem
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.JavassistCompletionBean
import idea.plugins.thirdparty.filecompletion.jrr.a.javassist.MyAcceptJavassistProviderImpl
import idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib.JrrCompletionBean
import idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib.MyAcceptJrrProviderImpl
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList

import javax.swing.JButton

@CompileStatic
class GotoDeclarationHandlerImpl implements GotoDeclarationHandler {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    PsiElement[] getGotoDeclarationTargets(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        try {
            return getGotoDeclarationTargetsImpl(sourceElement, offset, editor)
        } catch (ProcessCanceledException e) {
            log.debug(e)
            throw e;
        } catch (Throwable e) {
            net.sf.jremoterun.utilities.JrrUtilitiesShowE.showException("Failed on calc goto delcarion", e)
            return new PsiElement[0];
        }
    }


    static PsiElement createFileELlemnt(File file) {
        if (file.exists()) {
            if (file.isDirectory()) {
                log.info "file is dir : ${file}"
//                SwingUtilities.invokeLater {
//                    JOptionPane.showMessageDialog(null,"File is dir : ${file.name}")
//                }
                return null;
            }
            if (file.length() > 1000 * 500) {
                log.debug "file size too big : ${file} ${file.length()}"
//                SwingUtilities.invokeLater {
//                    JOptionPane.showMessageDialog(null,"file size too big : ${file.name} ${file.length()}")
//                }
                return null;
            }
            return new MySyntheticFileSystemItem(file);
        } else {
            log.debug "file not exists ${file}"
//            SwingUtilities.invokeLater {
//                JOptionPane.showMessageDialog(null,"file not exists ${file.absolutePath}")
//            }
        }
        return null;
    }


    // keep protected and keep params
    protected PsiElement[] getGotoDeclarationTargetsImpl(@Nullable PsiElement sourceElement, int offset, Editor editor) {
        PsiElement el = getGotoDeclarationTargetsImpl2(sourceElement);
        if (el == null) {
            return new PsiElement[0];
        }
        PsiElement[] result = new PsiElement[1]
        result[0] = el
        return result;
    }

    // keep protected
    protected PsiElement getGotoDeclarationTargetsImpl2(@Nullable PsiElement sourceElement) {
        // log.debug "getGoTo ${sourceElement?.class.name} ${sourceElement}"
        if (sourceElement instanceof PsiJavaToken) {
            ChildFileLazy file = MyAcceptFileProviderImpl.isOkJavaPsiElement3(sourceElement);
            if (file == null) {

            } else {
                CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
                if (handler == null) {
                    throw new IllegalStateException("customObjectHandler was not set")
                }
                File f3341 = handler.resolveToFileIfDownloaded(file)
                if (f3341 == null) {
                    log.info "seems not download ${file} ${file.getClass().getName()} "
                    return null
                }
                return createFileELlemnt(f3341)
            }
        }
        if (!(sourceElement instanceof LeafPsiElement)) {
            return null
        }
        return getGotoDeclarationTargetsImpl3(sourceElement)
    }

    // keep protected
    protected PsiElement getGotoDeclarationTargetsImpl3(@Nullable LeafPsiElement sourceElement) {
        PsiElement resultss;
        resultss = maybeFile(sourceElement)
        if (resultss != null) {
            return resultss
        }
        resultss = maybeJavassist(sourceElement)
        if (resultss != null) {
            return resultss
        }
        resultss = maybeJrrLib(sourceElement)
        if (resultss != null) {
            return resultss
        }

        return null
    }

    private PsiElement maybeFile(LeafPsiElement sourceElement) {
        FileCompletionBean element = MyAcceptFileProviderImpl.isOkJavaAndGroovyPsiElement(sourceElement);
        if (element == null) {
            return null
        }
        File file;
        if (element.parentFilePath == null) {
            file = new File(element.value)
        } else {
            file = new File(element.parentFilePath, element.value);

        }
        return createFileELlemnt(file)

    }

    private PsiMethod maybeJavassist(LeafPsiElement sourceElement) {
        JavassistCompletionBean element1 = MyAcceptJavassistProviderImpl.isOkPsiElement(sourceElement)
        if (element1 == null) {
            return null
        }
        String realValue = element1.getValueInLiteral();
        if (realValue == null) {
            return null
        }
        //.collect{LookupElementBuilder.create(it.name)}
        PsiExpressionList args = element1.args;
        if (!(args instanceof GrArgumentList)) {
            log.debug("no gr ")
            return null
        }
        PsiLiteral grLiteral3 = (PsiLiteral) args.getAllArguments()[3]
        Integer paramCount = (Integer) grLiteral3.value;
        log.debug "paramCount = ${paramCount}"
        PsiMethod find = element1.onObjectClass.allMethods.find {
            it.name == realValue && it.parameterList.parametersCount == paramCount
        };
        return find;
    }

    private PsiElement maybeJrrLib(LeafPsiElement sourceElement) {
        JrrCompletionBean element2 = MyAcceptJrrProviderImpl.isOkPsiElement(sourceElement)
        if (element2 == null) {
            return null
        }
        return maybeJrrLib2(element2)
    }

    static PsiElement maybeJrrLib2(JrrCompletionBean element2) {
        String realValue = MyAcceptFileProviderImpl.getStringFromPsiLiteral(element2.literalElement);
        //String realValue = element2.getValueInLiteral();
        if (realValue == null) {
            return null
        }

        if (StaticFieldType.reFields.contains(element2.methodName)) {
            PsiField psiField = element2.onObjectClass.allFields.find { it.name == realValue }
            return psiField

        }
        GrArgumentList args = element2.args
        if (args == null) {
            return null
        }
        GroovyPsiElement[] allArguments = args.allArguments
        int paramCount = getParamCount3(allArguments, element2)
        log.debug "paramCount = ${paramCount}"
        if (paramCount == -1) {
            return null
        }
        PsiMethod psiMethod = element2.onObjectClass.allMethods.find {
            it.name == realValue && it.parameterList.parametersCount == paramCount
        };
        return psiMethod
    }

    static private int getParamCount3(GroovyPsiElement[] allArguments, JrrCompletionBean element2) {
//        GroovyPsiElement[] allArguments = args.allArguments
        switch (element2.methodName) {
            case StaticFieldType.findMethodByCount:
            case StaticFieldType.newMethodRef:
                GroovyPsiElement element5 = allArguments[2];
                if (!(element5 instanceof PsiLiteral)) {
                    log.info "failed find params"
                    return -1
                }
                def value2 = ((PsiLiteral) element5).getValue();
                if (value2 instanceof Integer) {
                    return value2;
                }
                break;
            case StaticFieldType.findMethodByParamTypes1:
                break
        }
        int counttt = allArguments.length - 2
        if(element2.methodName!=null && element2.methodName.isFirstParamFake()){
            counttt--
            assert counttt>=0
        }
        return counttt;
    }

    @Override
    String getActionText(DataContext context) {
        return "Open file in Idea"
    }

    private void testNotUsed() {
        JButton testVar = null;
        JrrClassUtils.getFieldValue(testVar, "ui") //NOFIELDCHECK

        JrrClassUtils.findMethodByCount(testVar.class, "computeVisibleRect", 2) //NOFIELDCHECK

        JrrClassUtils.findMethodByCount(testVar.class, "requestFocus", 0) //NOFIELDCHECK
        JrrClassUtils.findMethodByCount(new ClRef('javax.swing.JButton'), "requestFocus", 0) //NOFIELDCHECK

        JrrClassUtils.findMethodByParamTypes1(testVar.class, "requestFocus") //NOFIELDCHECK

        JrrClassUtils.invokeJavaMethod(testVar, "requestFocus") //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(testVar, "") //NOFIELDCHECK
    }



}
