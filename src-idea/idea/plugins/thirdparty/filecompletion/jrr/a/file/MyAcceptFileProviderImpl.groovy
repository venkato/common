package idea.plugins.thirdparty.filecompletion.jrr.a.file


import com.intellij.patterns.ElementPattern
import com.intellij.patterns.ElementPatternCondition
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiCompiledElement
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiExpression
import com.intellij.psi.PsiField
import com.intellij.psi.PsiIdentifier
import com.intellij.psi.PsiJavaToken
import com.intellij.psi.PsiLiteral
import com.intellij.psi.PsiLiteralExpression
import com.intellij.psi.PsiNewExpression
import com.intellij.psi.PsiReferenceExpression
import com.intellij.psi.PsiType
import com.intellij.psi.impl.source.tree.LeafPsiElement
import com.intellij.psi.impl.source.tree.java.PsiJavaTokenImpl
import com.intellij.util.ProcessingContext
import groovy.transform.CompileStatic
import idea.plugins.thirdparty.filecompletion.jrr.IdeaMagic
import idea.plugins.thirdparty.filecompletion.jrr.a.remoterun.JrrIdeaBean
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.FieldResolvedDirectly
import idea.plugins.thirdparty.filecompletion.jrr.librayconfigurator.FieldResolvedDirectlyMoreComplex
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.CustomObjectHandler
import net.sf.jremoterun.utilities.classpath.MavenDefaultSettings
import net.sf.jremoterun.utilities.nonjdk.TwoResult
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.ChildFileLazy
import net.sf.jremoterun.utilities.nonjdk.classpath.helpers.FileToFileRef
import net.sf.jremoterun.utilities.nonjdk.git.GitSpec
import net.sf.jremoterun.utilities.nonjdk.git.SvnSpec
import org.jetbrains.annotations.Nullable
import org.jetbrains.plugins.groovy.lang.psi.GroovyPsiElement
import org.jetbrains.plugins.groovy.lang.psi.api.GroovyResolveResult
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrField
import org.jetbrains.plugins.groovy.lang.psi.api.statements.GrVariable
import org.jetbrains.plugins.groovy.lang.psi.api.statements.arguments.GrArgumentList
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrNewExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrReferenceExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.GrSafeCastExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.literals.GrLiteral
import org.jetbrains.plugins.groovy.lang.psi.api.statements.expressions.path.GrMethodCallExpression
import org.jetbrains.plugins.groovy.lang.psi.api.statements.typedef.members.GrAccessorMethod
import org.jetbrains.plugins.groovy.lang.psi.impl.GrClassReferenceType
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrNewExpressionImpl
import org.jetbrains.plugins.groovy.lang.psi.impl.statements.expressions.GrReferenceExpressionImpl

@CompileStatic
public class MyAcceptFileProviderImpl implements ElementPattern<PsiElement> {

    private static final java.util.logging.Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    boolean accepts(Object o) {
        log.debug " accept : ${o?.class.name}"
        if (o instanceof LeafPsiElement) {
            JrrIdeaBean.bean.psiElement2 = (PsiElement) o;
            boolean accept = isOkJavaAndGroovyPsiElement(o) != null;
            return accept;
        }
        return false;
    }

    static ChildFileLazy isOkJavaPsiElement3(PsiJavaToken leafPsiElement) {
        PsiElement parent = leafPsiElement.parent.parent.parent
        if (!(parent instanceof PsiNewExpression)) {
            log.debug("not new expression")
            return null;
        }
        log.debug "${parent}"
        return javaFileViaNewExpression7(parent, true);
    }

