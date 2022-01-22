package idea.plugins.thirdparty.filecompletion.jrr.a.jrrlib

import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiField
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.PsiClassReferenceType
import com.intellij.psi.impl.source.PsiImmediateClassType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.a.GenericRefNames
import idea.plugins.thirdparty.filecompletion.jrr.a.remoterun.JrrIdeaBean
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.FieldResolvedDirectly
import idea.plugins.thirdparty.filecompletion.share.OSIntegrationIdea
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement
import org.jetbrains.plugins.groovy.lang.psi.api.GroovyResolveResult
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrField
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrVariable
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrMethodCall
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrNewExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrSafeCastExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrTypeCastExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression
import org.jetbrains.plugins.groovy.lang.psi.api.types.GrCodeReferenceElement
import org.jetbrains.plugins.groovy.lang.psi.impl.GrClassReferenceType
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrNewExpressionImpl

import java.lang.management.ManagementFactory

@CompileStatic
public class MyAcceptJrrProviderImpl implements ElementPattern<PsiElement> {
    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    public boolean accepts(@Nullable Object o) {
        if (o instanceof com.intellij.psi.impl.source.tree.LeafPsiElement) {
            JrrIdeaBean.bean.psiElement2 = (PsiElement) o;
            boolean accepted = isOkPsiElement((LeafPsiElement) o) != null;
            log.debug "${o} ${accepted}"
            return accepted

        }
        return false;
    }

    public static JrrCompletionBean isOkPsiElement(LeafPsiElement leafPsiElement) {
//        log.info "checking jrr accept"
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
            log.debug "not psi literal ${parent}"
            return null;
        }
        PsiLiteral literalElemtnt = parent as PsiLiteral;
        Object value = literalElemtnt.getValue();
        if (value instanceof String) {
        } else {
            log.debug "not string ${value}"
            return null;
        }
        JrrCompletionBean completionBean = new JrrCompletionBean();
        completionBean.literalElement = literalElemtnt;
        PsiElement parent1 = literalElemtnt.getParent();
        if (!(parent1 instanceof GrArgumentList)) {
            log.debug "not gr arg ${parent1}"
            return null;
        }

        GrArgumentList args = (GrArgumentList) parent1;
        completionBean.args = args
        if (args.allArguments.length < 2) {
            log.debug "bad arg count ${args.allArguments}"
            return null;
        }

