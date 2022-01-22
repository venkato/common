package net.sf.jremoterun.utilities.nonjdk.staticanalizer.fieldsrefs

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.LocationRef
import net.sf.jremoterun.utilities.nonjdk.props.propstrack.LocationElement
import net.sf.jremoterun.utilities.nonjdk.staticanalizer.els.StaticElementInfo
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.FieldNode
import org.codehaus.groovy.ast.expr.Expression

import java.lang.reflect.Modifier;
import java.util.logging.Logger;

@CompileStatic
class FieldFoundedEls extends StaticElementInfo{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public Object foundEl;


    public Expression expression;
    public LocationRef locationRef;

    public ClassNode classNode;


    FieldFoundedEls( Expression expression,Object foundEl) {
        this.foundEl = foundEl
        this.expression = expression
        assert foundEl!=null
        assert expression!=null
    }

    FieldFoundedEls(LocationRef locationRef, Object foundEl) {
        this.locationRef = locationRef
        this.foundEl = foundEl
    }

    @Override
    String getPrintablePath() {
        return printablePath
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
        return "${expression.lineNumber} ${foundEl}"
    }
}
