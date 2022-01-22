package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean2
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.logging.Logger

@CompileStatic
class JavaBeanStore2 implements CustomWriter<JavaBean2> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean writeNull = false

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, JavaBean2 javaBean) {
        writer3.addImport(javaBean.getClass())
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
        return writeBeanELs(javaBean, resuult2)
    }

    boolean isWriteField(JavaBean2 javaBean, Field it) {
        if (Modifier.isStatic(it.getModifiers())) {
            return false
        }
        if (it.getAnnotation(IgnoreField) != null) {
            return false
        }
        if (it.getType() == MetaClass) {
            return false
        }
        return true
    }

    List<Field> getFields(JavaBean2 javaBean) {
        List<Field> fields = JrrClassUtils.getDeclaredFields(javaBean.getClass()).toList()
        return fields
    }

    String writeBeanELs(JavaBean2 javaBean, List<String> resuult2) {
        return " new ${javaBean.getClass().getSimpleName()} ( ${resuult2.join(', ')} ) "
    }

    String writeOneField(Writer3Import writer3, ObjectWriterI objectWriter, Field f, JavaBean2 javaBean) {
        f.setAccessible(true)

        Object fieldValue = f.get(javaBean)
        if (fieldValue == null && !writeNull) {
            return null;
        }
        String serValue = objectWriter.writeObject(writer3, fieldValue)
        return writeProp(f.getName(), serValue)
    }

    String writeProp(String fieldName, String serValue) {
        return "${fieldName}: ${serValue}"
    }

    @Override
    Class getDataClass() {
        throw new Exception()
    }
}
