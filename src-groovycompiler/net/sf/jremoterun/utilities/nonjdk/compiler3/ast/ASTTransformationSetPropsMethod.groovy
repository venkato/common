package net.sf.jremoterun.utilities.nonjdk.compiler3.ast

import groovy.transform.CompileStatic
import groovyjarjarasm.asm.MethodVisitor
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.classgen.BytecodeInstruction
import org.codehaus.groovy.classgen.BytecodeSequence
import org.codehaus.groovy.classgen.GeneratorContext
import org.codehaus.groovy.classgen.asm.BytecodeHelper
import org.codehaus.groovy.control.CompilationFailedException
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.CompilerConfiguration
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.control.customizers.CompilationCustomizer

import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger

import static groovyjarjarasm.asm.Opcodes.*

@CompileStatic
class ASTTransformationSetPropsMethod extends org.codehaus.groovy.control.customizers.CompilationCustomizer {
    private static final Logger log = Logger.getLogger(ASTTransformationSetPropsMethod.getName());

    public List<String> adedTo = []
    public List<String> skipped = []

    public boolean generateForExceptions = true;


    private static final Parameter[] INVOKE_METHOD_PARAMS = new Parameter[]{
            new Parameter(ClassHelper.STRING_TYPE, "method"),
            new Parameter(ClassHelper.OBJECT_TYPE, "arguments")
    };
    private static final Parameter[] SET_PROPERTY_PARAMS = new Parameter[]{
            new Parameter(ClassHelper.STRING_TYPE, "property"),
            new Parameter(ClassHelper.OBJECT_TYPE, "value")
    };
    private static final Parameter[] GET_PROPERTY_PARAMS = new Parameter[]{
            new Parameter(ClassHelper.STRING_TYPE, "property")
    };
    private static final Parameter[] SET_METACLASS_PARAMS = new Parameter[]{
            new Parameter(ClassHelper.METACLASS_TYPE, "mc")
    };

    ASTTransformationSetPropsMethod() {
        super(CompilePhase.INSTRUCTION_SELECTION)
    }


    /**
     * @see org.jetbrains.jps.incremental.groovy.JointCompilationClassLoader
     */
    static void addFileToClassloader(ClassLoader cl, File f) {
        assert f.exists()
        Method method1 = cl.getClass().getMethod("addFiles", List)
        method1.setAccessible(true)
        Path path1 =f.toPath()
        List ll = [path1];
        method1.invoke(cl, ll)
    }

    /**
     * @see org.jetbrains.groovy.compiler.rt.DependentGroovycRunner#applyConfigurationScript
     */
    static void addCustomize(File f, groovy.lang.Binding binding2) {
        org.codehaus.groovy.control.CompilerConfiguration cc = binding2.getVariable('configuration') as CompilerConfiguration
        ClassLoader clll = cc.getClass().getClassLoader()
        addFileToClassloader(clll, f)
        CompilationCustomizer compilationCustomizer = clll.loadClass('net.sf.jremoterun.utilities.nonjdk.compiler3.ast.ASTTransformationSetPropsMethod').newInstance() as CompilationCustomizer
        cc.addCompilationCustomizers(compilationCustomizer);
    }

    @Override
    void call(SourceUnit source, GeneratorContext context, ClassNode classNode) throws CompilationFailedException {
        forEachClass1(classNode);
    }

    boolean isException(ClassNode classNode1) {
        if (classNode1.getName() == Object.getName()) {
            return false
        }
        if (classNode1.getName() == Throwable.getName()) {
            return true;
        }

        ClassNode spp = classNode1.getSuperClass()
        if (spp == null) {
            return false
        }
        return isException(spp)
    }

    boolean isNeedDo123(ClassNode classNode1) {
        String name23 = classNode1.getName()

        if (classNode1.isInterface()) {
            return false
        }
        if(!generateForExceptions) {
            if (isException(classNode1)) {
                return false
            }
        }
        if (classNode1.isDerivedFrom(ClassHelper.GSTRING_TYPE)
                || classNode1.isDerivedFrom(ClassHelper.GROOVY_OBJECT_SUPPORT_TYPE)) {
            return false
        }
        return true;
    }

