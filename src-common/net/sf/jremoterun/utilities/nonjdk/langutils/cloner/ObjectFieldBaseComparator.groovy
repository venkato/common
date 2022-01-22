package net.sf.jremoterun.utilities.nonjdk.langutils.cloner

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
abstract class ObjectFieldBaseComparator<T> extends ObjectBaseComparator<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    int compareUsingFields(List<Field> fields, T lhs, T rhs) {
        compareUsingFieldsImpl(fields, lhs, rhs)
    }

    void compareUsingFieldsImpl(List<Field> fields, T lhs, T rhs) {

        fields.each {

                try {
                    int f = compareField(lhs, rhs, it)
                } catch (Throwable e) {
                    onException(lhs, rhs, it, e)
                }

        }
    }

    void onException(T lhs, T rhs, Field field, Throwable e) {
        log.info "failed compare ${field.getName()} ${e}"
        throw e
    }

    int compareField(T lhs, T rhs, Field field) {
        throw new Exception()
//        Object fl = field.get(lhs)
//        Object fr = field.get(rhs)
//        return getFieldComparator(field).compare(fl, fr)
    }




}
