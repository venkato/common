package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.javaeclipse

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.javaparser.eclipse.EclipseJavaParser
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.StaticFieldType
import org.eclipse.jdt.core.dom.ASTVisitor
import org.eclipse.jdt.core.dom.Expression
import org.eclipse.jdt.core.dom.IBinding
import org.eclipse.jdt.core.dom.ITypeBinding
import org.eclipse.jdt.core.dom.IVariableBinding
import org.eclipse.jdt.core.dom.MethodInvocation
import org.eclipse.jdt.core.dom.SimpleName
import org.eclipse.jdt.core.dom.StringLiteral
import org.eclipse.jdt.core.dom.Type
import org.eclipse.jdt.core.dom.TypeLiteral;

import java.util.logging.Logger;

@CompileStatic
class PrivateFieldsRefVistoroe extends ASTVisitor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public EclipseJavaParser eclipseJavaParser = new EclipseJavaParser(this)

    public boolean visit(MethodInvocation node) {

        //ASTNode parent = node.getParent()

        //if (parent instanceof ExpressionStatement) {

        //log.info "${node.getClass().getName()} ${node}"


        Expression expression1 = node.getExpression()
        //log.info "expression = ${expression1.getClass().getName()} ${expression1}"
        if (expression1 instanceof SimpleName) {
            SimpleName simpleName = (SimpleName) expression1;
            IBinding binding1 = simpleName.resolveBinding()
            //log.info "binding1 = ${binding1.getClass().getName()}"
            if (binding1 instanceof org.eclipse.jdt.core.dom.ITypeBinding) {
                org.eclipse.jdt.core.dom.ITypeBinding typeBinding = (org.eclipse.jdt.core.dom.ITypeBinding) binding1;
                //log.info "name = ${typeBinding.getName()}  ${typeBinding.getQualifiedName()}"
                if (typeBinding.getQualifiedName() == JrrClassUtils.getName()) {
                    SimpleName simpleName1 = node.getName()
                    log.info "nodeName = ${simpleName1}"
                    StaticFieldType staticFieldType = StaticFieldType.map1.get(simpleName1.getIdentifier())

                    log.info "staticFieldType = ${staticFieldType}"
                    if (staticFieldType in  StaticFieldType.reFields) {

                        List arguments1 = node.arguments()
                        Object onType = arguments1[0]
                        Object fieldName = arguments1[1]
                        if (fieldName instanceof StringLiteral) {
                            StringLiteral stringLiteral = (StringLiteral) fieldName;
                            ClRef type = resolveType(onType)
                            log.info "type = ${type} fn=${stringLiteral.literalValue} pos=${node.getStartPosition()}"
                        }

//                        log.info "${fieldName.getClass().getName()}"
                        //log.info "args = ${arguments1}"
                    }
                }

            }

        }

//            log.info "parent = ${parent.getClass().getName()} ${parent}"
//
//            ExpressionStatement expressionStatement = (ExpressionStatement) parent;
//            ASTNode parent2 = parent.getParent()
//            log.info "parent2 = ${parent2.getClass().getName()} ${parent2}"

        return super.visit(node);
    }

    ClRef resolveType(Object type){
                                log.info "typ99 = ${type.getClass().getName()}"
        if (type instanceof org.eclipse.jdt.core.dom.ClassInstanceCreation) {
            Type type2 = type.getType()
            ITypeBinding binding = type2.resolveBinding()
            if(binding.getQualifiedName() == ClRef.getName()){
                List arguments = type.arguments()
                if(arguments.size()==1){
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
            return resolveBinding(type)
        }
        if (type instanceof org.eclipse.jdt.core.dom.MethodInvocation) {
            log.info "${type.getName()}"
            if(type.getName().identifier == 'getClass'){
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
            log.info "sn=${simpleName1}"
            log.info "sn=${simpleName1.isVar()}"

            log.info "cve=${simpleName1.resolveConstantExpressionValue()}"
            IBinding binding = simpleName1.resolveBinding()
            log.info "b= ${binding.getClass().getName()} ${binding} "
            if (binding instanceof org.eclipse.jdt.core.dom.IVariableBinding ) {
                org.eclipse.jdt.core.dom.IVariableBinding  variableBinding = (org.eclipse.jdt.core.dom.IVariableBinding ) binding;

                log.info "je = ${variableBinding.getJavaElement()}"
                IVariableBinding variableDeclaration = variableBinding.getVariableDeclaration()
                log.info " variableDeclaration = ${variableDeclaration.getClass().getName()} ${variableDeclaration}"
                def constantValue = variableBinding.getConstantValue()
                log.info "constantValue = ${constantValue}"
                log.info "constantValue2 = ${variableDeclaration.getConstantValue()}"

            }
        }
        return null
    }

    private ClRef resolveBinding(Expression type){
        ITypeBinding typeBinding = type.resolveTypeBinding()
        String qualifiedName = typeBinding.getQualifiedName()
        return new ClRef(qualifiedName)
    }


}
