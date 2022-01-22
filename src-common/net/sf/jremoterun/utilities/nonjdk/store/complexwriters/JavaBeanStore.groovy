package net.sf.jremoterun.utilities.nonjdk.store.complexwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField
import net.sf.jremoterun.utilities.nonjdk.store.JavaBean
import net.sf.jremoterun.utilities.nonjdk.store.StoreComplex

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
//    public static String varFieldName ='b'


//    Writer6Sub createWriter(JavaBean javaBean) {
//        return new Writer7Sub(javaBean.getClass())
//    }

//    ObjectWriter createObjectWriter() {
//        return new ObjectWriter()
//    }


    static String save3(JavaBean javaBean) {
        return new JavaBeanStore(javaBean).saveComplexObject(javaBean)
    }

    public boolean writeNullField = false

//    @Deprecated
//    String saveAll(JavaBean javaBean) {
//        return saveComplexObject(javaBean)
//    }


    @Override
    String saveComplexObject(JavaBean javaBean) throws Exception {
        //Writer6Sub writer3 = writer7Sub;//createWriter(javaBean)
        //ObjectWriter objectWriter = objcreateObjectWriter()
        //Class beanClass = javaBean.class;
        //writer3.addImport(javaBean.class)
//        writer3.body.add "${beanClass.simpleName} b = ${writer3.generateGetProperty(ClasspathConfigurator.varName)} as ${beanClass.simpleName};" as String
//        if (javaBean instanceof JavaBeanCustomSaver) {
//            JavaBeanCustomSaver customSaver = (JavaBeanCustomSaver) javaBean;
//            writer7Sub.body.addAll customSaver.save(writer7Sub.varNameThis, writer7Sub, objectWriter)
//        } else {
        writer7Sub.body.addAll save(javaBean)
//        }
        onBodyCreated()
        return writer7Sub.buildResult()
    }

    boolean isWriteField(Field it) {
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

    List<Field> getFields(JavaBean javaBean) {
        List<Field> fields = JrrClassUtils.getDeclaredFields(javaBean.getClass()).toList()
        return fields
    }

    List<String> save(JavaBean javaBean) {
        writer3Importer.addImport(javaBean.getClass())
        List<Field> fields = getFields(javaBean)
        fields = fields.findAll { isWriteField(it) }
        int counttt = -1;
        List<String> resuult2 = []
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

    String saveOneEl(Field f, String beanVarName, JavaBean javaBean, boolean writeNull) {
        //Field f = it as Field;
        String fieldName = f.getName()
        //String result

        f.setAccessible(true)
        Object fieldValue = f.get(javaBean)
        if (fieldValue == null) {
            if (!writeNull) {
                return null;
            }
        }
        String serValue = objectWriter.writeObject(writer3Importer, fieldValue)
        return "${beanVarName}.${fieldName} = ${serValue} ${semiColumn}"

        //return result

    }

}
