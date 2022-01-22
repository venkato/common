package net.sf.jremoterun.utilities.nonjdk.classpath.refsdef

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.groovystarter.runners.ClRefRef

import java.lang.reflect.Constructor

@CompileStatic
class ConstructorRef implements ClassMemberRef{


    public Class clazz;
    ClRefRef clRef;
    public int paramsCount;

    ConstructorRef(Class clazz1, int paramsCount) {
        this.clazz = clazz1
        this.paramsCount = paramsCount
        clRef = new ClRef(clazz1)
        if(clRef==null){
            throw new NullPointerException('clRef is null')
        }

    }

    ConstructorRef(ClRefRef clRef, int paramsCount) {
        this.clRef = clRef
        this.paramsCount = paramsCount
        if(clRef==null){
            throw new NullPointerException('clRef is null')
        }

    }

    Constructor resolve() {
        if(clazz==null) {
            Class aClass = clRef.getClRef().loadClass2();
            return JrrClassUtils.findContructor(aClass, paramsCount);
        }
        return JrrClassUtils.findContructor(clazz, paramsCount);
    }


    @Override
    String toString() {
        return "${clRef} # ${paramsCount}"
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (o == null || getClass() != o.class) return false

        ConstructorRef that = (ConstructorRef) o

        if (paramsCount != that.paramsCount) return false
        if (clRef != that.clRef) return false

        return true
    }

    int hashCode() {
        int result
        result = (clRef != null ? clRef.hashCode() : 0)
        result = 31 * result + paramsCount
        return result
    }
}
