package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.langutils.reflictionutils.ObjectFieldsReceiverWithCache
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanCustomField
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
class JavaBeanStore2 implements CustomWriter {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean writeNull
    public boolean writeAsProp
    public ObjectFieldsReceiverWithCache objectFieldsReceiver = new ObjectFieldsReceiverWithCache()

    JavaBeanStore2(boolean writeNull, boolean writeAsProp) {
        this.writeNull = writeNull
        this.writeAsProp = writeAsProp
    }

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, Object javaBean) {
        List<Field> fields = objectFieldsReceiver.getFieldsFilteredPublicObj1(javaBean)
        int countt = -1
        List<String> resuult2 = fields.collect {
            countt++
            try {
                return writeOneField(writer3, objectWriter, it, javaBean)
            } catch (Throwable e) {
                log.info "failed write field ${it.getName()} : ${e}"
                objectWriter.failedWriteCountedEl(it, countt, e)
                throw e
            }
        }
        resuult2 = resuult2.findAll { it != null }
        return writeBeanELs(writer3, javaBean, resuult2)
    }



    String writeBeanELs(Writer3Import writer3, Object javaBean, List<String> resuult2) {
        return " new ${writer3.addImportWithName(javaBean.getClass())} ( ${resuult2.join(', ')} ) "
    }

    Object getFieldValue(Field f, Object javaBean) {
        f.setAccessible(true)
        Object fieldValue = f.get(javaBean)
        return fieldValue
    }

    String writeOneFieldNull(Writer3Import writer3, ObjectWriterI objectWriter, Field f, Object javaBean) {
        if (writeNull) {
            String serValue
            if (javaBean instanceof JavaBeanCustomField) {
                JavaBeanCustomField javaBean2CustomField = (JavaBeanCustomField) javaBean;
                serValue = javaBean2CustomField.customizeField(writer3, objectWriter, f, null)
            } else {
                serValue = objectWriter.writeObject(writer3, null)
            }
            return writeProp(f, serValue, javaBean)
        }
        return null;
    }

    String writeOneField(Writer3Import writer3, ObjectWriterI objectWriter, Field f, Object javaBean) {
        Object fieldValue = getFieldValue(f, javaBean)
        if (fieldValue == null) {
            return writeOneFieldNull(writer3, objectWriter, f, javaBean)
        }
        String serValue
        if (javaBean instanceof JavaBeanCustomField) {
            JavaBeanCustomField javaBean2CustomField = (JavaBeanCustomField) javaBean;
            serValue = javaBean2CustomField.customizeField(writer3, objectWriter, f, fieldValue)
        } else {
            serValue = objectWriter.writeObject(writer3, fieldValue)
        }
        return writeProp(f, serValue, javaBean)
    }

    String writeProp(Field fieldName, String serValue, Object javaBean) {
        if (writeAsProp) {
            return "${fieldName.getName()}: ${serValue}"
        }
        return serValue
    }

    @Override
    Class getDataClass() {
        throw new Exception()
    }
}
