package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger

@CompileStatic
class ListSimpleWriter<T> implements CustomWriter<List<T>> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public int newLineSize = 80;
    public String emptyList = '[]';
    public String separator = ', '
    public String separatorNewLine = ', \n'

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, List<T> list) {
        if (list.isEmpty()) {
            return emptyList
        }
        List<String> asList = convertToString(writer3, objectWriter, list)
        return writeEls(asList)
    }

    List<String> convertToString(Writer3Import writer3, ObjectWriterI objectWriter, List<T> list) {
        int counttt = -1;
        List<String> asList = list.collect {
            try {
                counttt++
                return writeEl(writer3, objectWriter, it)
            } catch (Throwable e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                objectWriter.failedWriteCountedEl(it, counttt, e)
                throw e
            }
        }
        return asList
    }

    String writeEls(List<String> asList) {
        String sep = separator
        int size = asList.size()
        if (size > 0 && newLineSize > 0) {
            int totalCount = 0
            asList.each {
                totalCount += it.length()
            }
            int avgSize = totalCount / size as int
            if (avgSize > newLineSize) {
                sep = separatorNewLine
            }
        }
        return ' [ ' + asList.join(sep) + ' ] '
    }

    String writeEl(Writer3Import writer3, ObjectWriterI objectWriter, T el) {
        return objectWriter.writeObject(writer3, el)
    }


    @Override
    Class<List> getDataClass() {
        return List
    }
}