    static FileCompletionBean isOkJavaPsiElement(PsiJavaToken leafPsiElement, boolean addSuffix) {
        PsiElement parent = leafPsiElement.parent.parent.parent
        if (!(parent instanceof PsiNewExpression)) {
            log.debug("not new expression")
            return null;
        }
//        PsiNewExpression newExpression = (PsiNewExpression) parent
        log.debug "${parent}"
        PsiNewExpression pn = parent
        TwoResult<Boolean, File> res3 = javaFileViaNewExpression3(parent, addSuffix);
        if (res3 == null) {
            return null
        }
        PsiExpression[] argsExpressions = parent.getArgumentList().expressions
        PsiExpression element = argsExpressions.toList().last()
        if (!(element instanceof PsiLiteral)) {
            log.debug "not a psi literal"
            return null;
        }
        String value2 = getStringFrom2PsiLiteral2(element);
        if (value2 == null) {
            log.debug "not a string"
            return null;
        }
        FileCompletionBean fileCompletionBean = new FileCompletionBean()
        fileCompletionBean.wholeFileDeclaration = parent
        fileCompletionBean.value = value2
        fileCompletionBean.parentFilePath = res3.second
        return fileCompletionBean

    }

    static FileCompletionBean isOkJavaAndGroovyPsiElement(LeafPsiElement leafPsiElement) {
        if (leafPsiElement instanceof PsiJavaTokenImpl) {
            return isOkJavaPsiElement(leafPsiElement, false)
        }
        PsiElement parent = leafPsiElement.getParent();
        if (!(parent instanceof PsiLiteral)) {
            return null;
        }
        PsiLiteral psiLiteral789 = parent;
        Object value = psiLiteral789.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        FileCompletionBean completionBean = new FileCompletionBean();
        completionBean.value = getStringFromPsiLiteral(psiLiteral789)
        if (completionBean.value == null) {
            return null;
        }
        // log.debug "value = ${completionBean.value}"
        completionBean.literalElemtnt = psiLiteral789;
        PsiElement parent1 = psiLiteral789.parent;
        PsiElement parent2 = parent1.parent
        if (parent1 instanceof GrArgumentList) {
            if (parent2 instanceof GrNewExpression) {
                TwoResult<Boolean, File> res = fileViaGrNewExpression(parent2)
                if (res != null && res.first) {
                    completionBean.wholeFileDeclaration = parent2
                    completionBean.parentFilePath = res.second
                    return completionBean
                }
                return null
            }
        }
        if (parent1 instanceof GrSafeCastExpression) {
            File file = resolveFileFromSafeCast(parent1)
            if (file != null) {
                completionBean.wholeFileDeclaration = parent1
                return completionBean
            }

        }

        if (parent2 instanceof GrMethodCallExpression) {
            ChildFileLazy file = fileViaFileChildMethod(parent2, false)
            if (file != null) {
                CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
                if (handler == null) {
                    throw new IllegalStateException("customObjectHandler was not set")
                }
                File f3341 = handler.resolveToFileIfDownloaded(file)
                if (f3341 == null) {
                    log.info "seems not download ${file} ${file.getClass().getName()} "
                    return null
                }
                completionBean.wholeFileDeclaration = parent1
                completionBean.parentFilePath = f3341
                return completionBean
            }

        }

        return null;
    }

