package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.CustomWriter

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.logging.Logger

@CompileStatic
class JavaBeanStore2 implements CustomWriter<JavaBean2> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static String save2(JavaBean2 javaBean, Writer3 writer3, ObjectWriter objectWriter, boolean writeNull) {
        writer3.addImport(javaBean.getClass())
        StringBuilder sb = new StringBuilder();
        List<Field> fields = JrrClassUtils.getDeclaredFields(javaBean.getClass()).toList()
        fields = fields.findAll { !Modifier.isStatic(it.getModifiers()) }
        fields = fields.findAll { it.getAnnotation(IgnoreField) == null }
        List<String> resuult2 = fields.collect {
            Field f = it as Field;
            String fieldName = f.name
            String result
            try {
                f.setAccessible(true)
                switch (f) {
                    case { f.type == MetaClass }:
                        break
                    default:
                        Object fieldValue = f.get(javaBean)
                        if (fieldValue == null && !writeNull) {
                            result = null;
                        } else {
                            String serValue = objectWriter.writeObject(writer3, fieldValue)
                            result = "${fieldName}: ${serValue}"
                        }
                }
                return result
            } catch (Throwable e) {
                log.info "failed write field ${fieldName} : ${e}"
                throw e
            }
        }
        resuult2 = resuult2.findAll { it != null }
        String resuult3 = " new ${javaBean.getClass().getSimpleName()} ( ${resuult2.join(', ')} ) "
        return resuult3;
    }

    @Override
    String save(Writer3 writer3, ObjectWriter objectWriter, JavaBean2 obj) {
        return save2(obj, writer3, objectWriter, false)
    }

    @Override
    Class getDataClass() {
        throw new Exception()
    }
}
