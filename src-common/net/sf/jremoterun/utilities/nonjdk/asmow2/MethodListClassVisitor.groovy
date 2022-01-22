package net.sf.jremoterun.utilities.nonjdk.asmow2

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.util.Printer
import org.objectweb.asm.util.TraceMethodVisitor

import java.util.logging.Logger

/**
 * Also can be used
 * @see org.eclipse.jdt.internal.compiler.classfmt.ClassFileReader
 */
@CompileStatic
class MethodListClassVisitor extends ClassVisitor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public HashSet<String> existedMethods =new HashSet<>()

    MethodListClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        String key = name+descriptor
        existedMethods.add(key)
        return methodVisitor
    }
}