    /**
     * Calculate path for var.
     * Example : File parent = new File('/opt');
     * use parent somewhere. This method calc path for parent var
     * @see #childLazyMethodResolveParent
     */
    static ChildFileLazy findFileFromVarGeneric(final PsiElement varRef) {
        if (varRef == null) {
            log.warn("ref is null", new Exception())
            return null
        }
        if (varRef instanceof GrReferenceExpression) {
            // may be use resolve ?
            // varRef.resolve()
            ChildFileLazy result = FieldResolvedDirectlyMoreComplex.fieldResolvedDirectlyMoreComplex.tryResolveFileViaGrRef(varRef)
            if (result != null) {
                return result;
            }
            GroovyResolveResult[] variants = varRef.getSameNameVariants()
            if (variants == null) {
                log.debug "variants is null"
                return null
            }
            if (variants.length != 1) {
                log.debug "args not 1"
                return null
            }
            PsiElement element = variants[0].element
            if (element == null) {
                log.debug "failed resolve el var for ${varRef}"
                return null
            }
            if (element == varRef) {
                log.info "cycle ${varRef}"
                return null
            }
            return findFileFromVarGeneric(element)
        }
        if (varRef instanceof PsiReferenceExpression) {
            ChildFileLazy result = FieldResolvedDirectlyMoreComplex.fieldResolvedDirectlyMoreComplex.tryResolveFileViaJavaRef(varRef)
            if (result != null) {
                return result;
            }
            PsiElement resolve = varRef.resolve()
            if (resolve == null) {
                log.info "failed resolve ${varRef}"
                return null
            }
            if (resolve == varRef) {
                log.info "cycle : ${varRef}"
                return null
            }
            return findFileFromVarGeneric(resolve)
        }
        if (varRef instanceof GrField) {
            ChildFileLazy result = FieldResolvedDirectlyMoreComplex.fieldResolvedDirectlyMoreComplex.tryResolveFile(varRef)
            if (result != null) {
                return result
            }
            GrExpression gr1 = varRef.getInitializerGroovy()
            if (gr1 == null) {
                log.debug "null init for ${varRef}"
                return null
            }
            return findFileFromVarGeneric(gr1)
        }
        if (varRef instanceof PsiField) {
            PsiClass containingClass = varRef.getContainingClass()
            String name = varRef.getName()
            String cassName = containingClass.getQualifiedName()
            boolean canResolve = FieldResolvedDirectly.fieldResolvedDirectly.canResolveAny(cassName, name)
            if (canResolve) {
                return FieldResolvedDirectly.fieldResolvedDirectly.resolveValue3(cassName, name)
            }
            return javaFindFileFromField(varRef)
        }
        if (varRef instanceof GrVariable) {
            GrExpression gr1 = varRef.getInitializerGroovy()
            if (gr1 == null) {
                log.debug "null init for ${varRef}"
                return null
            }
            return findFileFromVarGeneric(gr1)
        }
        if (varRef instanceof GrAccessorMethod) {
            PsiElement navigationElement = varRef.getNavigationElement()
            if (navigationElement == null) {
                log.info "navigation el is null"
            } else {
                if (navigationElement != varRef) {
                    return findFileFromVarGeneric(navigationElement)
                }
            }
        }

        // new begin
        if (varRef instanceof GrSafeCastExpression) {
            File f77 = resolveFileFromSafeCast(varRef)
            if (f77 != null) {
                return new FileToFileRef(f77)
            }
        }
        if (varRef instanceof GrNewExpression) {
            return fileViaGroovyNewExpression3(varRef, true);
        }
        if (varRef instanceof GrMethodCallExpression) {
            return fileViaFileChildMethod(varRef, true);
        }
        if (varRef instanceof PsiNewExpression) {
            javaFileViaNewExpression7(varRef, true)
        }
        // new end

        log.debug "not a GrVar : ${varRef.class} ${varRef}"
        return null;
    }


