package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.dumpers

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperI
import net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper.ObjectDumperProvider
import org.apache.commons.lang3.compare.ObjectToStringComparator

import java.util.logging.Logger

@CompileStatic
class MapDumper implements ObjectDumperI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    @Override
    Object dumpObject(Object obj, ObjectDumperProvider dumperProvider) {
        Map aa = obj as Map
        Map res = [:]
        aa.each {
            dumpEl(it, res, dumperProvider)
        }
        if (res.size() == 0) {
            return dumperProvider.emptyMap;
        }
        if (dumperProvider.sortList) {
            res.sort(new ObjectToStringComparator())
        }
        return res;
    }

    void dumpEl(Map.Entry it, Map res, ObjectDumperProvider dumperProvider) {
        def key444 = it.key
        try {
            Object key1 = generateUniqKey(dumperProvider.dumpObject(key444), res)
            Object value1 = dumperProvider.dumpObject(it.value)
            res.put(key1, value1)
        } catch (Throwable e) {
            try {
                log.info "failed dump ${key444}"
            } catch (Throwable e2) {
                log.warn("${key444.getClass().getName()}", e2)
            }
            throw e
        }
    }


    Object generateUniqKey(Object key, Map res) {
        int suffix = 1
        Object key2 = key
        while (true) {
            if (!res.containsKey(key2)) {
                return key2
            }
            suffix++
            key2 = "${key}-${suffix}"
        }
    }

}
