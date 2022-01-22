package net.sf.jremoterun.utilities.nonjdk.langutils.reflictionutils

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.langutils.ObjectFieldsReceiver

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
class ObjectFieldsReceiverWithCache extends ObjectFieldsReceiver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<String, List<Field>> fieldsCache = [:]



    @Override
    List<Field> getFieldsFilteredPublicClass1(Class class1) {
        if(checkNotClass){
            assert class1!=Class
        }
        List<Field> listOfFields = fieldsCache.get(class1.getName())
        if (listOfFields == null) {
            listOfFields = getFieldsFiltered1(class1)
            fieldsCache.put(class1.getName(), listOfFields)
        }
        return listOfFields
    }

    @Override
    List<Field> getFieldsFilteredPublicObj1(Object javaBean) {
        return getFieldsFilteredPublicClass1(javaBean.getClass())
    }
}
