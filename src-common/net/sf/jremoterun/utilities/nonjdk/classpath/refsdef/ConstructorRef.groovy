package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef

import java.lang.reflect.Constructor
import java.lang.reflect.Method

@CompileStatic
class ConstructorRef {


    public Class clazz;
    public ClRefRef clRef;
    public int paramsCount;

    ConstructorRef(Class clRef, int paramsCount) {
        this.clazz = clRef
        this.paramsCount = paramsCount
    }

    ConstructorRef(ClRefRef clRef, String methodName, int paramsCount) {
        this.clRef = clRef
        this.paramsCount = paramsCount
    }

    Constructor resolve() {
        if(clazz==null) {
            Class aClass = clRef.getClRef().loadClass2();
            return JrrClassUtils.findContructor(aClass, paramsCount);
        }
        return JrrClassUtils.findContructor(clazz, paramsCount);
    }


}
