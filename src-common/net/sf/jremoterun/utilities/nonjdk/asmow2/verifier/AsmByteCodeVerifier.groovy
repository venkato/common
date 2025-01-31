package net.sf.jremoterun.utilities.nonjdk.asmow2.verifier

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmConsoleDecompiler
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.analysis.Analyzer

import java.util.logging.Logger;

@CompileStatic
class AsmByteCodeVerifier {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassLoader classLoader1



    public static boolean verifyByteCodeField = true

    //AsmByteCodeVerifier(URLClassLoader classLoader1) {
    AsmByteCodeVerifier(ClassLoader classLoader1) {
        this.classLoader1 = classLoader1
    }

    // may be use org.objectweb.asm.util.CheckClassAdapter
/** may load class :
 * @see org.objectweb.asm.tree.analysis.SimpleVerifier#isAssignableFrom
     */
    ClassNode verifyByteCode(byte[] byteCode) {
        if (!verifyByteCodeField) {
            return null
        }
        ClassReader classReader = new ClassReader(byteCode);
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, getParsingOptions());
        List<MethodNode> methodNodes = classNode.methods
        methodNodes.each {
            MethodNode methodNode = it
            if (methodNode.instructions.size() > 0) {
//            log.info "checking : ${methodNode.name}"
                try {
                    Analyzer analyzer = createAnalyzer();
                    analyzer.analyze(classNode.name, methodNode);
                } catch (Throwable e) {
                    methodVerificationFailed(methodNode, byteCode, e)
                }
            }
        }
        return classNode;
    }

    Analyzer createAnalyzer(){
        return new Analyzer(new JrrAsmVerifier(classLoader1))
    }

    int getParsingOptions(){
        return 0
    }


    public boolean decompileErrorMethod = true;

    void methodVerificationFailed(MethodNode methodNode, byte[] byteCode, Throwable e) {
        if(decompileErrorMethod) {
            String method2 = new AsmConsoleDecompiler().printMethod2(new ClassReader(byteCode), methodNode.name, true)
            log.info "Verify failed in method : ${methodNode.name}: \n ${method2}"
        }else{
            log.info "Verify failed in method : ${methodNode.name}"
        }
        throw new Exception("Verify failed in method : ${methodNode.name}", e)
    }


}
