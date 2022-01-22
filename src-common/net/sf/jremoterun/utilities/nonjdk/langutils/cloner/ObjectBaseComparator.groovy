package net.sf.jremoterun.utilities.nonjdk.langutils.cloner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ObjectBaseComparator<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //public Comparator enumComparator =


    void clone1(T lhs, T rhs) {
        compareObject(lhs, rhs)
    }

    void compareObject(T lhs, T rhs) {
        if (lhs.is(rhs)) {
            throw new IllegalArgumentException("objects are same")
        }
        compareObjectNotNull(lhs, rhs)
    }

    void compareObjectNotNull(T lhs, T rhs) {
        boolean classEquals = lhs.getClass() == rhs.getClass()
        //log.debug("${lhsClass} ${rhsClass} : ${classEquals}")
        if (classEquals) {
            onSameClasses(lhs as T, rhs as T)
        }
        throw new IllegalArgumentException("objects are from different classes ${lhs.getClass()} ${rhs.getClass()}")
    }

    void onSameClasses(T lhs, T rhs) {
        onSameClassesC(lhs, rhs)
    }

    void onSameClassesC(T lhs, T rhs) {
        if (lhs instanceof Enum) {
            throw new IllegalArgumentException("objects is enum ${lhs.getClass()} ${lhs}")
        }
        throw new IllegalArgumentException("objects is enum ${lhs.getClass()} ${lhs}")
    }





}