    void forEachClass1(ClassNode classNode1) {
        String name23 = classNode1.getName()
        boolean needDo = isNeedDo123(classNode1)


        if (needDo) {
            adedTo.add name23
            addGroovyObjectInterfaceAndMethods(classNode1)
        } else {
            skipped.add name23
        }
    }


    void addInvokeMethod(ClassNode classNode1, final String classInternalName) {
        VariableExpression vMethods = new VariableExpression("method");
        VariableExpression vArguments = new VariableExpression("arguments");
        VariableScope blockScope = new VariableScope();
        blockScope.putReferencedLocalVariable(vMethods);
        blockScope.putReferencedLocalVariable(vArguments);

        addMethod4(classNode1, !Modifier.isAbstract(classNode1.getModifiers()),
                "invokeMethod",
                ACC_PUBLIC,
                ClassHelper.OBJECT_TYPE, INVOKE_METHOD_PARAMS,
                ClassNode.EMPTY_ARRAY,
                new BytecodeSequence(new BytecodeInstruction() {
                    public void visit(MethodVisitor mv) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, classInternalName, "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitVarInsn(ALOAD, 2);
                        mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClass", "invokeMethod", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", true);
                        mv.visitInsn(ARETURN);
                    }
                })
        );
    }


    void addGetProperty(ClassNode classNode1, final String classInternalName) {
        addMethod4(classNode1, !Modifier.isAbstract(classNode1.getModifiers()),
                "getProperty",
                ACC_PUBLIC,
                ClassHelper.OBJECT_TYPE,
                GET_PROPERTY_PARAMS,
                ClassNode.EMPTY_ARRAY,
                new BytecodeSequence(new BytecodeInstruction() {
                    public void visit(MethodVisitor mv) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, classInternalName, "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClass", "getProperty", "(Ljava/lang/Object;Ljava/lang/String;)Ljava/lang/Object;", true);
                        mv.visitInsn(ARETURN);
                    }
                })
        );
    }

    void addSetProperty(ClassNode classNode1, final String classInternalName) {
        addMethod4(classNode1, !Modifier.isAbstract(classNode1.getModifiers()),
                "setProperty",
                ACC_PUBLIC,
                ClassHelper.VOID_TYPE,
                SET_PROPERTY_PARAMS,
                ClassNode.EMPTY_ARRAY,
                new BytecodeSequence(new BytecodeInstruction() {
                    public void visit(MethodVisitor mv) {
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitMethodInsn(INVOKEVIRTUAL, classInternalName, "getMetaClass", "()Lgroovy/lang/MetaClass;", false);
                        mv.visitVarInsn(ALOAD, 0);
                        mv.visitVarInsn(ALOAD, 1);
                        mv.visitVarInsn(ALOAD, 2);
                        mv.visitMethodInsn(INVOKEINTERFACE, "groovy/lang/MetaClass", "setProperty", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/Object;)V", true);
                        mv.visitInsn(RETURN);
                    }

                })
        );
    }

    void addGroovyObjectInterfaceAndMethods(ClassNode classNode1) {
        if (false) {
            new org.codehaus.groovy.classgen.Verifier()
        }
        final String classInternalName = BytecodeHelper.getClassInternalName(classNode1);
        if (!classNode1.hasMethod("invokeMethod", INVOKE_METHOD_PARAMS)) {
            addInvokeMethod(classNode1, classInternalName)
        }

        if (!classNode1.hasMethod("getProperty", GET_PROPERTY_PARAMS)) {
            addGetProperty(classNode1, classInternalName)
        }

        if (!classNode1.hasMethod("setProperty", SET_PROPERTY_PARAMS)) {
            addSetProperty(classNode1, classInternalName)
        }


    }

    void addMethod4(ClassNode node, boolean shouldBeSynthetic, String name, int modifiers, ClassNode returnType, Parameter[] parameters,
                    ClassNode[] exceptions, Statement code) {
        //shouldBeSynthetic = false
        node.addMethod(name, modifiers & ~ACC_SYNTHETIC, returnType, parameters, exceptions, code);
    }

}