        if (args.allArguments[1] != literalElemtnt) {
            if (args.allArguments.length > 2) {
                if (args.allArguments[2] == literalElemtnt) {

                } else {
                    log.debug "bad second arg ${args.allArguments[1]}"
                    return null
                }
            } else {
                return null
            }
        }
        final GroovyPsiElement firstEl = args.allArguments[0]
        if ((firstEl instanceof GrMethodCallExpression)) {
            log.info "gr call1 ${firstEl.getName()}"
            GrMethodCallExpression callExpression = (GrMethodCallExpression) firstEl
            PsiType psiType1 = callExpression.getType()
            if (!(psiType1 instanceof PsiClassType)) {
                log.debug "bad"
                return null
            }
            PsiClass resolve = psiType1.resolve()
            if (resolve.getQualifiedName() == 'java.lang.Class') {
                completionBean.onObjectStatic = true;
                GrExpression invokedExpression = callExpression.invokedExpression
                if (!(invokedExpression instanceof GrReferenceExpression)) {
                    log.debug "bad"
                    return null
                }
                GrExpression qualifier = (GrExpression) ((GrReferenceExpression) invokedExpression).getQualifier()
                PsiType psiType = qualifier.type
                if (psiType instanceof PsiClassType) {
                    completionBean.onObjectClass = psiType.resolve()
                } else {
                    log.debug "bad"
                    return null
                }
//            }else if (resolve.getQualifiedName() == net.sf.jremoterun.utilities.classpath.ClRef.getName()) {

            } else {
                completionBean.onObjectClass = resolve
                completionBean.onObjectStatic = false;
            }
        } else if ((firstEl instanceof GrNewExpressionImpl)) {
            GrNewExpressionImpl grNewExpression = firstEl
            PsiType psiType = grNewExpression.getType()
            if (psiType instanceof GrClassReferenceType) {
                GrClassReferenceType classReferenceType = psiType;
                String qualifiedName1 = classReferenceType.getCanonicalText()
                if (qualifiedName1 == net.sf.jremoterun.utilities.classpath.ClRef.getName()) {
                    completionBean.onObjectClass = resolveClRef(grNewExpression)
                } else {
                    log.info("unknow type ${qualifiedName1}")
                }
            } else {
                log.info("unknow prsi type ${psiType}")
            }
        } else if ((firstEl instanceof GrReferenceExpression)) {
//            log.info "gr expression"
            GrReferenceExpression onObject = (GrReferenceExpression) firstEl;
            PsiType firstArgumentType = onObject.getType()
            if (firstArgumentType instanceof PsiImmediateClassType) {
                String text1 = onObject.getText()
//                log.info "text1 = ${text1} . ${GenericRefNames.this_.customName} .. ${(text1 == GenericRefNames.this_.customName)}"
                if (text1 == GenericRefNames.this_.customName) {
                    PsiClass psiClass33 = firstArgumentType.resolve()
                    if (psiClass33 == null) {
                        log.debug "bad this ref "
                        return null
                    }
                    completionBean.onObjectClass = psiClass33;
                    completionBean.onObjectStatic = false;
                } else {
                    if (onObject.sameNameVariants?.length != 1) {
                        log.debug "bad PsiImmediateClassType"
                        return null
                    }

                    PsiElement psiElement33 = onObject.sameNameVariants[0].element
                    if (psiElement33 instanceof PsiClass) {
                        completionBean.onObjectClass = (PsiClass) psiElement33;
                        completionBean.onObjectStatic = true;
                    } else if (psiElement33 instanceof PsiMethod) {
                        PsiMethod psiMethod = (PsiMethod) psiElement33
                        if (!(psiMethod.getName() == 'getClass')) {
                            log.debug "bad not class  2 ${psiElement33}"
                            return null
                        }
                        PsiElement element99 = onObject.children[0]
                        if (!(element99 instanceof GrReferenceExpression)) {
                            log.debug "bad not class "
                            return null
                        }
                        PsiType type = element99.getType()
                        if (!(type instanceof GrClassReferenceType)) {
                            log.debug "bad not class "
                            return null
                        }
                        completionBean.onObjectClass = type.resolve();
                        completionBean.onObjectStatic = true;
                    } else {
                        log.debug "bad not class ${psiElement33}"
                        return null
                    }
                }
            } else if (firstArgumentType instanceof GrClassReferenceType) {
//                log.info "gr ref"
                GrClassReferenceType referenceType = (GrClassReferenceType) firstArgumentType;
                String qualifiedName1 = referenceType.getCanonicalText()
                if (qualifiedName1 == net.sf.jremoterun.utilities.classpath.ClRef.getName()) {
                    completionBean.onObjectClass = resolveClRef3(onObject)
//                    log.info "rc ref resolved ? ${completionBean.onObjectClass != null}"
                } else {
                    completionBean.onObjectClass = referenceType.resolve();
                    completionBean.onObjectStatic = false;
                }
            } else if (firstArgumentType instanceof PsiClassReferenceType) {
//                log.info "gr ref type"
                PsiClassReferenceType referenceType = (PsiClassReferenceType) firstArgumentType;
                PsiClass resolve1 = referenceType.resolve()

                completionBean.onObjectClass = resolve1;
                completionBean.onObjectStatic = false;

            } else {
                log.debug "bad type ${firstArgumentType}"
                return null

            }
            //12312
        } else if (firstEl instanceof GrTypeCastExpression) {
            GrTypeCastExpression referenceType = (GrTypeCastExpression) firstEl;
            if (!(referenceType.type instanceof PsiClassType)) {
                log.debug "bad"
            }
            PsiClassType classType = (PsiClassType) referenceType.type
            completionBean.onObjectClass = classType.resolve();
            completionBean.onObjectStatic = false;
        } else if (firstEl instanceof GrSafeCastExpression) {
            GrSafeCastExpression referenceType = (GrSafeCastExpression) firstEl;
            if (!(referenceType.type instanceof PsiClassType)) {
                log.debug "bad"
            }
            PsiClassType classType = (PsiClassType) referenceType.type
            completionBean.onObjectClass = classType.resolve();
            completionBean.onObjectStatic = false;

        } else {
            log.debug "bad first arg ${firstEl}"
            return null;
        }

