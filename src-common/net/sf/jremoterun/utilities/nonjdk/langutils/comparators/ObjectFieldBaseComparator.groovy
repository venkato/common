package net.sf.jremoterun.utilities.nonjdk.langutils.comparators

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
abstract class ObjectFieldBaseComparator<T> extends ObjectBaseComparator<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Comparator compareFields = new ObjectBaseComparator()

    int compareUsingFields(List<Field> fields, T lhs, T rhs) {
        Integer resFoun = compareUsingFieldsImpl(fields, lhs, rhs)
        if (resFoun == null) {
            return 0
        }
        return resFoun
    }

    Integer compareUsingFieldsImpl(List<Field> fields, T lhs, T rhs) {
        Integer resFoun
        fields.each {
            if (resFoun == null) {
                try {
                    int f = compareField(lhs, rhs, it)
                    if (f != 0) {
                        resFoun = f
                    }
                } catch (Throwable e) {
                    onException(lhs, rhs, it, e)
                }
            }
        }
        return resFoun
    }

    void onException(T lhs, T rhs, Field field, Throwable e) {
        log.info "failed compare ${field.getName()} ${e}"
        throw e
    }

    int compareField(T lhs, T rhs, Field field) {
        Object fl = field.get(lhs)
        Object fr = field.get(rhs)
        return getFieldComparator(field).compare(fl, fr)
    }

    Comparator getFieldComparator(Field field) {
        return compareFields
    }


}
