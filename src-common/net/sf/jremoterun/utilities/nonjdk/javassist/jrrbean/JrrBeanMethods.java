package net.sf.jremoterun.utilities.nonjdk.javassist.jrrbean;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

import net.sf.jremoterun.utilities.MBeanFromJavaBean;


public class JrrBeanMethods {


    private final Map<String, Object> propertiesInfo;

    public static String objectS = "object";

    public static String methodsMapS = "methodsMap";

    public static String mbeanS = "mbean";

    public static String classS = "class";

    public JrrBeanMethods(final Class clazz) {
        propertiesInfo = JrrBeanMaker.getObjectBeanFieldsMap(clazz);
    }

    public MBeanFromJavaBean getMBean() {
        return (MBeanFromJavaBean) propertiesInfo.get(mbeanS);
    }

    public void setMBean(final MBeanFromJavaBean object) {
        propertiesInfo.put(mbeanS, object);
    }

    public Object getObject() {
        return propertiesInfo.get(objectS);
    }

    public void setObject(final Object object) {
        propertiesInfo.put(objectS, object);
    }

    public Map<ArrayList<String>, Method> getMethodsMap() {
        return (Map) propertiesInfo.get(methodsMapS);
    }

    public void setMethodsMap(final Map methodsMap) {
        propertiesInfo.put(methodsMapS, methodsMap);
    }

    public Class getJavaBeanClass() {
        return (Class) propertiesInfo.get(classS);
    }

    public void setJavaBeanClass(final Class clazz) {
        propertiesInfo.put(classS, clazz);
    }
}
