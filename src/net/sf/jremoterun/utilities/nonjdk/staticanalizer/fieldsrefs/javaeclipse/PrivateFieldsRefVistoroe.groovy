package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.javaeclipse

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ClassMemberRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.javaparser.OnNewCompilationUnit
import net.sf.jremoterun.utilities.nonjdk.javaparser.eclipse.EclipseJavaParser
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldTypeCollection
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy.ClassType
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy.FieldFoundedEls
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.ClassInstanceCreation
import org.eclipse.jdt.core.dom.CompilationUnit
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.IBinding
import org.eclipse.jdt.core.dom.ITypeBinding
import org.eclipse.jdt.core.dom.IVariableBinding
import org.eclipse.jdt.core.dom.MethodInvocation
import org.eclipse.jdt.core.dom.NumberLiteral
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.StringLiteral
import org.eclipse.jdt.core.dom.Type
import org.eclipse.jdt.core.dom.TypeLiteral;

import java.util.logging.Logger;

@CompileStatic
class PrivateFieldsRefVistoroe extends ASTVisitor implements OnNewCompilationUnit {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public EclipseJavaParser eclipseJavaParser = new EclipseJavaParser(this)

//    @Override
//    boolean preVisit2(ASTNode node) {
//        log.info "previst = ${node.getClass().getName()} ${node}"
//        return super.preVisit2(node)
//    }

//    public boolean visit(ConstructorInvocation node){
//        log.info "const ${node}"
//        super.visit(node)
//    }



    public boolean visit(ClassInstanceCreation node) {
        try {
            return visitConstructor(node)
        }catch (Throwable e){
            log.info "failed in ${currentFile} line = ${resolveLineNumberByOffset(node.getStartPosition())} in ${node}"
            throw e
        }
    }

    public boolean visitConstructor(ClassInstanceCreation node) {
                //log.info "const ${node}"
        Expression expression = node.getExpression()
        Type type1 = node.getType()
        ITypeBinding resolveBinding = type1.resolveBinding()
        if(resolveBinding==null){
            return true
        }
        String qualifiedName = resolveBinding.getQualifiedName()

        if(StaticFieldTypeCollection.javaClassRefNames.contains(qualifiedName)){

            int lineNumberByOffset = resolveLineNumberByOffset(node.getStartPosition())
            String lineContent = getLineConentByNumber(lineNumberByOffset)
            if (isOkLine(lineNumberByOffset)) {
                log.info "${resolveBinding.getName()}  qn = ${qualifiedName}"
                StaticFieldType staticFieldType = StaticFieldType.map1.get(resolveBinding.getName())
                List expressions44 = node.arguments()
                ClRef clRef7 = resolveClRefExpression(expressions44[0])
                Object nestedExpression = expressions44[1]
                log.info "staticFieldType = ${staticFieldType}  clref=${clRef7}"
                if (staticFieldType == StaticFieldType.FieldRef) {
                    log.info "t = ${nestedExpression.getClass().getName()}"
                    if (nestedExpression instanceof org.eclipse.jdt.core.dom.StringLiteral) {
                        log.info "aauu line= ${lineNumberByOffset}"
                        StringLiteral sl = nestedExpression
                        onElementFound(new FieldRef(clRef7, sl.literalValue), lineNumberByOffset)
                    }
                }
                if (staticFieldType == StaticFieldType.MethodRef) {
                    if (nestedExpression instanceof org.eclipse.jdt.core.dom.StringLiteral) {
                        StringLiteral sl = nestedExpression
                        def secondArg = expressions44[2]
                        if (secondArg instanceof org.eclipse.jdt.core.dom.NumberLiteral) {
                            int counnttt = resolveInt(secondArg)
                            onElementFound(new MethodRef(clRef7, sl.literalValue, counnttt), lineNumberByOffset)
                        }
                    }
                }
                if (staticFieldType == StaticFieldType.ConstructorRef) {
                    if (nestedExpression instanceof NumberLiteral) {
                        int counnttt = resolveInt(nestedExpression)
                        onElementFound(new ConstructorRef(clRef7, counnttt), lineNumberByOffset)
                    }
                }
            }
        }
        super.visit(node)
//        return true;
    }

    public boolean visit(MethodInvocation node) {
        try {
            return visitMethod(node)
        }catch (Throwable e){
            log.info "failed in ${currentFile} line = ${resolveLineNumberByOffset(node.getStartPosition())} in ${node}"
            throw e
        }
    }

