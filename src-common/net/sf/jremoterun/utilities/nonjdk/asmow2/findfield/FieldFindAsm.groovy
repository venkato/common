package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef

import java.util.logging.Logger;

@CompileStatic
class FieldFindAsm extends AsmClassFinder<FieldRef> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();



    BaseVisitor createClassVisitor() {
        FieldFinderVisitor v = new FieldFinderVisitor()
        v.findFieldAsm = this
        return v
    }

    void prepare(FieldRef fieldRef1){
        fieldRef = fieldRef1
    }


}
