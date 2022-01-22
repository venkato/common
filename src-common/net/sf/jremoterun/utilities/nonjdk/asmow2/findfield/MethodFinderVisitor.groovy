package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.EmptyClassVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type

import java.util.logging.Logger

@CompileStatic
class MethodFinderVisitor extends BaseVisitor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MethodFindAsm findFieldAsm;

    MethodFinderVisitor() {
    }

    MethodFinderVisitor(int api) {
        super(api)
    }

    MethodFinderVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        //Type[] types = Type.getArgumentTypes(descriptor)
        int counttt = Type.getArgumentCount(descriptor)
        if (findFieldAsm.fieldRef.methodName == name && counttt == findFieldAsm.fieldRef.paramsCount) {
            AsmFieldFound asmFieldFound = new AsmFieldFound()
            asmFieldFound.access = access
            asmFieldFound.name = name
            asmFieldFound.descriptor = descriptor
            asmFieldFound.signature = signature
//            asmFieldFound.value = value
            findFieldAsm.addFound(this,asmFieldFound)
        }
        return null;
        //return super.visitMethod(access, name, descriptor, signature, exceptions)
    }


}