    boolean visitMethod(MethodInvocation node) {
boolean debugFlag = false
        if(false &&  currentFile.getName() == 'EclipseClassLoaderUtilsJrr.java'){
            log.info "mi ${node}"
            if(node.toString().contains(JrrClassUtils.getSimpleName())) {
                debugFlag = true
            }
//            return super.visit(node)
        }
        List expressions1 = node.arguments()
        Expression expression1 = node.getExpression()
        if(debugFlag){
            log.info "cp1"
        }
        if (expression1 instanceof SimpleName) {
            if(debugFlag){
                log.info "cp2"
            }
            SimpleName simpleName = (SimpleName) expression1;
            IBinding binding1 = simpleName.resolveBinding()
            //log.info "binding1 = ${binding1.getClass().getName()}"
            if (binding1 instanceof org.eclipse.jdt.core.dom.ITypeBinding) {
                if(debugFlag){
                    log.info "cp3"
                }
                org.eclipse.jdt.core.dom.ITypeBinding typeBinding = (org.eclipse.jdt.core.dom.ITypeBinding) binding1;
                //log.info "name = ${typeBinding.getName()}  ${typeBinding.getQualifiedName()}"
                ClassType type1 = isCheckType(typeBinding)
                if (type1 == null) {
                    if(debugFlag){
                        log.info "type is null"
                    }

                }else{
                    if(debugFlag){
                        log.info "cp4"
                    }
                    SimpleName simpleName1 = node.getName()
                    //log.info "nodeName = ${simpleName1}"
                    StaticFieldType staticFieldType = StaticFieldType.map1.get(simpleName1.getIdentifier())
                    if (staticFieldType == null) {
                        if(debugFlag){
                            log.info "staticFieldType is null"
                        }
                    }else{
                      //  log.info "staticFieldType = ${staticFieldType}"

                        int extraOffset = 0
                        if (staticFieldType.isFirstParamFake() || type1 == ClassType.JrrJavassistUtils) {
                            extraOffset = 1
                        }

                        int lineNumberByOffset = resolveLineNumberByOffset(node.getStartPosition())
                        String lineContent = getLineConentByNumber(lineNumberByOffset)
                        if (isOkLine(lineNumberByOffset)) {
                            if(debugFlag){
                                log.info "cp6"
                            }
                            Object onType = expressions1[0]
                            ClRef expression1Obj = resolveClRefExpression(onType)
                            if(expression1Obj==null){
                                expression1Obj=new ClRef('unkownp1.Unknowncl2')
                            }
                            if (StaticFieldTypeCollection.reFields.contains(staticFieldType) ||
                                    staticFieldType in [StaticFieldType.invokeJavaMethodR, StaticFieldType.invokeJavaMethod, StaticFieldType.invokeJavaMethod2, StaticFieldType.invokeJavaMethod2R, StaticFieldType.findMethodByCount, StaticFieldType.findMethodByCountR, StaticFieldType.findMethodByParamTypes1
                                                        , StaticFieldType.findMethodByParamTypes2, StaticFieldType.findMethodByParamTypes3, StaticFieldType.findMethodByParamTypes4]) {
                                if(debugFlag){
                                    log.info "cp7"
                                }

                                if (expression1Obj == null) {
                                    log.info "failed resolve ${onType} in ${currentFile} line=${lineNumberByOffset}"
                                }else{
                                    Object fieldName = expressions1[extraOffset + 1]
//                                if (staticFieldType in StaticFieldType.reFields) {


                                    if (fieldName instanceof StringLiteral) {
                                        StringLiteral stringLiteral = (StringLiteral) fieldName;

                                        if(debugFlag){
                                            log.info "cp8"
                                        }

//                                        log.info "type = ${expression1Obj} fn=${stringLiteral.literalValue} pos=${node.getStartPosition()} line=${lineNumberByOffset} cont=${lineContent}"
//                                        if (expression1Obj != null) {
//                                            onElementFound(new FieldRef(expression1Obj, stringLiteral.literalValue), lineNumberByOffset)
//                                        }


                                        if (StaticFieldTypeCollection.reFields.contains(staticFieldType)) {
                                            onElementFound(new FieldRef(expression1Obj, stringLiteral.literalValue), lineNumberByOffset)
                                        }
                                        if (staticFieldType in [StaticFieldType.invokeJavaMethod, StaticFieldType.invokeJavaMethodR, StaticFieldType.invokeJavaMethod2R, StaticFieldType.invokeJavaMethod2]) {
                                            int argsCount = expressions1.size() - 2 - extraOffset
                                            assert argsCount >= 0
                                            onElementFound(new MethodRef(expression1Obj, stringLiteral.literalValue, argsCount), lineNumberByOffset)
                                        }
                                        if (staticFieldType in [StaticFieldType.findMethodByParamTypes1, StaticFieldType.findMethodByParamTypes3]) {
                                            int argsCount = expressions1.size() - 2 - extraOffset
                                            assert argsCount >= 0
                                            onElementFound(new MethodRef(expression1Obj, stringLiteral.literalValue, argsCount), lineNumberByOffset)
                                        }
                                        if (staticFieldType in [StaticFieldType.findMethodByCount, StaticFieldType.findMethodByCountR,]) {
                                            Object expressionCounttt = expressions1[2 + extraOffset]

                                            if (expressionCounttt instanceof org.eclipse.jdt.core.dom.NumberLiteral) {
                                                int argsCount = resolveInt(expressionCounttt)
                                                assert argsCount >= 0
                                                onElementFound(new MethodRef(expression1Obj, stringLiteral.literalValue, argsCount), lineNumberByOffset)
                                            }
                                        }


                                    }else {

                                        if(debugFlag){
                                            log.info "not string letral : ${fieldName}"
                                        }
                                    }
                                }
                            }


                            if (StaticFieldTypeCollection.constructorsFunctions.contains(staticFieldType)) {

                                //Object expression1Obj = resolveClRefExpression(expressions1[0])
                                if (staticFieldType == StaticFieldType.findConstructorByCount) {
                                    org.codehaus.groovy.ast.expr.Expression expressionCounttt = expressions1[1 + extraOffset]
                                    if (expressionCounttt instanceof org.eclipse.jdt.core.dom.NumberLiteral) {
                                        int counnttt = resolveInt(expressionCounttt)
                                        onElementFound(new ConstructorRef(expression1Obj, counnttt), lineNumberByOffset)
                                    }
                                } else {
                                    onElementFound(new ConstructorRef(expression1Obj, expressions1.size() - 1 - extraOffset), lineNumberByOffset)
                                }
                            }
                        }else {
                            if(debugFlag){
                                log.info "not ok line"
                            }
                        }

                    }
                }
            }else{
                if(debugFlag) {
                    log.info "not binding ${simpleName}  :: He${binding1}"
                }
            }

        }else{

            if(debugFlag){
                log.info "not simple name"
            }
        }
        return super.visit(node);
    }


