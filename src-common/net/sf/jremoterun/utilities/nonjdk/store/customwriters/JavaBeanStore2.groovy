package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean2
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3Import

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.logging.Logger

@CompileStatic
class JavaBeanStore2 implements CustomWriter<JavaBean2> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean writeNull = false

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

    String save2(JavaBean2 javaBean, Writer3Import writer3, ObjectWriter objectWriter) {
        writer3.addImport(javaBean.getClass())
        //StringBuilder sb = new StringBuilder();
        List<Field> fields = JrrClassUtils.getDeclaredFields(javaBean.getClass()).toList()
        fields = fields.findAll { isWriteField(javaBean, it) }
        int countt = -1
        List<String> resuult2 = fields.collect {
            countt++
            try {
                writeOneField(it, javaBean, writer3, objectWriter)
            } catch (Throwable e) {
                log.info "failed write field ${it} : ${e}"
                objectWriter.failedWriteCountedEl(it, countt, e)
                throw e
            }
        }
        resuult2 = resuult2.findAll { it != null }
        return writeBeanEL(javaBean, resuult2)
    }

    String writeBeanEL(JavaBean2 javaBean, List<String> resuult2) {
        return " new ${javaBean.getClass().getSimpleName()} ( ${resuult2.join(', ')} ) "
    }

    String writeOneField(Field f, JavaBean2 javaBean, Writer3Import writer3, ObjectWriter objectWriter) {
        String fieldName = f.name
//        String result

        f.setAccessible(true)

        Object fieldValue = f.get(javaBean)
        if (fieldValue == null && !writeNull) {
            return null;
        }
        String serValue = objectWriter.writeObject(writer3, fieldValue)
        return "${fieldName}: ${serValue}"

    }

    @Override
    String save(Writer3Import writer3, ObjectWriter objectWriter, JavaBean2 obj) {
        return save2(obj, writer3, objectWriter)
    }

    @Override
    Class getDataClass() {
        throw new Exception()
    }
}
