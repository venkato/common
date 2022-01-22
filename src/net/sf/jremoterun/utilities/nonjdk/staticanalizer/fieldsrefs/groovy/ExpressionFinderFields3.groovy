package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy


import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ClassMemberRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.CurrentDirDetector
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.LoaderStuff
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldTypeCollection
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.ElementMethodGroovy
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.FailedParse
import org.codehaus.groovy.ast.ClassCodeVisitorSupport
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Variable
import org.codehaus.groovy.ast.expr.*
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.stc.StaticTypesMarker

import java.util.logging.Logger

@CompileStatic
class ExpressionFinderFields3 extends ClassCodeVisitorSupport {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String NOFIELDCHECK = "NOFIELDCHECK";
    public Set<Integer> ignoreLines;

    public LoaderStuff loaderStuff

    public ClassNode classNode;
    public String elName;
    public String printablePath;

    public String methodName
    public List<String> lines
    public boolean checkNOFIELDCHECK = true


    public List<FieldFoundedEls> interested = []


    List<Integer> debugLines = []

    ExpressionFinderFields3(List<String> lines) {
        this.lines = lines
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        methodName = node.name
        super.visitConstructorOrMethod(node, isConstructor)
        methodName = null
    }

    boolean isOkLine(int lineNum) {
        if(lineNum==-1){
            return true
        }
        assert lineNum > 0
        if(ignoreLines.contains(lineNum)){
            return false
        }
        if(checkNOFIELDCHECK) {
            String content = lines[lineNum - 1]
            if (content.contains(NOFIELDCHECK)) {
                return false
            }
        }
        return true
    }


    void castExpressionImpl2(Expression parentExpression, ConstantExpression ce, boolean isFileExp) {
        String text = ce.text
        ElementMethodGroovy elementMethodGroovy = new ElementMethodGroovy();
        elementMethodGroovy.expression = parentExpression;
        elementMethodGroovy.fieldName = methodName
        elementMethodGroovy.className = elName
        elementMethodGroovy.printablePath = printablePath
        elementMethodGroovy.isParentFile = true
        elementMethodGroovy.fieldType = isFileExp ? StaticFieldType.File : StaticFieldType.ClRef
        if (isFileExp) {
            if (CurrentDirDetector.isCurrentDir(text)) {

            } else {
                File f = text as File
                if (f.exists()) {
                    loaderStuff.onFileFound(elementMethodGroovy, f)
                } else {
                    loaderStuff.problemFound(elementMethodGroovy, text)
                }
            }
        } else {
            try {
                if (!loaderStuff.checkClassExists(text)) {
                    loaderStuff.problemFound(elementMethodGroovy, text)
                }
//                loaderStuff.loadClass(text)
//            }catch (ClassNotFoundException e){
//                loaderStuff.problemFound(elementMethodGroovy,text)
            } catch (Throwable e) {
                loaderStuff.onException(e, parentExpression)
            }
        }
    }


    int resolveInt(Expression expression1Count) {
        org.codehaus.groovy.ast.expr.ConstantExpression ceee = expression1Count as ConstantExpression
        int counnttt = ceee.getText() as int
        return counnttt
    }

    void visitConstructorCallExpression3(ConstructorCallExpression expression) {
        String nameWithoutRef = expression.type.nameWithoutPackage
        StaticFieldType staticFieldType = StaticFieldType.map1.get(nameWithoutRef)
//        log.info "staticFieldType = ${staticFieldType}"
        if (staticFieldType in [ StaticFieldType.MethodRef , StaticFieldType.FieldRef , StaticFieldType.ConstructorRef]) {
            //interested.add(expression)
//            String text1 = expression.getType().getNameWithoutPackage()
//            StaticFieldType staticFieldType = StaticFieldType.map1.get(text1)
            boolean debugg = debugLines.contains(expression.lineNumber)
            if (debugg) {
                log.info "${expression.lineNumber} ${staticFieldType} "
            }
            //if (staticFieldType in [ StaticFieldType.FieldRef , StaticFieldType.MethodRef , StaticFieldType.ConstructorRef]) {

                Expression arguments = expression.getArguments()
                if (arguments instanceof ArgumentListExpression) {
                    ArgumentListExpression list = (ArgumentListExpression) arguments;
                    ClRef clRef7 = resolveClRefExpression(arguments[0])
                    List<Expression> expressions44 = list.expressions
                    //assert staticFieldType == StaticFieldType.FieldRef
                    if (staticFieldType == StaticFieldType.FieldRef) {
                        Expression nestedExpression = expressions44[1]
                        if (nestedExpression instanceof ConstantExpression) {
                            ConstantExpression ce = (ConstantExpression) nestedExpression;
                            onElementFound(new FieldRef(clRef7, ce.getText()), expression)
                        }
                    }
                    if (staticFieldType == StaticFieldType.MethodRef) {
                        Expression nestedExpression = expressions44[1]
                        if (nestedExpression instanceof ConstantExpression) {
                            ConstantExpression ce = (ConstantExpression) nestedExpression;
                            if (expressions44[2] instanceof ConstantExpression) {
                                int counnttt = resolveInt(expressions44[2])
                                onElementFound(new MethodRef(clRef7, ce.getText(), counnttt), expression)
                            }
                        }
                    }
                    if (staticFieldType == StaticFieldType.ConstructorRef) {
                        if (expressions44[1] instanceof ConstantExpression) {
                            int counnttt = resolveInt(expressions44[1])
                            onElementFound(new ConstructorRef(clRef7, counnttt), expression)
                        }
                    }
                }
        //    }
        }
    }