    int resolveInt(org.eclipse.jdt.core.dom.NumberLiteral constantExpression) {
        String token1 = constantExpression.getToken()
        log.info "num = ${token1}"
        return token1 as int
    }


    private ClassType isCheckType(org.eclipse.jdt.core.dom.ITypeBinding typeBinding) {
        String name1 = typeBinding.getQualifiedName()
        if (name1 == JrrClassUtils.getName()) {
            return ClassType.JrrClassUtils
        }
        if (name1 == JrrJavassistUtils.getName()) {
            return ClassType.JrrJavassistUtils
        }
        return null
    }

    ClRef resolveClRefExpression(Object type) {
//        log.info "typ99 = ${type.getClass().getName()}"
        if (type instanceof org.eclipse.jdt.core.dom.ClassInstanceCreation) {
            Type type2 = type.getType()
            ITypeBinding binding = type2.resolveBinding()
            if (binding.getQualifiedName() == ClRef.getName()) {
                List arguments = type.arguments()
                if (arguments.size() == 1) {
                    Object object = arguments[0]
                    if (object instanceof StringLiteral) {
                        StringLiteral stringLiteral = (StringLiteral) object;
                        return new ClRef(stringLiteral.getLiteralValue())
                    }
                    if (object instanceof TypeLiteral) {
                        //log.info "args = ${object}"
                        Type type1 = object.getType()
                        ITypeBinding typeBinding = type1.resolveBinding()
                        String qualifiedName1 = typeBinding.getQualifiedName()
                        return new ClRef(qualifiedName1)
                    }
                }
            }
        }
        if (type instanceof TypeLiteral) {
            TypeLiteral typeLiteral = (TypeLiteral) type;
            Type type1 = type.getType()
            log.info "typeLiteral=${typeLiteral}  type1=${type1}"
            ITypeBinding binding111 = type1.resolveBinding()
            log.info "binding111 ${binding111.getQualifiedName()}"
            return new ClRef(binding111.getQualifiedName())
//            return resolveBinding(type)
        }
        if (type instanceof org.eclipse.jdt.core.dom.MethodInvocation) {
            log.info "${type.getName()}"
            if (type.getName().identifier == 'getClass') {
                Expression expression1 = type.getExpression()
                return resolveBinding(expression1)
            }
            return resolveBinding(type)
        }
        if (type instanceof org.eclipse.jdt.core.dom.ThisExpression) {
            return resolveBinding(type)
        }
        if (type instanceof org.eclipse.jdt.core.dom.QualifiedName) {
            return resolveBinding(type)
        }
        if (type instanceof SimpleName) {
            SimpleName simpleName1 = (SimpleName) type;
    //        log.info "sn=${simpleName1}"
      //      log.info "sn=${simpleName1.isVar()}"

  //          log.info "cve=${simpleName1.resolveConstantExpressionValue()}"
            IBinding binding = simpleName1.resolveBinding()
//            log.info "b= ${binding.getClass().getName()} ${binding} "
            if (binding instanceof org.eclipse.jdt.core.dom.IVariableBinding) {
                org.eclipse.jdt.core.dom.IVariableBinding variableBinding = (org.eclipse.jdt.core.dom.IVariableBinding) binding;

        //        log.info "je = ${variableBinding.getJavaElement()}"
                IVariableBinding variableDeclaration = variableBinding.getVariableDeclaration()
          //      log.info " variableDeclaration = ${variableDeclaration.getClass().getName()} ${variableDeclaration}"
                def constantValue = variableBinding.getConstantValue()
            //    log.info "constantValue = ${constantValue}"
              //  log.info "constantValue2 = ${variableDeclaration.getConstantValue()}"
                ITypeBinding type1 = variableDeclaration.getType()
                return new ClRef(type1.getQualifiedName())
                //return variableDeclaration.
            }
        }
        return null
    }

