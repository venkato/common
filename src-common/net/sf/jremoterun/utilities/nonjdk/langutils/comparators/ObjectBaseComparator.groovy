package net.sf.jremoterun.utilities.nonjdk.langutils.comparators

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Logger

@CompileStatic
class ObjectBaseComparator<T> implements Comparator<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    //public Comparator enumComparator =

    @Override
    int compare(T lhs, T rhs) {
        return compareObject(lhs, rhs)
    }

    int compareObject(T lhs, T rhs) {
        Integer cmpNUll = compareNull(lhs, rhs)
        if (cmpNUll != null) {
            return cmpNUll
        }
        if (lhs.is(rhs)) {
            return 0
        }
        return compareObjectNotNull(lhs, rhs)
    }

    int compareObjectNotNull(T lhs, T rhs) {
        boolean classEquals = lhs.getClass() == rhs.getClass()
        //log.debug("${lhsClass} ${rhsClass} : ${classEquals}")
        if (classEquals) {
            return onSameClasses(lhs as T, rhs as T)
        }
        return onDifferentClasses(lhs as T, rhs as T)
    }

    Integer compareNull(Object lhs, Object rhs) {
        if (lhs == null) {
            if (rhs == null) {
                return 0;
            }
            return -1;
        }
        if (rhs == null) {
            return 1
        }
        return null
    }

    int onSameClasses(T lhs, T rhs) {
        onSameClassesC(lhs, rhs)
    }

    int onSameClassesC(T lhs, T rhs) {
        if (lhs instanceof Comparable) {
            if (lhs instanceof Enum) {
                return compareEnum(lhs, rhs as Enum)
            }
            return compareComparable(lhs, rhs as Comparable)
        }
        throw new UnsupportedOperationException("${lhs.getClass()}")
    }


    int compareEnum(Enum lhs, Enum rhs) {
        Integer cmpNUll = compareNull(lhs, rhs)
        if (cmpNUll != null) {
            return cmpNUll
        }
        return lhs.compareTo(rhs)
    }

    int compareComparable(Comparable lhs, Comparable rhs) {
        Integer cmpNUll = compareNull(lhs, rhs)
        if (cmpNUll != null) {
            return cmpNUll
        }
        return lhs.compareTo(rhs)
    }


    int onDifferentClasses(T lhs, T rhs) {
        String lhsClass = lhs.getClass().getName()
        String rhsClass = rhs.getClass().getName()
        return lhsClass.compareTo(rhsClass)
    }


}
