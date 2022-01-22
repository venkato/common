package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3

import java.util.logging.Logger

@CompileStatic
class ListSimpleWriter implements CustomWriter<List> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int newLineSize = 80;

    @Override
    String save(Writer3 writer3, ObjectWriter objectWriter, List list) {
        return save2(writer3, objectWriter, list)
    }

    String save2(Writer3 writer3, ObjectWriter objectWriter, List list) {
        if (list.size() == 0) {
            return '[]'
        }
        List<String> asList = convertToString(writer3,objectWriter,list)
        return writeEls(asList)
    }

    List<String> convertToString(Writer3 writer3, ObjectWriter objectWriter, List list){
        int counttt = -1;
        List<String> asList = list.collect {
            try {
                counttt++
                return writeEl(writer3, objectWriter, it)
            } catch (Exception e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                throw e
            }
        }
        return asList
    }

    String writeEls(List<String> asList ){
        String sep = ', '
        int size = asList.size()
        if (size > 0 && newLineSize > 0) {
            int totalCount = 0
            asList.each {
                totalCount += it.length()
            }
            int avgSize = totalCount / size as int
            if (avgSize > newLineSize) {
                sep = ', \n'
            }
        }
        return ' [ ' + asList.join(sep) + ' ] '
    }

    String writeEl(Writer3 writer3, ObjectWriter objectWriter, Object el) {
        return objectWriter.writeObject(writer3, el)
    }


    @Override
    Class<List> getDataClass() {
        return List
    }
}
