package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.FieldRef
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.CustomFieldDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.IgnoreFieldDumper
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider
import net.sf.jremoterun.utilities.nonjdk.store.IgnoreField

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
    public Map<String, ObjectDumperI> customFieldDumpers = [:]

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
        if (f.getReadMethod().getAnnotation(IgnoreFieldDumper) != null) {
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
           handleField(obj,it,dumperProvider,r)
        }
        r = r.sort()
        return r;
    }



    void handleField(Object obj, PropertyDescriptor propertyDescriptor1, ObjectDumperProvider dumperProvider,Map<String, Object> r ){
        String name1 = propertyDescriptor1.getName()
        Object value3 = propertyDescriptor1.getReadMethod().invoke(obj)
        if (value3 == null) {
            if (isWriteNull(propertyDescriptor1,dumperProvider)) {
                r.put(name1, dumperProvider.nullObject)
            }
        } else {
            try {
                r.put(name1, convertValueToStr(obj, propertyDescriptor1, dumperProvider, value3))
            } catch (Throwable e) {
                log.info "failed dump ${name1}"
                throw e
            }
        }

    }

    boolean isWriteNull(PropertyDescriptor field, ObjectDumperProvider dumperProvider){
        return dumperProvider.writeNullO
    }



    Object convertValueToStr(Object obj, PropertyDescriptor field1, ObjectDumperProvider dumperProvider, Object value3) {
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
