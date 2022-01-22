package net.sf.jremoterun.utilities.nonjdk.store.csvstore

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import
import net.sf.jremoterun.utilities.nonjdk.store.customwriters.CustomWriter;

import java.util.logging.Logger;

@CompileStatic
class ObjectWriterCsvImpl implements ObjectWriterCsvI {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Map<String, ObjectWriterCsvI> customsS = [:]
    public Map<String, ObjectWriterCsvI> customsCurrent = new HashMap<>(customsS)

    @Override
    String writeObject(Object obj) {
        String classNameFull = obj.getClass().getName()
        if (obj != null) {
            ObjectWriterCsvI customWriter = customsCurrent.get(classNameFull)
            if (customWriter != null) {
                return useCustomWriter(customWriter, obj)
            }
        }
        return obj.toString()
    }

    String useCustomWriter(ObjectWriterCsvI customWriter, Object obj) {
        return customWriter.writeObject(obj)
    }



    static void addAddCustomWriter(Class clazz,ObjectWriterCsvI cw) {
        customsS.put(clazz.getName(), cw)
    }

    void addAddCustomWriter2(Class clazz,ObjectWriterCsvI cw) {
        customsCurrent.put(clazz.getName(), cw)
    }

}
