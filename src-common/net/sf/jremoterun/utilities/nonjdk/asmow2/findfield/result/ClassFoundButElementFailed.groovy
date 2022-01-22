package net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.result

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef
import net.sf.jremoterun.utilities.nonjdk.asmow2.findfield.ReflectionElCommon
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ClassMemberRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy.FieldFoundedEls
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI
import org.codehaus.groovy.ast.expr.Expression;

import java.util.logging.Logger;

@CompileStatic
class ClassFoundButElementFailed extends FieldFoundedEls implements ReflectionElCommon {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Object locationInfo

    ClassFoundButElementFailed(Expression expression, ClassMemberRef foundEl, Object locationInfo) {
        super(expression, foundEl)
        this.locationInfo = locationInfo
    }

    ClassFoundButElementFailed(LocationRef locationRef, ClassMemberRef foundEl, Object locationInfo) {
        super(locationRef, foundEl)
        this.locationInfo = locationInfo
    }


    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter) {
        return objectWriter.writeConstructorWithArgs(writer3, getClass(),  [locationRef, foundEl,locationInfo]);
    }




    @Override
    ClRefRef getClRef(){
        foundEl.getClRef()
    }


    @Override
    int getLineNumber(){
        if(locationRef==null){
            return -1
        }
        return locationRef.lineNumber
    }
}
