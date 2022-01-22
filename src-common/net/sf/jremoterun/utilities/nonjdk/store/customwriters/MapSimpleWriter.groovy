package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.Writer3Import

import java.util.logging.Logger

@CompileStatic
class MapSimpleWriter implements CustomWriter<Map> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    String save(Writer3Import writer3, ObjectWriter objectWriter, Map map) {
        return save2(writer3, objectWriter, (Map) map)
    }

    String save2(Writer3Import writer3, ObjectWriter objectWriter, Map<String, Object> map) {
        if (map.size() == 0) {
            return '[:]'
        }
//        Map<Integer,Integer> aa= [(1):2,(2):2]
        Set<Map.Entry<String, Object>> entries = map.entrySet()
        List<String> els = entries.collect {
            writeEntry(writer3, objectWriter, it, map)
        }
        return writeEls(els)
    }

    public String elsSeparator = ',\n'

    String writeEls(List<String> els) {
        return '[' + els.join(elsSeparator) + ']'
    }

    String writeEntry(Writer3Import writer3, ObjectWriter objectWriter, Map.Entry<String, Object> entry1, Map<String, Object> map) {
        String key
        try {
            key = writeMapKey(writer3, map, entry1.key, objectWriter)
        } catch (Throwable e) {
            log.info("Failed write key ${entry1.key} from map : ${e}")
            objectWriter.failedWriteCountedEl(entry1, -1, e)
            throw e
        }
        try {
            String value = writeMapValue(writer3, map, entry1.value, objectWriter)
            return buildEntry(key, value)
        } catch (Throwable e) {
            log.info("Failed write value for key ${key} from map : ${e}")
            objectWriter.failedWriteCountedEl(entry1, -1, e)
            throw e
        }
    }

    String buildEntry(String key, String value) {
        return "(${key}) : ${value} "
    }


    String writeMapKey(Writer3Import writer3, Map<String, Object> map, Object key, ObjectWriter objectWriter) {
        return objectWriter.writeObject(writer3, key)
    }


    String writeMapValue(Writer3Import writer3, Map<String, Object> map, Object value, ObjectWriter objectWriter) {
        return objectWriter.writeObject(writer3, value)
    }


    @Override
    Class<Map> getDataClass() {
        return Map
    }
}
