package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.CustomFieldDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.IgnoreFieldDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField

import java.lang.reflect.Field
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

@CompileStatic
class JavaObjectFieldsDumper implements ObjectDumperI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static HashSet<String> ignoreFieldsGroovy = new HashSet<>(['metaClass'])
    public HashSet<String> ignoreFields = new HashSet<>(ignoreFieldsGroovy)
    public boolean acceptStaticFields = false
    public Map<String, ObjectDumperI> customFieldDumpers = [:]

    JavaObjectFieldsDumper() {
    }

    JavaObjectFieldsDumper(Collection<FieldRef> ignoreFields) {
        this.ignoreFields = new HashSet<>(ignoreFields.collect { it.fieldName })
    }

    List<Field> fetchFields(Class cl) {
        List<Field> list1 = []
//        JrrClassUtils.getFieldValue(cl,'')
        net.sf.jremoterun.utilities.JrrClassUtils2.fieldsIterator(cl) {
            list1.add(it)
            return false
        }
        list1 = list1.findAll { isNeedField(it) }
        return list1
    }

    boolean isNeedField(Field f) {
        int modifiers = f.getModifiers()
        if (!acceptStaticFields) {
            if (Modifier.isStatic(modifiers)) {
                return false
            }
        }
        if (ignoreFields.contains(f.getName())) {
            return false
        }
        if (f.getAnnotation(IgnoreFieldDumper) != null) {
            return false
        }
        return true
    }

    @Override
    Object dumpObject(Object obj, ObjectDumperProvider dumperProvider) {

        if (obj == null) {
            return null
        }
        if (obj.getClass() == Class) {
            throw new IllegalStateException("${obj}")
        }
        Map<String, Object> r = [:]
        fetchFields(obj.getClass()).each {
            handleField(obj,it,dumperProvider,r)
        }
        r = r.sort()
        return r;
    }

    void handleField(Object obj, Field field1, ObjectDumperProvider dumperProvider,Map<String, Object> r ){
        String name1 = field1.getName()
        Object value3 = field1.get(obj)
        if (value3 == null) {
            if (isWriteNull(field1,dumperProvider)) {
                r.put(name1, dumperProvider.nullObject)
            }
        } else {
            try {
                r.put(name1, convertValueToStr(obj, field1, dumperProvider, value3))
            } catch (Throwable e) {
                log.info "failed dump ${name1}"
                throw e
            }
        }

    }

    boolean isWriteNull(Field field, ObjectDumperProvider dumperProvider){
        return dumperProvider.writeNullO
    }


    Object convertValueToStr(Object obj, Field field1, ObjectDumperProvider dumperProvider, Object value3) {
        if(value3!=null) {
            ObjectDumperI objectDumperCustom1 = customFieldDumpers.get(field1.getName())
            if (objectDumperCustom1 != null) {
                return objectDumperCustom1.dumpObject(value3, dumperProvider)
            }
        }
        if (obj instanceof CustomFieldDumper) {
            CustomFieldDumper customFieldDumper = (CustomFieldDumper) obj;
            return customFieldDumper.dumpField(field1.getName(),dumperProvider,value3)
        }
        return dumperProvider.dumpObject(value3)
    }


}
