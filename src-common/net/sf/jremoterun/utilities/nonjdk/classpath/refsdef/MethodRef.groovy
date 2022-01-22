package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef

import java.lang.reflect.Field
import java.lang.reflect.Method

@CompileStatic
class MethodRef  implements ClassMemberRef{
    // may be add return type


    public Class clazz;
    ClRefRef clRef;
    public String methodName;
    public int paramsCount;

    MethodRef(Class clazz1, String methodName, int paramsCount) {
        this.clazz = clazz1
        this.methodName = methodName
        this.paramsCount = paramsCount
        clRef = new ClRef(clazz1)
        if(clRef==null){
            throw new NullPointerException('clRef is null')
        }
        assert methodName!=null
    }

    MethodRef(ClRefRef clRef, String methodName, int paramsCount) {
        this.clRef = clRef
        this.methodName = methodName
        this.paramsCount = paramsCount
        if(clRef==null){
            throw new NullPointerException('clRef is null')
        }
        assert methodName!=null
    }

    Method resolve() {
        if(clazz==null) {
            Class aClass = clRef.getClRef().loadClass2();
            return JrrClassUtils.findMethodByCount(aClass, methodName, paramsCount);
        }
        return JrrClassUtils.findMethodByCount(clazz, methodName, paramsCount);
    }

    @Override
    String toString() {
        return "${clRef} # ${methodName} ${paramsCount}"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.getClass()) return false

        MethodRef methodRef = (MethodRef) o
        if (paramsCount != methodRef.paramsCount) return false
        if (clRef != methodRef.clRef) return false
        if (methodName != methodRef.methodName) return false

        return true
    }

    int hashCode() {
        int result
        result = (clRef != null ? clRef.hashCode() : 0)
        result = 31 * result + (methodName != null ? methodName.hashCode() : 0)
        result = 31 * result + paramsCount
        return result
    }
}
