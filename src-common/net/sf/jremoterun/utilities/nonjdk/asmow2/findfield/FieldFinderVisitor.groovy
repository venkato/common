package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.asmow2.EmptyClassVisitor
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor;

import java.util.logging.Logger;

@CompileStatic
class FieldFinderVisitor extends BaseVisitor{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public FieldFindAsm findFieldAsm;

    FieldFinderVisitor() {
    }

    FieldFinderVisitor(int api) {
        super(api)
    }

    FieldFinderVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    @Override
    FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if(findFieldAsm.fieldRef.fieldName == name){
            AsmFieldFound asmFieldFound=new AsmFieldFound()
            asmFieldFound.access = access
            asmFieldFound.name = name
            asmFieldFound.descriptor =descriptor
            asmFieldFound.signature = signature
            asmFieldFound.value = value
            findFieldAsm.addFound(this,asmFieldFound)
        }
        return super.visitField(access, name, descriptor, signature, value)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
//        return super.visitMethod(access, name, descriptor, signature, exceptions)
        return null
    }
}
