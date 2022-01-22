package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger

@CompileStatic
class MapSimpleWriter<K, V> implements CustomWriter<Map<K, V>> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public String emptyMap = '[:]'
    public String elsSeparator = ',\n'

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, Map<K, V> map) {
        if (map.isEmpty()) {
            return emptyMap
        }
//        Map<Integer,Integer> aa= [(1):2,(2):2]
        Set<Map.Entry<K, V>> entries = map.entrySet()
        int countt = -1
        List<String> els = entries.collect {
            countt++
            writeEntry(writer3, objectWriter, it, map, countt)
        }
        return writeEls(els)
    }


    String writeEls(List<String> els) {
        return '[' + els.join(elsSeparator) + ']'
    }

    String writeEntry(Writer3Import writer3, ObjectWriterI objectWriter, Map.Entry<K, V> entry1, Map<K, V> map1, int countt) {
        String key
        try {
            key = writeMapKey(writer3, objectWriter, map1, entry1.key)
        } catch (Throwable e) {
            log.info("Failed write key ${entry1.key} from map1 : ${e}")
            objectWriter.failedWriteCountedEl(entry1, countt, e)
            throw e
        }
        try {
            String value = writeMapValue(writer3, objectWriter, map1, entry1.value)
            return buildEntry(key, value)
        } catch (Throwable e) {
            log.info("Failed write value for key ${key} value = ${entry1.value} : ${e}")
            objectWriter.failedWriteCountedEl(entry1, countt, e)
            throw e
        }
    }

    String buildEntry(String key, String value) {
        return "(${key}) : ${value} "
    }


    String writeMapKey(Writer3Import writer3, ObjectWriterI objectWriter, Map map, K key) {
        return objectWriter.writeObject(writer3, key)
    }


    String writeMapValue(Writer3Import writer3, ObjectWriterI objectWriter, Map map, V value) {
        return objectWriter.writeObject(writer3, value)
    }


    @Override
    Class<Map> getDataClass() {
        return Map
    }
}