    void onElementFound(ClassMemberRef obj1, Expression e) {
        interested.add new FieldFoundedEls(e, obj1)
    }

    private ClassType isCheckType(MethodCallExpression expression) {
        Expression getObjectExpression = expression.getObjectExpression()
        if (getObjectExpression instanceof ClassExpression) {
            ClassExpression classExpression1 = (ClassExpression) getObjectExpression;
            String name1 = classExpression1.getType().getName()
            if (name1 == JrrClassUtils.getName()) {
                return ClassType.JrrClassUtils
            }
            if (name1 == JrrJavassistUtils.getName()) {
                return ClassType.JrrJavassistUtils
            }
        }
        return null
    }

    @Override
    void visitMethodCallExpression(MethodCallExpression expression) {
        super.visitMethodCallExpression(expression)
        try {
            if (isOkLine(expression.lineNumber)) {
                visitMethodCallExpressionImpl(expression);
            }
        } catch (Throwable eee) {
            log.info "failed on line ${expression.lineNumber} ${expression.getText()} : ${eee}"
            throw eee
        }
    }

    void visitMethodCallExpressionImpl(MethodCallExpression expression) {
        boolean debugg = debugLines.contains(expression.lineNumber)
        if (debugg) {
            log.info "${expression.lineNumber} ${expression.getText()} "
        }
        Expression arguments1 = expression.getArguments()
        if (arguments1 instanceof ArgumentListExpression) {
            ArgumentListExpression argumentListExpression = (ArgumentListExpression) arguments1;
            List<Expression> expressions1 = argumentListExpression.getExpressions()
            Expression method1 = expression.getMethod()
            if (method1 instanceof org.codehaus.groovy.ast.expr.ConstantExpression) {
                ClassType type1 = isCheckType(expression)
                if (type1 != null) {
                    org.codehaus.groovy.ast.expr.ConstantExpression constantExpression1 = (org.codehaus.groovy.ast.expr.ConstantExpression) method1;
                    String text1 = constantExpression1.getText()
                    StaticFieldType staticFieldType = StaticFieldType.map1.get(text1)
//                    log.info "staticFieldType2 = ${staticFieldType}"
                    if (staticFieldType != null) {
                        int extraOffset = 0
                        if (staticFieldType.isFirstParamFake() || type1 == ClassType.JrrJavassistUtils) {
                            extraOffset = 1
                        }
                        ClRef expression1Obj = resolveClRefExpression(expressions1[0])
                        if (staticFieldType == StaticFieldType.runMainMethod) {

                            onElementFound(new MethodRef(expression1Obj, JrrClassUtils.mainMethodName, 1), expression)
                        }
                        if (StaticFieldTypeCollection.reFields.contains(staticFieldType )||
                                staticFieldType in [  StaticFieldType.invokeJavaMethodR , StaticFieldType.invokeJavaMethod , StaticFieldType.invokeJavaMethod2 , StaticFieldType.invokeJavaMethod2R , StaticFieldType.findMethodByCount ,StaticFieldType.findMethodByCountR , StaticFieldType.findMethodByParamTypes1
                                , StaticFieldType.findMethodByParamTypes2 , StaticFieldType.findMethodByParamTypes3 , StaticFieldType.findMethodByParamTypes4 ]) {

                            if (expressions1.size() > 1) {

                                Expression expression11 = expressions1[extraOffset + 1]
                                if (expression11 instanceof org.codehaus.groovy.ast.expr.ConstantExpression) {

                                    org.codehaus.groovy.ast.expr.ConstantExpression constantExpression2 = (org.codehaus.groovy.ast.expr.ConstantExpression) expression11;
                                    //ClRef expression1Obj = resolveClRefExpression(expressions1[0])
                                    if (expression1Obj == null) {
                                        log.warning("failed resolve clRef for ${printablePath} line=${expression.lineNumber} expression=${expression.getText()} ")
                                    } else {
                                        if (StaticFieldTypeCollection.reFields.contains(staticFieldType)) {
                                            onElementFound(new FieldRef(expression1Obj, constantExpression2.getText()), expression)
                                        }
                                        if (staticFieldType in [ StaticFieldType.invokeJavaMethod , StaticFieldType.invokeJavaMethodR , StaticFieldType.invokeJavaMethod2R , StaticFieldType.invokeJavaMethod2 ] ) {
                                            int argsCount = expressions1.size() - 2 - extraOffset
                                            assert argsCount >= 0
                                            onElementFound(new MethodRef(expression1Obj, constantExpression2.getText(), argsCount), expression)
                                        }
                                        if (staticFieldType in [ StaticFieldType.findMethodByParamTypes1 , StaticFieldType.findMethodByParamTypes3]) {
                                            int argsCount = expressions1.size() - 2 - extraOffset
                                            assert argsCount >= 0
                                            onElementFound(new MethodRef(expression1Obj, constantExpression2.getText(), argsCount), expression)
                                        }
                                        if (staticFieldType in [ StaticFieldType.findMethodByCount, StaticFieldType.findMethodByCountR,]) {
                                            Expression expressionCounttt = expressions1[2 + extraOffset]
                                            if (expressionCounttt instanceof ConstantExpression) {
                                                int argsCount = resolveInt(expressionCounttt)
                                                assert argsCount >= 0
                                                onElementFound(new MethodRef(expression1Obj, constantExpression2.getText(), argsCount), expression)
                                            }
                                        }
                                    }
                                }
                            }


                        }
                        if (StaticFieldTypeCollection.constructorsFunctions.contains(staticFieldType)) {

                            //Object expression1Obj = resolveClRefExpression(expressions1[0])
                            if (staticFieldType == StaticFieldType.findConstructorByCount) {
                                Expression expressionCounttt = expressions1[1 + extraOffset]
                                if (expressionCounttt instanceof ConstantExpression) {
                                    int counnttt = resolveInt(expressionCounttt)
                                    onElementFound(new ConstructorRef(expression1Obj, counnttt), expression)
                                }
                            } else {
                                onElementFound(new ConstructorRef(expression1Obj, expressions1.size() - 1 - extraOffset), expression)
                            }
                        }
                    }
                }
            }
        }

    }