    /**
     * @see #findFileFromVarGeneric
     */
    private static ChildFileLazy childLazyMethodResolveParent(PsiElement firstChild) {
        if (firstChild instanceof GrNewExpressionImpl) {
            PsiType type = firstChild.getType();
            if (!(type instanceof GrClassReferenceType)) {
                return null
            }
            PsiClass resolve = type.resolve()

            if (resolve == null) {
                log.info "not  git spec 1 ${type}"
                return null
            }
            boolean isSvnSpec = false;
            if (!(resolve.getName().contains(GitSpec.getSimpleName()))) {
                log.info("not  git spec: ${resolve}")
                if (resolve.getName().contains(SvnSpec.getSimpleName())) {
                    isSvnSpec = true
                } else {
                    return null
                }
            }
            GrArgumentList args = firstChild.getArgumentList()
            GroovyPsiElement[] arguments = args.getAllArguments()
            if (arguments == null || arguments.length != 1) {
                log.info "bad args : ${arguments}"
                return null
            }
            GroovyPsiElement firstArg = arguments[0]
            if (firstArg instanceof GrLiteral) {
                GrLiteral literal = (GrLiteral) firstArg;
                String gitRefValue = literal.getValue()
                ChildFileLazy gitSpec;
                if (isSvnSpec) {
                    return new SvnSpec(gitRefValue)
                }
                return new GitSpec(gitRefValue)
            }
        }
        if (firstChild instanceof GrMethodCallExpression) {
            if (firstChild instanceof GrMethodCallExpression) {
                GrMethodCallExpression ii = (GrMethodCallExpression) firstChild;
                GrExpression grExpression = ii.invokedExpression
                if (grExpression instanceof GrReferenceExpressionImpl) {
                    GrReferenceExpressionImpl invokedExpression = (GrReferenceExpressionImpl) grExpression;
                    String methodName = invokedExpression.getReferenceName()
                    if (methodName == SpecialMethodName.getRedirect.name()) {
                        return getRedirectMethod(ii)
                    }
                    if (methodName == SpecialMethodName.childL.name()) {
                        ChildFileLazy childFileLazy2 = childLazyMethod(firstChild, true)
                        return childFileLazy2;
                    }
                    log.info "strange method ${methodName}"
                    return null
                }
            }
            log.info "not GrRef2 ${firstChild.getClass()} ${firstChild}"
            return null
        }
        if (!(firstChild instanceof GrReferenceExpression)) {
            log.info "not GrRef ${firstChild.getClass()} ${firstChild}"
            return null

        }
        ChildFileLazy result = FieldResolvedDirectlyMoreComplex.fieldResolvedDirectlyMoreComplex.tryResolveFileViaGrRef(firstChild)
        if (result != null) {
            return result;
        }

        log.info "good ref ${firstChild}"
        PsiElement child2 = firstChild.getFirstChild()
        if (!(child2 instanceof GrReferenceExpression)) {
            log.info "firstChild no psi : ${child2}"
            return null
        }

        PsiElement psiField = firstChild.resolve()
        if (!(psiField instanceof PsiField)) {
            log.info("not psi field : ${psiField}")
            return null
        }
        PsiField psiVar = psiField as PsiField
        PsiClass containingClass = psiVar.getContainingClass()
        String className1 = containingClass.getQualifiedName();
        PsiIdentifier pi = psiVar.getNameIdentifier();
        String fieldName = pi.getText()
        boolean canResolveB = FieldResolvedDirectly.fieldResolvedDirectly.canResolveAny(className1, fieldName)
        if (!canResolveB) {
            log.info "not in scope : ${className1}"
            return null
        }
        return FieldResolvedDirectly.fieldResolvedDirectly.resolveValue3(className1, fieldName)
    }

    private static String getStringFrom2PsiLiteral2(PsiLiteral literalElemtnt) {
        Object value = literalElemtnt.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        return value.replace(IdeaMagic.addedConstant, '');
    }

    static String getStringFromPsiLiteral(PsiElement psiElement) {
        if (!(psiElement instanceof PsiLiteral)) {
            return null;
        }
        PsiLiteral literalElemtnt = (PsiLiteral) psiElement;
        return getStringFrom2PsiLiteral2(literalElemtnt)
    }


    private static File resolveFileFromSafeCast(GrSafeCastExpression castExpression) {
        String text = castExpression.type.presentableText
        if (text != null && text.contains('File')) {
            log.debug("accpted")
            GrExpression operand1 = castExpression.operand
            if (!(operand1 instanceof PsiLiteral)) {
                log.debug "not psi literal"
                return null;
            }
            String literal = getStringFrom2PsiLiteral2(operand1)
            if (literal == null) {
                log.debug "not a tsring"
                return null;
            }
            return new File(literal);
        }
        return null
    }

    /**
     * Internal method
     */
    private
    static TwoResult<Boolean, File> fileViaGrNewExpression(GrNewExpression grExpression) {
        PsiType type = grExpression.getType();
        if (!(type instanceof GrClassReferenceType)) {
            return null
        }
        PsiClass resolve = type.resolve()

        if (resolve == null || !(resolve.getName().contains('File'))) {
            log.debug("accpted")
            return null
        }
        if (grExpression.argumentList.allArguments.length == 1) {
            GroovyPsiElement psiElement = grExpression.argumentList.allArguments[0]

            if (!(psiElement instanceof PsiLiteral)) {
                log.debug "not psi literal"
                return null;
            }
            String literal
            literal = getStringFrom2PsiLiteral2(psiElement)

            if (literal == null) {
                log.debug "not a string"
                return null
            }
            return new TwoResult<Boolean, File>(true, null);
        }
        if (grExpression.argumentList.allArguments.length == 2) {
            ChildFileLazy parentFilePath = fileViaGroovyNewExpression3(grExpression, false)
            if (parentFilePath == null) {
                return null
            }
            CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
            if (handler == null) {
                throw new IllegalStateException("customObjectHandler was not set")
            }
            File f3341 = handler.resolveToFileIfDownloaded(parentFilePath)
            if (f3341 == null) {
                log.info "seems not download ${parentFilePath} ${parentFilePath.getClass().getName()} "
                return null
            }
            return new TwoResult<Boolean, File>(true, f3341);
//            return completionBean.parentFilePath != null;
        }
        return null
    }

