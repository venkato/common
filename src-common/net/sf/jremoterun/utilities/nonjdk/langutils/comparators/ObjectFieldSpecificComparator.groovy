package net.sf.jremoterun.utilities.nonjdk.langutils.comparators

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
class ObjectFieldSpecificComparator<T> extends ObjectFieldBaseComparator<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public List<Field> fieldsSameClass
    public List<Field> fieldsDiffClass

    ObjectFieldSpecificComparator(List<Field> fieldsSameClass) {
        this.fieldsSameClass = fieldsSameClass
    }

    @Override
    int onSameClasses(T lhs, T rhs) {
        return compareUsingFields(fieldsSameClass, lhs, rhs)
    }

    @Override
    int onDifferentClasses(T lhs, T rhs) {
        if (fieldsDiffClass == null) {
            return super.onDifferentClasses(lhs, rhs)
        }
        return compareUsingFields(fieldsDiffClass, lhs, rhs)
    }


}
