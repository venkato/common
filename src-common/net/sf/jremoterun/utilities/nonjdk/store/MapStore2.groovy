package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.util.logging.Logger

@CompileStatic
class MapStore2<K, V> extends StoreComplex {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    MapStore2() {
        this(Map)
    }

    MapStore2(Class configClass) {
        super(configClass)
    }

    String save3(Map<K, V> files) throws Exception {
        assert files != null
        int counttt = -1;
        writer7Sub.body.addAll(files.collect {
            try {
                counttt++
                return writeEl(it)
            } catch (Exception e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                throw e
            }
        })
        return buildResult()
    }

    String writeEl(Map.Entry<K, V> el) {
        String key1 = objectWriter.writeObject(writer7Sub, el.getKey())
        String value1 = objectWriter.writeObject(writer7Sub, el.getValue())
        return writeEL2(key1, value1)
    }

    String writeEL2(String key1, String value1) {
        return "${writer7Sub.varNameThis}.put ( ${key1} , ${value1} ) ;"
    }


    Map<K,V> loadSettingsS(String scriptSource, String scriptName) {
        GroovyConfigLoader2 configLoader2 = RunnableFactory.groovyClassLoader.parseClass(scriptSource).newInstance()
        Map<K,V> list = [:]
        configLoader2.loadConfig(list)
        return list
    }

}