    ClRef resolveClRefExpression(Expression ee) {
        ClRef impl1 = resolveClRefExpressionImpl(ee)
        if (impl1 == null) {
            String text111 = ee.getText()
            if(text111.contains('JrrClassUtils.getJdkLogForCurrentClass()')){

            }else{
                log.info "failed resolve line ${printablePath} ${ee.getLineNumber()} ${text111}"
            }

        }
        return impl1
    }

    ClRef resolveClRefExpressionImpl(Expression ee) {
        if (ee instanceof org.codehaus.groovy.ast.expr.MethodCallExpression) {
            org.codehaus.groovy.ast.expr.MethodCallExpression classExpression1 = ee;
            Expression method1 = classExpression1.getMethod()
            if (method1 instanceof org.codehaus.groovy.ast.expr.ConstantExpression) {
                org.codehaus.groovy.ast.expr.ConstantExpression constantExpression1 = (org.codehaus.groovy.ast.expr.ConstantExpression) method1;
                if (constantExpression1.getText() == 'getClass') {
                    Expression arguments11 = classExpression1.getArguments()
                    if (arguments11 instanceof ArgumentListExpression) {
                        if (arguments11.size() == 0) {
//                            log.info "${ee}"
                            //log.info "${classExpression1.getObjectExpression()}"
                            return resolveClRefExpression(classExpression1.getObjectExpression())

                            //return new ClRef()
                        }
                    }
                }
            }
            return new ClRef(classExpression1.type.getName())
        }
        if (ee instanceof org.codehaus.groovy.ast.expr.ClassExpression) {
            org.codehaus.groovy.ast.expr.ClassExpression classExpression1 = ee;
            return new ClRef(classExpression1.type.getName())
        }
        if (ee instanceof org.codehaus.groovy.ast.expr.VariableExpression) {
            org.codehaus.groovy.ast.expr.VariableExpression variableExpression1 = (org.codehaus.groovy.ast.expr.VariableExpression) ee;
            if (variableExpression1.getName() == 'this') {
                Object infertedType1 = variableExpression1.metaDataMap.get(StaticTypesMarker.INFERRED_TYPE)
                ClassNode classNode1 = infertedType1 as ClassNode
                return new ClRef(classNode1.getName())
            }
            ClassNode type1 = variableExpression1.getType()
            String name1 = type1.getName()
            if (name1 == 'java.lang.Class' || name1 == ClRef.getName()) {
                Expression expression1 = variableExpression1.getInitialExpression()
                if (expression1 == null) {
                    Variable accessedVariable = variableExpression1.getAccessedVariable()
                    expression1 = accessedVariable.getInitialExpression()
                    if (expression1 == null) {
                        Expression init111 = vars.get(variableExpression1.getName())
                        if (init111 != null) {
                            return resolveClRefExpression(init111)
                        }
                        log.info("no init expression for ${printablePath} line=${variableExpression1.lineNumber} ${variableExpression1.getText()}")
                        loaderStuff.failedParseList.add new FailedParse(new LocationRef(new File(printablePath),variableExpression1.lineNumber),"no init expression ${variableExpression1.getText()}")
                        return null
                    }
                }
                return resolveClRefExpression(expression1)
            }
            return new ClRef(name1)
        }
        if (ee instanceof org.codehaus.groovy.ast.expr.ConstructorCallExpression) {
            org.codehaus.groovy.ast.expr.ConstructorCallExpression constructorCallExpression1 = (org.codehaus.groovy.ast.expr.ConstructorCallExpression) ee;
            //String nameWithoutRef = ;
            //StaticFieldType get1 = StaticFieldType.map1.get(nameWithoutRef)
            if (constructorCallExpression1.getType().getName() == ClRef.getName()) {
                Expression arguments = constructorCallExpression1.getArguments()
                if (arguments instanceof ArgumentListExpression) {
                    ArgumentListExpression list = (ArgumentListExpression) arguments;
                    if (list.expressions.size() == 1) {
                        Expression nestedExpression = list.expressions[0]
                        if (nestedExpression instanceof ConstantExpression) {
                            ConstantExpression ce = (ConstantExpression) nestedExpression;
//                            assert get1 == StaticFieldType.ClRef
                            return new ClRef(ce.getText());
                        }
                        if (nestedExpression instanceof org.codehaus.groovy.ast.expr.ClassExpression) {
                            org.codehaus.groovy.ast.expr.ClassExpression ce = (org.codehaus.groovy.ast.expr.ClassExpression) nestedExpression;
//                            assert get1 == StaticFieldType.ClRef
                            return new ClRef(ce.type.getName());
                        }
                    }
                }
            }
            return null
        }
        if (ee instanceof org.codehaus.groovy.ast.expr.PropertyExpression) {
            org.codehaus.groovy.ast.expr.PropertyExpression propertyExpression1 = (org.codehaus.groovy.ast.expr.PropertyExpression) ee;
            Expression expressionObj = propertyExpression1.getObjectExpression()
            if (expressionObj instanceof ClassExpression) {
                ClassExpression classExpression1 = (ClassExpression) expressionObj;
                return loaderStuff.resolveFieldClass(new ClRef(classExpression1.type.getName()), propertyExpression1.propertyAsString)
            }
        }
        return null
    }




