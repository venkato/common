package net.sf.jremoterun.utilities.nonjdk.langutils.cloner

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
    void onSameClasses(T lhs, T rhs) {
        compareUsingFields(fieldsSameClass, lhs, rhs)
    }


}
