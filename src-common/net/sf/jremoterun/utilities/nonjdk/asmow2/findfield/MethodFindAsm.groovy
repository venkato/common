package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ConstructorRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef

import java.util.logging.Logger

@CompileStatic
class MethodFindAsm extends AsmClassFinder<MethodRef> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public MethodRef fieldRef;

    void setConstaructor(ConstructorRef contructorRef){
        fieldRef = new MethodRef(contructorRef.clRef, net.sf.jremoterun.utilities.nonjdk.asmow2.accessmodif.AsmConstains.constructorMethodName,contructorRef.paramsCount) //NOFIELDCHECK
    }

//    @Override
//    ClRef getFirstClass() {
//        return fieldRef.clRef.clRef
//    }

    BaseVisitor createClassVisitor() {
        MethodFinderVisitor v = new MethodFinderVisitor()
        v.findFieldAsm = this
        return v
    }


    boolean prepare(MethodRef fieldRef1){
        fieldRef = fieldRef1
    }

    boolean prepare(ConstructorRef fieldRef1){
        setConstaructor(fieldRef1)
        checkParents = false
    }


}
