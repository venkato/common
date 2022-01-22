package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef

import java.lang.reflect.Field;
import java.util.logging.Logger;

@CompileStatic
class FieldRef {

    public Class clazz;
    public ClRefRef clRef;
    public String fieldName;

    FieldRef(Class clRef, String fieldName) {
        clazz = clRef
        this.fieldName = fieldName
    }

    FieldRef(ClRefRef clRef, String fieldName) {
        this.clRef = clRef
        this.fieldName = fieldName
    }

    Field resolve() {
        if(clazz==null){
            return JrrClassUtils.findField(clRef.getClRef(), fieldName);
        }
        return JrrClassUtils.findField(clazz, fieldName);
    }


}
