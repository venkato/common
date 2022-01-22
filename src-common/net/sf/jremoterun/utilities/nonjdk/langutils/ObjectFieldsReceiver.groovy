package net.sf.jremoterun.utilities.nonjdk.langutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField

import java.lang.annotation.Annotation
import java.lang.reflect.Field
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

@CompileStatic
class ObjectFieldsReceiver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public static HashSet<String> ignoreFieldsGroovy = new HashSet<>()
    public boolean setAccesible = true
    public boolean acceptStaticFields = false
    public boolean receiveFieldsOfParentClasses = true
    public HashSet<String> acceptFields = new HashSet<>()
    public HashSet<String> ignoreFields = new HashSet<>(ignoreFieldsGroovy)
    public List<? extends Class> ignoreTypes = [MetaClass]
    public List<? extends Class> ignoreDeclaratedClass = []
    public Class<? extends Annotation> ignoreAnnotation1 = IgnoreField;
    public Class<? extends Annotation> ignoreAnnotation2;

    void addIgnoreFields(Collection<FieldRef> ignoreFields1) {
        ignoreFields.addAll(ignoreFields1.collect { it.fieldName })
    }

    void addAcceptedFields(Collection<FieldRef> ignoreFields1) {
        acceptFields.addAll(ignoreFields1.collect { it.fieldName })
    }


    List<Field> getFieldsFilteredPublicObj1(Object javaBean) {
        return getFieldsFilteredPublicClass1(javaBean.getClass())
    }

    List<Field> getFieldsFilteredPublicClass1(Class clazz) {
        return getFieldsFiltered1(clazz)
    }

    List<Field> getFieldsFiltered1(Class clazz) {
        List<Field> fields = getFieldsImpl(clazz)
        fields = fields.findAll { isWriteField(it) }
        return fields
    }

    List<Field> getFields(Object javaBean) {
        return getFieldsImpl(javaBean.getClass())
    }

    public boolean checkNotClass = true

    List<Field> getFieldsImpl(Class javaBean) {
        if(checkNotClass){
            assert javaBean!=Class
        }
        if (receiveFieldsOfParentClasses) {
            List<Field> fields = []
            Class clazz = javaBean
            while (true) {
                if (ignoreDeclaratedClass.isEmpty() || !ignoreDeclaratedClass.contains(clazz)) {
                    fields.addAll(0, JrrClassUtils.getDeclaredFields(clazz).toList())
                }

                clazz = clazz.getSuperclass();
                if (clazz == Object) {
                    break;
                }
                if (clazz == null) {
                    // interface
                    break
                }
            }
            if (setAccesible) {
                fields.each { it.setAccessible(true) }
            }
            return fields
        }
        List<Field> fields = JrrClassUtils.getDeclaredFields(javaBean).toList()
        if (setAccesible) {
            fields.each { it.setAccessible(true) }
        }
        return fields
    }


    boolean isWriteField(Field field1) {
        if (!acceptFields.isEmpty()) {
            if (acceptFields.contains(field1.getName())) {
                return true
            }
        }
        if (!ignoreFields.isEmpty()) {
            if (ignoreFields.contains(field1.getName())) {
                return false
            }
        }
        if (Modifier.isStatic(field1.getModifiers())) {
            if (!acceptStaticFields) {
                return false
            }
        }
        if (ignoreAnnotation1 != null) {
            if (field1.getAnnotation(ignoreAnnotation1) != null) {
                return false
            }
        }
        if (ignoreAnnotation2 != null) {
            if (field1.getAnnotation(ignoreAnnotation2) != null) {
                return false
            }
        }
        if (ignoreTypes.contains(field1.getType())) {
            return false
        }
        return true
    }

}
