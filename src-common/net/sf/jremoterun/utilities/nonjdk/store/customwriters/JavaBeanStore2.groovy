package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanCommon
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanCustomField
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.logging.Logger

@CompileStatic
class JavaBeanStore2 implements CustomWriter<JavaBeanCommon> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean writeNull
    public boolean writeAsProp

    JavaBeanStore2(boolean writeNull, boolean writeAsProp) {
        this.writeNull = writeNull
        this.writeAsProp = writeAsProp
    }

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, JavaBeanCommon javaBean) {
        //writer3.addImport(javaBean.getClass())
        List<Field> fields = getFields(javaBean)
        fields = fields.findAll { isWriteField(javaBean, it) }
        int countt = -1
        List<String> resuult2 = fields.collect {
            countt++
            try {
                writeOneField(writer3, objectWriter, it, javaBean)
            } catch (Throwable e) {
                log.info "failed write field ${it.getName()} : ${e}"
                objectWriter.failedWriteCountedEl(it, countt, e)
                throw e
            }
        }
        resuult2 = resuult2.findAll { it != null }
        return writeBeanELs(writer3, javaBean, resuult2)
    }

    boolean isWriteField(JavaBeanCommon javaBean, Field field1) {
        if (Modifier.isStatic(field1.getModifiers())) {
            return false
        }
        if (field1.getAnnotation(IgnoreField) != null) {
            return false
        }
        if (field1.getType() == MetaClass) {
            return false
        }
        return true
    }

    List<Field> getFields(JavaBeanCommon javaBean) {
        List<Field> fields = JrrClassUtils.getDeclaredFields(javaBean.getClass()).toList()
        return fields
    }

    String writeBeanELs(Writer3Import writer3, JavaBeanCommon javaBean, List<String> resuult2) {
        return " new ${writer3.addImportWithName(javaBean.getClass())} ( ${resuult2.join(', ')} ) "
    }

    Object getFieldValue(Field f, JavaBeanCommon javaBean) {
        f.setAccessible(true)
        Object fieldValue = f.get(javaBean)
        return fieldValue
    }

    String writeOneField(Writer3Import writer3, ObjectWriterI objectWriter, Field f, JavaBeanCommon javaBean) {
        Object fieldValue = getFieldValue(f, javaBean)
        if (fieldValue == null && !writeNull) {
            return null;
        }
        String serValue
        if (javaBean instanceof JavaBeanCustomField) {
            JavaBeanCustomField javaBean2CustomField = (JavaBeanCustomField) javaBean;
            serValue = javaBean2CustomField.customizeField(writer3,objectWriter, f, fieldValue)
        }else {
            serValue = objectWriter.writeObject(writer3, fieldValue)
        }
        return writeProp(f, serValue,javaBean)
    }

    String writeProp(Field fieldName, String serValue, JavaBeanCommon javaBean) {
        if(writeAsProp) {
            return "${fieldName.getName()}: ${serValue}"
        }
        return serValue
    }

    @Override
    Class getDataClass() {
        throw new Exception()
    }
}
