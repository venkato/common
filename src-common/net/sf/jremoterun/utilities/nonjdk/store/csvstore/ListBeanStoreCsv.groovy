package net.sf.jremoterun.utilities.nonjdk.store.csvstore

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCountStat
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCountStatI
import net.sf.jremoterun.utilities.nonjdk.langutils.ObjectFieldsReceiver

import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class ListBeanStoreCsv {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ObjectFieldsReceiver objectFieldsReceiver = new ObjectFieldsReceiver()
    public Class beanClass
    public List<Field> fields
    public ObjectWriterCsvI objectWriter = new ObjectWriterCsvImpl()
    public String fieldsSeparator = ','
    public String lineSeparator = '\n'
    public String nullObject = 'null'
    public String comment = '#'
    public boolean writePrefixInfoStat = false
    public boolean doSort = true
    public SimpleDateFormat sdf = new SimpleDateFormat('yyyy-MM-dd HH:mm')
    public LineCountStatI lineCountStat// = new LineCountStat()

    ListBeanStoreCsv(Class beanClass) {
        this.beanClass = beanClass
        fields = objectFieldsReceiver.getFieldsFilteredPublicClass1(beanClass)
    }

    void failedWriteCountedEl(Object el, int countt, Throwable e) {
        throw e
    }

    List<String> writeSuffix(Collection beans) {
        return []
    }

    String buildInfo(Collection beans) {
        String info = comment + " ${sdf.format(new Date())} count=${beans.size()}"
        return info
    }

    List<String> writePrefix(Collection beans) {
        if (writePrefixInfoStat) {
            return [buildInfo(beans)]
        }
        return []
    }

    String writeFieldInfo() {
        return fields.collect { it.getName() }.join(fieldsSeparator)
    }


    String saveList(Collection beans) {
        saveListImpl(beans).join(lineSeparator)
    }

    void logStat(int countt) {
        log.info "written ${countt}"
//        Thread.dumpStack()
    }

    List<String> saveListImpl(Collection beans) {
        List<String> r = []
        int countt = -1
        beans.each {
            countt++
            try {
                r.add(saveOneEl(it))
                if (lineCountStat != null) {
                    if (lineCountStat.isNeedPrintProgress(countt)) {
                        logStat(countt)
                    }
                }
            } catch (Throwable e) {
                log.info "failed write field ${countt} : ${e}"
                failedWriteCountedEl(it, countt, e)
                throw e
            }
        }
        if (doSort) {
            r = r.sort()
        }
        r.add(0, writeFieldInfo())
        List<String> prefix = writePrefix(beans)
        if (!prefix.isEmpty()) {
            r.addAll(0, prefix)
        }
        r.addAll(writeSuffix(beans))
        r = r.findAll { it != null }
        return r;
    }

    String saveOneEl(Object javaBean) {
        assert javaBean.getClass() == beanClass
        int countt = -1
        List<String> resuult2 = fields.collect {
            countt++
            try {
                return writeOneField(it, javaBean)
            } catch (Throwable e) {
                log.info "failed write field ${it.getName()} : ${e}"
                failedWriteCountedEl(it, countt, e)
                throw e
            }
        }
        return resuult2.join(fieldsSeparator)
    }

    Object getFieldValue(Field f, Object javaBean) {
        return f.get(javaBean)
    }

    String writeOneField(Field f, Object javaBean) {
        Object fieldValue = getFieldValue(f, javaBean)
        if (fieldValue == null) {
            return nullObject
        }
        String serValue = objectWriter.writeObject(fieldValue)
        return writeProp(f, serValue, javaBean)
    }

    String writeProp(Field fieldName, String serValue, Object javaBean) {
        return serValue
    }


}