    FileRefLiteral resolveFileExpression(Expression ee) {
        if (ee instanceof org.codehaus.groovy.ast.expr.VariableExpression) {
            org.codehaus.groovy.ast.expr.VariableExpression variableExpression1 = (org.codehaus.groovy.ast.expr.VariableExpression) ee;
            ClassNode type1 = variableExpression1.getType()
            String name1 = type1.getName()
            if (name1 == 'java.lang.Class') {
                Expression expression1 = variableExpression1.getInitialExpression()
                return resolveFileExpression(expression1)
            }
            return null
        }
        if (ee instanceof org.codehaus.groovy.ast.expr.ConstructorCallExpression) {
            org.codehaus.groovy.ast.expr.ConstructorCallExpression constructorCallExpression1 = (org.codehaus.groovy.ast.expr.ConstructorCallExpression) ee;
            String nameWithoutRef = constructorCallExpression1.getType().getNameWithoutPackage();
            StaticFieldType get1 = StaticFieldType.map1.get(nameWithoutRef)
            boolean isFileExp = get1 == StaticFieldType.File
            if (isFileExp || get1 == StaticFieldType.ClRef) {
                Expression arguments = constructorCallExpression1.arguments
                if (arguments instanceof ArgumentListExpression) {
                    ArgumentListExpression list = (ArgumentListExpression) arguments;
                    if (list.expressions.size() == 1) {
                        Expression nestedExpression = list.expressions[0]
                        if (nestedExpression instanceof ConstantExpression) {
                            ConstantExpression ce = (ConstantExpression) nestedExpression;
                            assert get1 == StaticFieldType.File
//                            if (get1 == StaticFieldType.ClRef) {
//                                return new ClRef(ce.getText());
//                            }
//                            assert get1 == StaticFieldType.File
                            return new FileRefLiteral(null, ce.getText())
                        }
                    }
                    if (list.expressions.size() == 2) {
                        Expression nestedExpression = list.expressions[1]
                        if (nestedExpression instanceof ConstantExpression) {
                            ConstantExpression ce = (ConstantExpression) nestedExpression;

                            Object expression1 = resolveClRefExpression(list.expressions[0]);
                            if (expression1 == null) {
                                log.info("failed  parent ${list.expressions[0]}")
                                return null
                            }
                            FileRefLiteral fileRefLiteral = expression1 as FileRefLiteral
                            return new FileRefLiteral(fileRefLiteral, ce.getText())
                        }
                    }

                }
            }
            return null
        }
        if (ee instanceof org.codehaus.groovy.ast.expr.PropertyExpression) {
            org.codehaus.groovy.ast.expr.PropertyExpression propertyExpression1 = (org.codehaus.groovy.ast.expr.PropertyExpression) ee;
            Expression expressionObj = propertyExpression1.getObjectExpression()
            if (expressionObj instanceof ClassExpression) {
                ClassExpression classExpression1 = (ClassExpression) expressionObj;
                Object obj = loaderStuff.resolveField(new ClRef(classExpression1.type.getName()), propertyExpression1.propertyAsString)
                if (obj == null) {
                    return null
                }
                File f1 = obj as File;
                return new FileRefLiteral(null, f1.getAbsoluteFile().getCanonicalPath());
            }
        }
        return null
    }

