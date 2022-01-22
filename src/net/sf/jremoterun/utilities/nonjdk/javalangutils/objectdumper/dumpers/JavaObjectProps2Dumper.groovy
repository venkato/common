package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider

import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.logging.Logger

@CompileStatic
class JavaObjectProps2Dumper implements ObjectDumperI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static HashSet<String> ignoreFieldsGroovy = new HashSet<>(['metaClass', 'class'])
    public HashSet<String> ignoreFields = new HashSet<>(ignoreFieldsGroovy)
    public boolean acceptStaticFields = false

    JavaObjectProps2Dumper() {
    }

    JavaObjectProps2Dumper(Collection<String> ignoreFields) {
        this.ignoreFields = new HashSet<>(ignoreFields)
    }

    public boolean makeAllAccessible = false;

    List<PropertyDescriptor> fetchFields(Class clazz) {
//        JrrClassUtils.getFieldValue(cl,'')
        final PropertyDescriptor[] propertyDescriptors2 = Introspector
                .getBeanInfo(clazz, Introspector.IGNORE_ALL_BEANINFO)
                .getPropertyDescriptors();

        List<PropertyDescriptor> propertyDescriptors1 = [];

        for (final PropertyDescriptor propertyDescriptor : propertyDescriptors2) {
            final Method readMethod = propertyDescriptor.getReadMethod();
            if (readMethod == null) {
                // propertyDescriptors.remove(propertyDescriptor);
            } else {
                if (makeAllAccessible) {
                    if (propertyDescriptor.getReadMethod() != null) {
                        propertyDescriptor.getReadMethod().setAccessible(
                                true);

                    }
                }
                propertyDescriptors1.add(propertyDescriptor)
            }

        }
        propertyDescriptors1 = propertyDescriptors1.findAll { isNeedField(it) }
        return propertyDescriptors1
    }

    boolean isNeedField(PropertyDescriptor f) {
        int modifiers = f.getReadMethod().getModifiers()
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
    Map<String, Object> dumpObject(Object obj, ObjectDumperProvider dumperProvider) {
        if (obj == null) {
            return null
        }
        if (obj.getClass() == Class) {
            throw new IllegalStateException("${obj}")
        }
        Map<String, Object> r = [:]
        fetchFields(obj.getClass()).each {
            String name1 = it.getName()
            Object value3 = it.getReadMethod().invoke(obj)
            if (value3 == null) {
                if (dumperProvider.writeNullO) {
                    r.put(name1, dumperProvider.nullObject)
                }
            } else {
                try {
                    r.put(name1, dumperProvider.dumpObject(value3))
                } catch (Throwable e) {
                    log.info "failed dump ${name1}"
                    throw e
                }
            }

        }
        r = r.sort()
        return r;
    }


}
