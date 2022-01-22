package net.sf.jremoterun.utilities.nonjdk.log.tojdk

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogConfigurationException
import org.apache.commons.logging.LogFactory
import org.apache.commons.logging.impl.Jdk14Logger
import org.apache.commons.logging.impl.LogFactoryImpl

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@CompileStatic
public class JavaCommonsLogger extends LogFactory{


    private final ConcurrentMap<String, Object> attributes = new ConcurrentHashMap<>();



    private static final Object[] EMPTY_OBJECT_ARRAY = [];

    private static final String[] EMPTY_STRING_ARRAY = [];

    static void setCommonsLoggerToLog4j2() throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        LogFactory.releaseAll();
        net.sf.jremoterun.utilities.nonjdk.log.tojdk.JavaCommonsLogger log4jLoggerLogFactory = new net.sf.jremoterun.utilities.nonjdk.log.tojdk.JavaCommonsLogger();
        setCommonsLoggerTo(log4jLoggerLogFactory)
    }

    static void setCommonsLoggerTo(LogFactory logFactory) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        LogFactory.releaseAll();
        Log instance = logFactory.getInstance("test");
        JrrClassUtils.setFieldValue(LogFactory, "nullClassLoaderFactory", logFactory);
    }

    static setLoggerProps() {
        List<String> props = [
                LogFactoryImpl.LOG_PROPERTY,
                Log.getName(),
                "org.apache.commons.logging.log",
        ]

        String classname = Jdk14Logger.getName();
        props.each { System.setProperty(it, classname) }
    }


    public void release() {

    }


    @Override
    public void removeAttribute(final String name) {
        attributes.remove(name);
    }

    @Override
    public void setAttribute(final String name, final Object value) {
        if (value != null) {
            attributes.put(name, value);
        } else {
            removeAttribute(name);
        }
    }


    @Override
    public Object getAttribute(final String name) {
        return attributes.get(name);
    }

    @Override
    public String[] getAttributeNames() {
        return attributes.keySet().toArray(EMPTY_STRING_ARRAY);
    }

    @Override
    Log getInstance(Class<?> clazz) throws LogConfigurationException {
        return new Jdk14Logger(clazz.getName())
    }

    @Override
    Log getInstance(String name) throws LogConfigurationException {
        return new Jdk14Logger(name)
    }
}