    private ClRef resolveBinding(Expression type) {
        ITypeBinding typeBinding = type.resolveTypeBinding()
        String qualifiedName = typeBinding.getQualifiedName()
        return new ClRef(qualifiedName)
    }


    public List<String> lines;
    public String content;
    public File currentFile

    @Override
    void onNewCompilationUnit(File sourceFilePath, CompilationUnit ast) {
        log.info "new file ${sourceFilePath}"
        currentFile = sourceFilePath
        content = currentFile.text
        lines = content.readLines()
        ast.accept(this)
    }

    String getLineConentByNumber(int lineNumer) {
        assert lineNumer > 0
        int size1 = lines.size()
        assert lineNumer <= size1
        return lines[lineNumer - 1]
    }

    int resolveLineNumberByOffset(int offset) {
        int length = content.length()
        assert offset < length
        String substring = content.substring(0, offset)
        int count11 = substring.count('\n')
        return count11 + 1
    }

    boolean checkNOFIELDCHECK = true


    boolean isOkLine(int lineNum) {
        if (lineNum == -1) {
            return true
        }
        assert lineNum > 0
        if (checkNOFIELDCHECK) {
            String content = lines[lineNum - 1]
            if (content.contains(net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy.ExpressionFinderFields3.NOFIELDCHECK)) {
                return false
            }
        }
        return true
    }

    public List<FieldFoundedEls> interested = []


    void onElementFound(ClassMemberRef obj1, int lineNum) {
        log.info "found ${currentFile}:${lineNum}    ${obj1}"
        LocationRef locationRef = new LocationRef(currentFile, lineNum)
        interested.add new FieldFoundedEls(locationRef, obj1)
    }

    public PrintStream problemStream = System.out


    void printProblems() {
        if (interested.size() == 0) {
            log.info("no problems found")
        } else {
//            interested.each { eachProblem(it) }
            interested = interested.sort()
            interested = interested.findAll { isPrintProblem(it) }
            problemStream.println("================")
            problemStream.println("founded problems ${interested.size()} :")
            interested.each {
                problemFound2(it)
            }
        }
    }


    boolean isPrintProblem(FieldFoundedEls pb) {
        return true
    }




    void problemFound2(FieldFoundedEls pb) {
        problemStream.println("${pb}")
    }

}
