package net.sf.jremoterun.utilities.nonjdk.jmx

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MbeanConnectionCreator

import javax.management.ObjectName
import javax.management.openmbean.CompositeData
import java.beans.IntrospectionException
import java.lang.management.MemoryType
import java.lang.management.PlatformManagedObject
import java.lang.reflect.Method
import java.lang.reflect.Modifier;
import java.util.logging.Logger;

@CompileStatic
class MBeanClientCompositeDataSupport extends MBeanClient {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Map<Class, Method> compositeDataMethodsMap = [:];

    MBeanClientCompositeDataSupport(Class class1, MbeanConnectionCreator mbeanConnectionCreator, ObjectName objectName) throws IntrospectionException {
        super(class1, mbeanConnectionCreator, objectName)
    }

    @Override
    Object invoke2(Object beanObject, Method methodToInvoke, Method superMethod, Object[] args) throws Throwable {
        // superMethod ref is null ?
//        log.info "dd = ${getters.collect {it.key.getName()}}"
        Object res = super.invoke2(beanObject, methodToInvoke, superMethod, args)
        if (res instanceof String && methodToInvoke.getReturnType() == MemoryType) {
            res =MemoryType.valueOf(res);
        }else if (res instanceof CompositeData) {
            CompositeData compositeDataR = (CompositeData) res;
            Class<?> returnType = methodToInvoke.getReturnType();
            Method method1 = compositeDataMethodsMap.get(returnType)
            if (method1 == null) {
                method1 = returnType.getMethod('from', CompositeData)
                method1.setAccessible(true)
                assert Modifier.isStatic(method1.getModifiers())
                res = method1.invoke(null, compositeDataR)
                compositeDataMethodsMap.put(returnType, method1);
            } else {
                res = method1.invoke(null, compositeDataR)
            }
        }
        return res;
    }

    public static <T extends PlatformManagedObject> T buildMbeanClientUseDefaultObjectName2(final T javaBean,
                                                                                            MbeanConnectionCreator mbeanConnectionCreator) {
        Class<?>[] interfaces = javaBean.getClass().getInterfaces();
        assert interfaces.length>0
        return (T) buildMbeanClient2((Class)interfaces[0], mbeanConnectionCreator, javaBean.getObjectName());
    }

    static <T> T buildMbeanClient2(final Class<T> clazz, MbeanConnectionCreator mbeanConnectionCreator,
                                   ObjectName objectName) {
        return (T) new MBeanClientCompositeDataSupport(clazz, mbeanConnectionCreator, objectName).createInstance();

    }
}
