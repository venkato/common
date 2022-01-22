package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs.groovy

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.ClassMemberRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfo
import net.sf.jremoterun.utilities.nonjdk.store.WriteStateMySelf
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.ObjectWriterI
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.expr.Expression

import java.util.logging.Logger;

@CompileStatic
class FieldFoundedEls extends StaticElementInfo implements WriteStateMySelf{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public ClassMemberRef foundEl;


    public Expression expression;
    public LocationRef locationRef;

    public ClassNode classNode;


    FieldFoundedEls( Expression expression,ClassMemberRef foundEl) {
        this.foundEl = foundEl
        this.expression = expression
        assert foundEl!=null
        assert expression!=null
    }

    FieldFoundedEls(LocationRef locationRef, ClassMemberRef foundEl) {
        this.locationRef = locationRef
        this.foundEl = foundEl
    }

    @Override
    String getPrintablePath() {
        return locationRef.toString()
    }

    @Override
    String getClassName() {
        return classNode.getName()
    }

    @Override
    String getFieldName() {
        return null
    }


    @Override
    boolean isStatic() {
        return true
    }

    @Override
    int getLineNumber() {
        return expression.getLineNumber()
    }

    @Override
    String getFileName() {
        return classNode.nameWithoutPackage
    }

    @Override
    String toString() {
        if(expression==null){
            if(locationRef==null){
                return "${foundEl}"
            }
            return "${locationRef.lineNumber} ${foundEl}"
        }
        return "${expression.lineNumber} ${foundEl}"
    }

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter) {
        return objectWriter.writeConstructorWithArgs(writer3, getClass(),  [locationRef, foundEl]);
    }
}
