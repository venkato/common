package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.ClasspathConfigurator

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.util.logging.Logger

@CompileStatic
class JavaBeanStore {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

//    public static String varFieldName ='b'


    Writer6Sub createWriter(JavaBean javaBean){
        return new Writer7Sub(javaBean.getClass())
    }

    ObjectWriter createObjectWriter(){
        return new ObjectWriter()
    }

    static String save3(JavaBean javaBean) {
        return new JavaBeanStore().saveS(javaBean)
    }

    String saveS(JavaBean javaBean) {
        Writer6Sub writer3 = createWriter(javaBean)
        ObjectWriter objectWriter = createObjectWriter()
        Class beanClass = javaBean.class;
        writer3.addImport(beanClass)
//        writer3.body.add "${beanClass.simpleName} b = ${writer3.generateGetProperty(ClasspathConfigurator.varName)} as ${beanClass.simpleName};" as String
        if (javaBean instanceof JavaBeanCustomSaver) {
            JavaBeanCustomSaver customSaver = (JavaBeanCustomSaver) javaBean;
            writer3.body.addAll customSaver.save(Writer6Sub.varName, writer3, objectWriter)
        }else{
            writer3.body.addAll save(Writer6Sub.varName, javaBean, writer3, objectWriter,false)
        }
        return writer3.buildResult()
    }

    static List<String> save(String beanVarName, JavaBean javaBean, Writer3 writer3, ObjectWriter objectWriter,boolean writeNull) {
        writer3.addImport(javaBean.getClass())
        StringBuilder sb = new StringBuilder();
        List<Field> fields = javaBean.class.getDeclaredFields().toList()
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
                            result = "${beanVarName}.${fieldName} = ${serValue} ;"
                        }
                }
                return result
            }catch (Throwable e){
                log.info "failed write field ${fieldName} : ${e}"
                throw e
            }
        }
        resuult2 = resuult2.findAll {it!=null}
        return resuult2;
    }

}
