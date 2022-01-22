package net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.AsmUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.verifier.AsmByteCodeVerifier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.logging.Logger
import java.util.zip.ZipEntry

@CompileStatic
class AccessModifControllerP {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public AsmUtils asmUtils
    public AsmByteCodeVerifier asmByteCodeVerifier


    AccessModifControllerP() {
        this(JrrClassUtils.getCurrentClassLoader())
    }

    AccessModifControllerP(ClassLoader cl) {
        asmByteCodeVerifier = new AsmByteCodeVerifier(cl)
        asmUtils = new AsmUtils(cl)
    }

    byte[] removeFinalModifier3(byte[] bs) {
        final ClassReader reader = new ClassReader(bs);
        return removeFinalModifier2(reader, bs)
    }

    byte[] removeFinalModifier2(ClassReader reader, byte[] bs) {
        boolean isInterface = (reader.getAccess() & Opcodes.ACC_INTERFACE) > 0
        if (isInterface) {
            return bs;
        }
        final ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        reader.accept(createClassVisitor(reader, writer), 0);
        byte[] array = writer.toByteArray();
        if (needVerifyClass(reader)) {
            asmByteCodeVerifier.verifyByteCode(array)
        }
        return array
    }

    public boolean isNeedVerify2 = false

    boolean needVerifyClass(ClassReader reader) {
        return isNeedVerify2
    }


    ClassVisitor createClassVisitor(ClassReader reader, ClassWriter writer) {
        return new AccessClassVisitor(writer, this, reader)
    }

    boolean needMakeAllPublic(ClassReader classReader) {
        int access = classReader.getAccess()
        String className1 = classReader.getClassName()
        return isNeedMakeClassPublic2(className1, access, classReader)
    }

    boolean isNeedMakeClassPublic2(String className1, int access, ClassReader classReader) {
        if (className1.contains('$')) {
            boolean isFinal = (access & Opcodes.ACC_FINAL) > 0
            if (isFinal) {
                return true
            }
            //            boolean isStatic = (access & Opcodes.ACC_STATIC) > 0
//            if (!isStatic) {
//                return true
//            }
            return false
        }
        boolean needMakeAllPublic2 = (access & Opcodes.ACC_FINAL) > 0
        return needMakeAllPublic2

    }


    boolean decideAccessForInnderClass(ClassReader classReader, String name, int access) {
        return true
    }

    int modifyOpsCodeIfNeeded(int opcode, String owner, String methodName, String descriptor, boolean isInterface) {
        if (opcode == Opcodes.INVOKESPECIAL) {
            boolean need = isModifyOpsFromInvokeSpecialToVirtual(opcode, owner, methodName, descriptor, isInterface)
            if (need) {
                return Opcodes.INVOKEVIRTUAL
            }
        }
        return opcode
    }

    boolean isModifyOpsFromInvokeSpecialToVirtual(int opcode, String owner, String methodName, String descriptor, boolean isInterface) {
        boolean needpublic = needMakeMethodPublic(methodName, owner)
        return needpublic;
    }

    /**
     * @param className with separator: /
     */
    boolean isNeedMakeClassPublic(String className) {
        return true
    }

    boolean needMakeMethodPublic(String methodName, String className) {
        isNeedMakeClassPublic(className)
    }

    boolean needMakeMethodPublic(String methodName, ClassReader classReader) {
        return needMakeMethodPublic(methodName, classReader.getClassName())
    }

    boolean needMakeFieldPublic(String fieldName, String className) {
        return isNeedMakeClassPublic(className)
    }

    boolean needMakeFieldPublic(String fieldName, ClassReader classReader) {
        return needMakeFieldPublic(fieldName, classReader.getClassName())
    }
}
