package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.result

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ClassMemberRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy.FieldFoundedEls
import org.codehaus.groovy.ast.expr.Expression

import java.util.logging.Logger

@CompileStatic
class ClassElNotFound extends FieldFoundedEls{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    ClassElNotFound(Expression expression, ClassMemberRef foundEl) {
        super(expression, foundEl)
    }

    ClassElNotFound(LocationRef locationRef, ClassMemberRef foundEl) {
        super(locationRef, foundEl)
    }
}
