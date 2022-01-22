package net.sf.jremoterun.utilities.nonjdk.langutils.comparators

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.langutils.reflictionutils.ObjectFieldsReceiverWithCache

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
class ObjectFieldComparator<T> extends ObjectFieldBaseComparator<T> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ObjectFieldsReceiverWithCache objectFieldsReceiver = new ObjectFieldsReceiverWithCache()

    @Override
    int onSameClasses(T lhs, T rhs) {
        List<Field> fields = objectFieldsReceiver.getFieldsFilteredPublicObj1(lhs)
        return compareUsingFields(fields, lhs, rhs)
    }


}
