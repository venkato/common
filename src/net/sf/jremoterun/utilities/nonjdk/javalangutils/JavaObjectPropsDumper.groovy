package net.sf.jremoterun.utilities.nonjdk.javalangutils

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider

import java.lang.reflect.Field
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

@CompileStatic
class JavaObjectPropsDumper implements ObjectDumperI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static HashSet<String> ignoreFieldsGroovy = new HashSet<>(['metaClass'])
    public HashSet<String> ignoreFields = new HashSet<>(ignoreFieldsGroovy)
    public boolean acceptStaticFields = false

    JavaObjectPropsDumper() {
    }

    JavaObjectPropsDumper(Collection<FieldRef> ignoreFields) {
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
            String name1 = it.getName()
//            String fieldStrr = fetchField(obj, it, dumperProvider)
            //r.put(name1, fieldStrr)
            Object value3 = it.get(obj)
            if (value3 == null) {
                if (dumperProvider.writeNullO) {
                    r.put(name1, dumperProvider.nullObject)
                }
            } else {
                try {
                    r.put(name1, convertValueToStr(obj, it, dumperProvider, value3))
                } catch (Throwable e) {
                    log.info "failed dump ${name1}"
                    throw e
                }
            }
        }
        r = r.sort()
        return r;
    }


    String convertValueToStr(Object obj, Field field1, ObjectDumperProvider dumperProvider, Object value3) {
        return dumperProvider.dumpObject(value3)
    }


}
