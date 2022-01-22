package net.sf.jremoterun.utilities.nonjdk.store.complexwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo

import java.util.logging.Logger

@CompileStatic
class MapStore2<K, V> extends StoreComplex<Map<K, V>> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();
    public Class<K> templateKeyClass
    public Class<V> templateValueClass

    MapStore2() {
        this(Map)
    }

    MapStore2(Class configClass) {
        super(configClass)
    }

    @Override
    String writeTemplate(Class configClass2) {
        if (templateKeyClass == null) {
            return super.writeTemplate(configClass2)
        }
        if (templateValueClass == null) {
            writer3Importer.addImport(templateKeyClass)
            return "${configClass2.getSimpleName()}<${templateKeyClass.getSimpleName()},?>"
        }
        writer3Importer.addImport(templateKeyClass)
        writer3Importer.addImport(templateValueClass)
        return "${configClass2.getSimpleName()}<${templateKeyClass.getSimpleName()},${templateValueClass.getSimpleName()}>"
    }

    @Override
    String saveComplexObject(Map<K, V> files) throws Exception {
        assert files != null
        int counttt = -1;
        files.each {
            try {
                counttt++
                writer7Sub.body.add new LineInfo(Brakets.netural, writeEl(it))
            } catch (Throwable e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                onFailedWriteEl(it, counttt, e)
            }
        }
        onBodyCreated()
        return buildResult()
    }

    String writeEl(Map.Entry<K, V> el) {
        String key1 = objectWriter.writeObject(writer3Importer, el.getKey())
        String value1 = objectWriter.writeObject(writer3Importer, el.getValue())
        return writeEL2(key1, value1)
    }

    String writeEL2(String key1, String value1) {
        return "${writer7Sub.varNameThis}.put ( ${key1} , ${value1} ) ${semiColumn}"
    }


    static Map loadSettingsS(File file) {
        if (file.exists()) {
            Map map1 = [:]
            GroovyConfigLoaderJrr.configLoader.parseConfig(file).loadConfig(map1)
            return map1
        }
        return [:]
    }

}
