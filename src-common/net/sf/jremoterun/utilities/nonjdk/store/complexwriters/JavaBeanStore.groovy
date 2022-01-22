package net.sf.jremoterun.utilities.nonjdk.store.complexwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanCustomField
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfoI

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.logging.Logger

@CompileStatic
class JavaBeanStore extends StoreComplex<JavaBean> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    JavaBeanStore(JavaBean configClass) {
        super(configClass.getClass())
    }

    JavaBeanStore(Class configClass) {
        super(configClass)
    }

    static String save3(JavaBean javaBean) {
        return new JavaBeanStore(javaBean).saveComplexObject(javaBean)
    }

    public boolean writeNullField = false


    @Override
    String saveComplexObject(JavaBean javaBean) throws Exception {
        writer7Sub.body.addAll save(javaBean)
        onBodyCreated()
        return writer7Sub.buildResult()
    }

    boolean isWriteField(Field field1) {
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

    List<Field> getFields(JavaBean javaBean) {
        List<Field> fields = JrrClassUtils.getDeclaredFields(javaBean.getClass()).toList()
        return fields
    }

    List<LineInfoI> save(JavaBean javaBean) {
        //writer3Importer.addImport(javaBean.getClass())
        List<Field> fields = getFields(javaBean)
        fields = fields.findAll { isWriteField(it) }
        int counttt = -1;
        List<LineInfoI> resuult2 = []
        fields.each {
            counttt++
            try {
                resuult2.add saveOneEl(it, writer7Sub.varNameThis, javaBean, writeNullField);
            } catch (Throwable e) {
                log.info "failed write field ${it} : ${e}"
                onFailedWriteEl(it, counttt, e)
            }
        }
        resuult2 = resuult2.findAll { it != null }
        return resuult2;
    }

    LineInfoI saveOneEl(Field f, String beanVarName, JavaBean javaBean, boolean writeNull) {
        String fieldName = f.getName()
        f.setAccessible(true)
        Object fieldValue = f.get(javaBean)
        if (fieldValue == null) {
            if (!writeNull) {
                return null;
            }
        }
        String serValue
        if (javaBean instanceof JavaBeanCustomField) {
            JavaBeanCustomField javaBean2CustomField = (JavaBeanCustomField) javaBean;
            serValue = javaBean2CustomField.customizeField(writer3Importer,objectWriter, f, fieldValue)
        }else {
            serValue = objectWriter.writeObject(writer3Importer, fieldValue)
        }
        return new LineInfo(Brakets.netural,"${beanVarName}.${fieldName} = ${serValue} ${semiColumn}")
    }

}
