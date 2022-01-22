package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.DateWithFormat
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3

import java.util.logging.Logger

@CompileStatic
class MapSimpleWriter implements CustomWriter<Map> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    String save(Writer3 writer3, ObjectWriter objectWriter, Map map) {
        return save2(writer3, objectWriter, (Map) map)
    }

    String save2(Writer3 writer3, ObjectWriter objectWriter, Map<String, Object> map) {
        if (map.size() == 0) {
            return '[:]'
        }
//        Map<Integer,Integer> aa= [(1):2,(2):2]
        Set<Map.Entry<String, Object>> entries = map.entrySet()
        String join = entries.collect {
            String key = writeMapKey(writer3, map, it.key, objectWriter)
            try {
                String value = writeMapValue(writer3, map, it.value, objectWriter)
                return "(${key}) : ${value} ".toString()
            } catch (Exception e) {
                log.info("Failed write key ${key} from map : ${e}")
                throw e
            }
        }.join(',\n')
        return '[' + join + ']'
    }


    String writeMapKey(Writer3 writer3, Map<String, Object> map, Object key, ObjectWriter objectWriter) {
        return objectWriter.writeObject(writer3, key)
    }


    String writeMapValue(Writer3 writer3, Map<String, Object> map, Object value, ObjectWriter objectWriter) {
        return objectWriter.writeObject(writer3, value)
    }


    @Override
    Class<Map> getDataClass() {
        return Map
    }
}