    void visitConstructorCallExpression(ConstructorCallExpression expression) {
        super.visitConstructorCallExpression(expression)
//        log.info "const = ${expression}"
        try {
            //boolean toChekc =
            if (isOkLine(expression.getLineNumber())) {
                String nameWithoutRef = expression.type.nameWithoutPackage
                StaticFieldType staticFieldType = StaticFieldType.map1.get(nameWithoutRef)
//                log.info "staticFieldType3 = ${staticFieldType}"
                if (StaticFieldTypeCollection.memberRefClasses.contains(staticFieldType)) {
                    visitConstructorCallExpression3(expression)
                }
            }
        } catch (Throwable eee) {
            log.info "failed on line ${expression.lineNumber} ${expression.getText()} : ${eee}"
            throw eee
        }
        //return false
    }

    @Override
    void visitVariableExpression(VariableExpression expression) {
        super.visitVariableExpression(expression)
    }

//    public DeclarationExpression declarationExpression1
    public Map<String, Expression> vars = [:];

    @Override
    void visitDeclarationExpression(DeclarationExpression expression) {
//        declarationExpression1 = expression
        Expression expression11 = expression.getLeftExpression()
        if (expression11 instanceof VariableExpression) {
            VariableExpression variableExpression1 = (VariableExpression) expression11;
            String name1 = variableExpression1.getName()
            if (name1 != null && name1.length() > 0) {
                vars.put(name1, expression.getRightExpression())
            }
        }
        super.visitDeclarationExpression(expression)
//        declarationExpression1 = null
    }

    @Override
    void visitMethod(MethodNode node) {
        vars.clear()
        super.visitMethod(node)
        vars.clear()
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return null
    }
}