    /**
     * Resolve java construction new File('path')
     * java construction not supported : new File(parent,'child')
     * but for groovy supported
     * @param grExpression
     * @param addSuffix
     * @return
     */
    private
    static TwoResult<Boolean, File> javaFileViaNewExpression3(PsiNewExpression grExpression, boolean addSuffix) {
        PsiType type = grExpression.type;
        if (!(type instanceof PsiClassType)) {
            log.debug "not a type"
            return null;
        }
        PsiClass psiClass = type.resolve();
        if (psiClass == null || psiClass.name == null || !(psiClass.name.contains('File'))) {
            log.debug "not a file"
            return null;
        }
        PsiExpression[] argsExpressions = grExpression.argumentList.expressions
        if (argsExpressions.length == 1) {
            return new TwoResult<Boolean, File>(true, null);
        }
        if (argsExpressions.length == 2) {
            log.debug "too many args"
            ChildFileLazy fff = javaFileViaNewExpression7(grExpression, addSuffix)
            if (fff == null) {
                return null;
            }
            CustomObjectHandler handler = MavenDefaultSettings.mavenDefaultSettings.customObjectHandler
            if (handler == null) {
                throw new IllegalStateException("customObjectHandler was not set")
            }
            File f3341 = handler.resolveToFileIfDownloaded(fff)
            if (f3341 == null) {
                log.info "seems not download ${fff} ${fff.getClass().getName()} "
                return null
            }
            return new TwoResult<Boolean, File>(true, f3341);


        }
        log.debug "not implemented"
        return null
    }

    private static ChildFileLazy javaFileViaNewExpression7(PsiNewExpression grExpression, boolean addSuffix) {
        PsiType type = grExpression.type;
        if (!(type instanceof PsiClassType)) {
            log.debug "not a type"
            return null;
        }
        PsiClass psiClass = type.resolve();
        if (psiClass == null || psiClass.name == null || !(psiClass.name.contains('File'))) {
            log.debug "not a file"
            return null;
        }
        PsiExpression[] argsExpressions = grExpression.argumentList.expressions
        if (argsExpressions.length == 1) {
            PsiExpression childEl = argsExpressions[0]
            if (!(childEl instanceof PsiLiteralExpression)) {
                log.info "not psi literal : ${childEl}"
                return null
            }
//            if (childEl instanceof PsiLiteral) {
            String value2 = getStringFrom2PsiLiteral2(childEl);
            if (value2 == null) {
                log.debug "not a string"
                return null;
            }
            return new FileToFileRef(new File(value2))
        }
        if (argsExpressions.length != 2) {
            log.debug "too many args"
            return null;
        }
        PsiExpression childEl = argsExpressions[1]
        if (!(childEl instanceof PsiLiteralExpression)) {
            log.info "not psi literal : ${childEl}"
            return null
        }
        ChildFileLazy parentFile = findFileFromVarGeneric(argsExpressions[0])
        if (parentFile == null) {
            return null
        }
        if (!addSuffix) {
            return parentFile
        }
        if (!(childEl instanceof PsiLiteral)) {
            log.debug "not a psi literal"
            return null;
        }
        String value2 = getStringFrom2PsiLiteral2(childEl);
        if (value2 == null) {
            log.debug "not a string"
            return null;
        }
        return parentFile.childL(value2)
    }

