package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.EmptyClassVisitor
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ClassMemberRef
import org.objectweb.asm.ClassVisitor;

import java.util.logging.Logger;

@CompileStatic
abstract class BaseVisitor extends EmptyClassVisitor {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public File baseFile
    public ClRef clRef

    BaseVisitor() {
    }

    BaseVisitor(int api) {
        super(api)
    }

    BaseVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }
}
