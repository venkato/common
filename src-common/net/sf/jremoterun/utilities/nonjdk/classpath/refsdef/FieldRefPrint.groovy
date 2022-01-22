package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils;
import java.util.logging.Logger;

@CompileStatic
class FieldRefPrint {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static String printNameWithValue(Object obj, FieldRef fieldRef){
        Object value =  JrrClassUtils.getFieldValue(obj,fieldRef.fieldName)
        return "${fieldRef.fieldName} = ${value}"
    }


}
