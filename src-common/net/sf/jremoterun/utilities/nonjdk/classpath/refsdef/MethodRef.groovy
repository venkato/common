package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef

import java.lang.reflect.Field
import java.lang.reflect.Method

@CompileStatic
class MethodRef {


    public Class clazz;
    public ClRefRef clRef;
    public String methodName;
    public int paramsCount;

    MethodRef(Class clRef, String methodName, int paramsCount) {
        this.clazz = clRef
        this.methodName = methodName
        this.paramsCount = paramsCount
    }
    MethodRef(ClRefRef clRef, String methodName, int paramsCount) {
        this.clRef = clRef
        this.methodName = methodName
        this.paramsCount = paramsCount
    }

    Method resolve() {
        if(clazz==null) {
            Class aClass = clRef.getClRef().loadClass2();
            return JrrClassUtils.findMethodByCount(aClass, methodName, paramsCount);
        }
        return JrrClassUtils.findMethodByCount(clazz, methodName, paramsCount);
    }


}
