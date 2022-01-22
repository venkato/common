package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef

import java.lang.reflect.Field;
import java.util.logging.Logger;

@CompileStatic
class FieldRef  implements ClassMemberRef{

    // may be add field type

    public Class clazz;
    ClRefRef clRef;
    public String fieldName;

    FieldRef(Class clazz1, String fieldName) {
        this.clazz = clazz1
        clRef = new ClRef(clazz1)
        this.fieldName = fieldName
        if(clRef==null){
            throw new NullPointerException('clRef is null')
        }
        assert fieldName!=null
    }

    FieldRef(Field field) {
        this(field.getDeclaringClass(),field.getName())
    }

    FieldRef(ClRefRef clRef, String fieldName) {
        this.clRef = clRef
        this.fieldName = fieldName
        if(clRef==null){
            throw new NullPointerException('clRef is null')
        }
        assert fieldName!=null
    }

    Field resolve() {
        if(clazz==null){
            return JrrClassUtils.findField(clRef.getClRef(), fieldName);
        }
        return JrrClassUtils.findField(clazz, fieldName);
    }

    @Override
    String toString() {
        return "${clRef} # ${fieldName}"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.getClass()) return false

        FieldRef fieldRef = (FieldRef) o

        if (clRef != fieldRef.clRef) return false
        if (fieldName != fieldRef.fieldName) return false

        return true
    }

    int hashCode() {
        int result
        result = (clRef != null ? clRef.hashCode() : 0)
        result = 31 * result + (fieldName != null ? fieldName.hashCode() : 0)
        return result
    }
}