    private static File javaFileViaNewExpression2(PsiNewExpression grExpression) {
        PsiType type = grExpression.type;
        if (!(type instanceof PsiClassType)) {
            log.debug "not a type"
            return null;
        }
        PsiClass psiClass = type.resolve();
        if (psiClass == null || psiClass.name == null || !(psiClass.name.contains('File'))) {
            log.debug "not a file"
            return null;
        }
        if (grExpression.argumentList.expressions.length == 1) {
            PsiExpression element = grExpression.argumentList.expressions[0]

            if (!(element instanceof PsiLiteral)) {
                log.debug "not a psi literal"
                return null;
            }
            String value2 = getStringFrom2PsiLiteral2(element);
            if (value2 == null) {
                log.debug "not a string"
                return null;
            }
            return new File(value2);
        }
        if (grExpression.argumentList.expressions.length != 2) {
            log.debug "too many args"
            return null;
        }
        log.debug "not implemented"
        return null
    }

    /**
     * Resolve groovy construction new File('path') and new File(parent,'path')
     * @param grExpression
     * @param addSuffix indecatios if need resolve child path for 2 args constructor
     * @return
     */
    private static ChildFileLazy fileViaGroovyNewExpression3(GrNewExpression grExpression, boolean addSuffix) {
        PsiType type = grExpression.type;
        if (!(type instanceof GrClassReferenceType)) {
            log.debug "not a type"
            return null;
        }
        PsiClass resolve = type.resolve()

        if (resolve == null || !(resolve.name.contains('File'))) {
            log.debug "not a file"
            return null;
        }
        if (grExpression.argumentList.allArguments.length == 1) {
            GroovyPsiElement element = grExpression.argumentList.allArguments[0]

            if (!(element instanceof PsiLiteral)) {
                log.debug "not a psi literal"
                return null;
            }
            String value2 = getStringFrom2PsiLiteral2(element);
            if (value2 == null) {
                log.debug "not a string"
                return null;
            }
            return new FileToFileRef(new File(value2));
        }
        if (grExpression.argumentList.allArguments.length != 2) {
            log.debug "too many args"
            return null;
        }
        GroovyPsiElement arg = grExpression.argumentList.allArguments[0]
        ChildFileLazy fileParent = findFileFromVarGeneric(arg);
        if (fileParent == null) {
            return null
        }
        if (!addSuffix) {
            return fileParent
        }
        GroovyPsiElement element44 = grExpression.argumentList.allArguments[1]
        if (!(element44 instanceof PsiLiteral)) {
            log.debug "not a psi literal"
            return null;
        }
        String suffixFile = getStringFrom2PsiLiteral2(element44);
        if (suffixFile == null) {
            return null
        }
        return fileParent.childL(suffixFile)
    }

