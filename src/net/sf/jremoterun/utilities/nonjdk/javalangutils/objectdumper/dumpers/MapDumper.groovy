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
            Object key1 = generateUniqKey(dumperProvider.dumpObject(it.key),res)
            try {
                Object value1 = dumperProvider.dumpObject(it.value)
                res.put(key1, value1)
            } catch (Throwable e) {
                log.info "failed dump ${key1}"
                throw e
            }
        }
        if(res.size()==0){
            return dumperProvider.emptyMap;
        }
        if (dumperProvider.sortList) {
            res.sort(new ObjectToStringComparator())
        }
        return res;
    }


    Object generateUniqKey(Object key,Map res){
        int suffix=1
        Object key2=key
        while (true) {
            if (!res.containsKey(key2)) {
                return key2
            }
            suffix++
            key2="${key}-${suffix}"
        }
    }

}