        if (completionBean.onObjectClass == null) {
            log.debug "on object class is null"
            return null
        }
        PsiElement parent2 = args.getParent()
        if (parent2 instanceof GrNewExpression) {
            GrNewExpression grNew3 = parent2;
            GrCodeReferenceElement element2 = grNew3.getReferenceElement()
            String text4 = element2.getText()
            if (text4.contains(MethodRef.getSimpleName())) {
                completionBean.methodName = StaticFieldType.newMethodRef
            } else if (text4.contains(FieldRef.getSimpleName())) {
                completionBean.methodName = StaticFieldType.newFieldRef
            } else {
                log.debug "starnge text ${text4}"
                return null
            }
        } else {
            if (!(parent2 instanceof GrMethodCall)) {
                log.debug "not gr call1"
                return null;
            }
            //GrMethodCall methodCall = (GrMethodCall) parent2;
            PsiElement child = parent2.getChildren()[0];
            if (!(child instanceof GrReferenceExpression)) {
                log.debug "not gr ref ${child}"
                return null;
            }

            GrReferenceExpression referenceExpression = (GrReferenceExpression) child;

            String text1 = referenceExpression.text
            if (text1?.contains(MethodRef.getSimpleName())) {
                completionBean.methodName = StaticFieldType.newMethodRef
            } else if (text1?.contains(FieldRef.getSimpleName())) {
                completionBean.methodName = StaticFieldType.newFieldRef
            } else {
                if (!(text1?.contains('JrrClassUtils.'))) {
                    log.debug "not JrrClassUtils"
                    return null;
                }
                completionBean.methodName = StaticFieldType.valueOf(referenceExpression.getReferenceName());
            }
        }
        return completionBean;

    }


    static PsiClass resolveClRef3(GrReferenceExpression onObject) {
        GroovyResolveResult[] variants1 = onObject.getSameNameVariants()
        if (variants1 == null || variants1.size() != 1) {
            log.debug "bad ClRef"
            return null
        }
        GroovyResolveResult result1 = variants1[0]
        com.intellij.psi.ResolveResult elr = result1 as com.intellij.psi.ResolveResult;
        PsiElement element1 = elr.getElement()
        if (element1 instanceof GrField) {
            GrField field1 = element1 as GrField
            GrExpression groovyInit = field1.getInitializerGroovy()
            if (!(groovyInit instanceof GrNewExpressionImpl)) {
                log.debug "no groovy init for grField ${groovyInit}"
                return null
            }
            GrNewExpressionImpl grNewExpression = (GrNewExpressionImpl) groovyInit;
            PsiClass psiClass1 = resolveClRef(grNewExpression)
            if (psiClass1 != null) {
                return psiClass1
            }


            PsiClass containingClass = field1.getContainingClass()
            String name = field1.getName()
            String cassName = containingClass.getQualifiedName()
            boolean canResolve = FieldResolvedDirectly.fieldResolvedDirectly.canResolveAny(cassName, name)
            if (canResolve) {
                ClRefRef rr = FieldResolvedDirectly.fieldResolvedDirectly.resolveValue2(cassName, name) as ClRefRef
                return OSIntegrationIdea.osIntegrationIdea.findClass(rr.getClRef().className)
            }
            log.debug "failed resolve ${cassName}  ${name}"
            return null
        }
        if (element1 instanceof GrVariable) {
            GrVariable grVariable = (GrVariable) element1;
            GrExpression groovyInit = grVariable.getInitializerGroovy()
            if (!(groovyInit instanceof GrNewExpressionImpl)) {
                log.debug "no groovy init for GrVariable ${groovyInit}"
                return null
            }
            GrNewExpressionImpl grNewExpression = (GrNewExpressionImpl) groovyInit;
            PsiClass psiClass1 = resolveClRef(grNewExpression)
            if (psiClass1 != null) {
                return psiClass1
            }
            log.debug "failed resolve grVar"
            return null
        }
        if (element1 instanceof PsiField) {
            PsiClass containingClass = element1.getContainingClass()
            String name = element1.getName()
            String cassName = containingClass.getQualifiedName()
            boolean canResolve = FieldResolvedDirectly.fieldResolvedDirectly.canResolveAny(cassName, name)
            if (canResolve) {
                ClRefRef rr = FieldResolvedDirectly.fieldResolvedDirectly.resolveValue2(cassName, name) as ClRefRef
                return OSIntegrationIdea.osIntegrationIdea.findClass(rr.getClRef().className)
            }
            log.debug "failed resolve ${cassName}  ${name}"
            return null
        }
        return null
    }

    static PsiClass resolveClRef(GrNewExpressionImpl grNewExpression) {
        GrArgumentList argumentList = grNewExpression.getArgumentList()
        GroovyPsiElement[] allArguments = argumentList.getAllArguments()
        if (allArguments.length != 1) {
            log.debug "length != 1 : ${allArguments.length}"
            return null
        }
        GroovyPsiElement arg1 = allArguments[0]
        if (arg1 instanceof GrLiteral) {
            GrLiteral grLiteral = (GrLiteral) arg1;
            String clName1 = grLiteral.getValue() as String
            return OSIntegrationIdea.osIntegrationIdea.findClass(clName1)
        }
        if ((arg1 instanceof GrReferenceExpression)) {
//            log.info "gr expression"
        GrReferenceExpression onObject = (GrReferenceExpression) arg1;
        PsiType firstArgumentType = onObject.getType()
        if (firstArgumentType instanceof PsiImmediateClassType) {
            String text1 = onObject.getText()
                if (onObject.sameNameVariants?.length != 1) {
                    log.debug "bad PsiImmediateClassType"
                    return null
                }

                PsiElement psiElement33 = onObject.sameNameVariants[0].element
                if (psiElement33 instanceof PsiClass) {
                    return  (PsiClass) psiElement33;
                } else if (psiElement33 instanceof PsiMethod) {
                    PsiMethod psiMethod = (PsiMethod) psiElement33
                    if (!(psiMethod.getName() == 'getClass')) {
                        log.debug "bad not class  2 ${psiElement33}"
                        return null
                    }
                    PsiElement element99 = onObject.children[0]
                    if (!(element99 instanceof GrReferenceExpression)) {
                        log.debug "bad not class "
                        return null
                    }
                    PsiType type = element99.getType()
                    if (!(type instanceof GrClassReferenceType)) {
                        log.debug "bad not class "
                        return null
                    }
                    return type.resolve();
                } else {
                    log.debug "bad not class ${psiElement33}"
                    return null
                }
            }
        }
        log.debug "not GrLiteral ${arg1}"
        return null
    }

    @Override
    public boolean accepts(@Nullable Object o, ProcessingContext context) {
        return accepts(o);
    }

    @Override
    public ElementPatternCondition<PsiElement> getCondition() {
        log.debug(1)
        return new ElementPatternCondition(null);
    }

    private void testNotUsed() {
        Socket testVar = null;
        ClRef clRef1 = new ClRef('java.net.Socket')
        JrrClassUtils.getFieldValue(testVar, "") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(testVar.getChannel().keyFor(null), "attachment") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(ManagementFactory.properties as HashMap, "size") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(Class, "useCaches") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(testVar, "bound") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(clRef1, "bound") //NOFIELDCHECK
        JrrClassUtils.findField(clRef1, "bound") //NOFIELDCHECK
        JrrClassUtils.findField(new ClRef('java.net.Socket'), "bound") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(net.sf.jremoterun.utilities.nonjdk.idea.IdeaRedefineClassloaderSupport.ivyDepResolver, "log") //NOFIELDCHECK
        JrrClassUtils.getFieldValue(net.sf.jremoterun.utilities.nonjdk.idea.IdeaRedefineClassloaderSupport.redefineIdeaClassLoader, "ideaPluginId") //NOFIELDCHECK
        // why doesn't work ?
        JrrClassUtils.getFieldValue(java.awt.Font.DIALOG,'hash'); //NOFIELDCHECK
        // justRandomComment
        JrrClassUtils.getFieldValue(this, "log") //NOFIELDCHECK
        JrrClassUtils.getFieldValueR(clRef1, this, "bound") //NOFIELDCHECK
        JrrClassUtils.findField(this.getClass(), "log") //NOFIELDCHECK
        JrrClassUtils.findField(clRef1, "bound") //NOFIELDCHECK
        //JrrClassUtils.findMethod(testVar, "connect",2) //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(clRef1, "close") //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(testVar, "close") //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(testVar, "connect", null) //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethodR(clRef1, this, "connect", null) //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(clRef1, "connect", null) //NOFIELDCHECK
        JrrClassUtils.invokeJavaMethod(new ClRef('java.net.Socket'), "connect", null) //NOFIELDCHECK


        new FieldRef(testVar.getClass(), "bound"); //NOFIELDCHECK
        new FieldRef(clRef1, "bound"); //NOFIELDCHECK
        new MethodRef(testVar.getClass(), "close", 0) //NOFIELDCHECK
        new MethodRef(testVar.getClass(), "connect", 1) //NOFIELDCHECK
        new MethodRef(clRef1, "close", 0) //NOFIELDCHECK
    }


}