    /**
     * Calculate path for field.
     * Example : File parent = new File('/opt');
     * use parent somewhere. This method calc path for parent field
     */
    private static ChildFileLazy javaFindFileFromField(final PsiField psiField) {
        PsiField psiField2 = psiField
        if (psiField2.navigationElement != null && psiField2.navigationElement instanceof PsiField) {
            log.debug "use navigation el"
            psiField2 = psiField2.navigationElement as PsiField
        }
        PsiExpression initializer = psiField2.initializer
        if (initializer instanceof PsiNewExpression) {
            File fileResolved = javaFileViaNewExpression2(initializer);
            return new FileToFileRef(fileResolved);
        }
        if (initializer == null) {
            if (!(psiField2 instanceof PsiCompiledElement)) {
                log.debug "not a psi compile"
                return null;
            }
            PsiElement mirror = psiField2.getMirror();
            if (!(mirror instanceof PsiField)) {
                log.debug "not a field"
                return null;
            }
            psiField2 = mirror;
        } else {
            log.info "not imeplementted initializer : ${initializer.class.name} ${initializer}"
            return null
        }
        if (psiField2 instanceof GrField) {
            return findFileFromVarGeneric(psiField2.getInitializerGroovy())
        }
        if (psiField2 != psiField) {
            return javaFindFileFromField(psiField2)
        }
        log.debug "not gr field ${psiField2?.class.name} ${psiField2}"

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


    private static ChildFileLazy resolveToFileMethod(GrMethodCallExpression grExpression) {
        GrReferenceExpressionImpl invokedExpression = grExpression.invokedExpression as GrReferenceExpressionImpl
        String methodName = invokedExpression.getReferenceName()
        assert methodName == SpecialMethodName.resolveToFile.name()
        PsiElement firstChild = invokedExpression.getFirstChild()
        int size = grExpression.getArgumentList().getAllArguments().size()
        if(size!=0){
            log.info "args count ${size} !=0 "
            return null
        }
        return findFileFromVarGeneric(firstChild)
    }

    private static ChildFileLazy getRedirectMethod(GrMethodCallExpression grExpression) {
        GrReferenceExpressionImpl invokedExpression = grExpression.invokedExpression as GrReferenceExpressionImpl
        String methodName = invokedExpression.getReferenceName()
        assert methodName == SpecialMethodName.getRedirect.name()
        int size = grExpression.getArgumentList().getAllArguments().size()
        if(size!=0){
            log.info "args count ${size} !=0 "
            return null
        }
        PsiElement firstChild = invokedExpression.getFirstChild()
        return findFileFromVarGeneric(firstChild)
    }


    private static ChildFileLazy childLazyMethod(GrMethodCallExpression grExpression, boolean addSuffix) {
        GrReferenceExpressionImpl invokedExpression = grExpression.invokedExpression as GrReferenceExpressionImpl
        String methodName = invokedExpression.getReferenceName()
        assert methodName == SpecialMethodName.childL.name()
        GroovyPsiElement[] arguments = grExpression.argumentList.allArguments
        if(arguments.length!=1){
            log.info("arg count ${arguments.length} !=1")
            return null
        }
        PsiElement firstChild = invokedExpression.getFirstChild()
        ChildFileLazy res1 = childLazyMethodResolveParent(firstChild);
        if (res1 == null) {
            return null
        }
        if (!addSuffix) {
            return res1
        }

        GroovyPsiElement element44 = arguments[0];
        if (!(element44 instanceof PsiLiteral)) {
            log.debug("not psi literal")
            return null
        }
        String suffixFile = getStringFrom2PsiLiteral2(element44);
        if (suffixFile == null) {
            return null
        }
        return res1.childL(suffixFile)
    }

    private
    static ChildFileLazy fileViaFileChildMethod(GrMethodCallExpression grExpression, boolean addSuffix) {

        GrExpression invokedExpression = grExpression.invokedExpression
        if (!(invokedExpression instanceof GrReferenceExpressionImpl)) {
            log.info "not GrReferenceExpressionImpl : ${invokedExpression.class.name} ${invokedExpression}"
            return null
        }
        String methodName = invokedExpression.getReferenceName()
        if (methodName == SpecialMethodName.resolveToFile.name()) {
            return resolveToFileMethod(grExpression)
        }
        if (methodName == SpecialMethodName.getRedirect.name()) {
            return getRedirectMethod(grExpression)
        }
        if (methodName == SpecialMethodName.childL.name()) {
            return childLazyMethod(grExpression, addSuffix)
        }
        if (methodName != SpecialMethodName.child.name()) {
            log.debug "method name is not child : ${methodName}"
            return null
        }
        return childStdMethod(grExpression,addSuffix)
    }

    private static ChildFileLazy childStdMethod(GrMethodCallExpression grExpression, boolean addSuffix){
        GrReferenceExpressionImpl invokedExpression = grExpression.invokedExpression as GrReferenceExpressionImpl
        GroovyPsiElement[] allArgs = grExpression.argumentList.allArguments
        PsiElement[] children = grExpression.children
        String methodName = invokedExpression.getReferenceName()
        assert methodName == SpecialMethodName.child.name()
        if (children.length != 2) {
            log.info "childerns count ${children.length} != 2 "
            return null
        }
        ChildFileLazy parentFile = findFileFromVarGeneric(invokedExpression.getFirstChild())
        if (parentFile == null) {
//            log.debug "parent file not found"
            return null
        }
        if (!addSuffix) {
            return parentFile
        }
        GroovyPsiElement element44 = allArgs[0];
        if (!(element44 instanceof PsiLiteral)) {
            log.debug "not a psi literal"
            return null;
        }
        String suffixFile = getStringFrom2PsiLiteral2(element44);
        if (suffixFile == null) {
            return null
        }
        return parentFile.childL(suffixFile)
    }

    private void notUsed() {
        File f = "c:/1/" as File

    }

}
