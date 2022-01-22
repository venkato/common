package net.sf.jremoterun.utilities.nonjdk.store.csvstore

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCountStat
import net.sf.jremoterun.utilities.nonjdk.cvsutils.LineCountStatI
import net.sf.jremoterun.utilities.nonjdk.langutils.ObjectFieldsReceiver

import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.logging.Logger

@CompileStatic
class ListBeanReaderCsv {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public ObjectFieldsReceiver objectFieldsReceiver = new ObjectFieldsReceiver()
    public Class beanClass
    public List<Field> fields
    public StringToObjectConverter objectReader = net.sf.jremoterun.utilities.groovystarter.st.str2obj.StringToObjectConverter.defaultConverter
    public String fieldsSeparator = ','
    public String lineSeparator = ','
    public String nullObject = 'null'
    public String comment = '#'
    public LineCountStatI lineCountStat// = new LineCountStat()

    ListBeanReaderCsv(Class beanClass) {
        this.beanClass = beanClass
        fields = objectFieldsReceiver.getFieldsFilteredPublicClass1(beanClass)
    }

    void failedWriteCountedEl(Object el, int countt, Throwable e) {
        throw e
    }

    void logStat(int countt) {
        log.info "written ${countt}"
    }

    boolean isGoodLine(String line) {
        if (comment == null) {
            return true
        }
        if (line.startsWith(comment)) {
            return false
        }
        return true
    }

    boolean readHeader = true

    void handleHeader(List<String> headers) {
        List<Field> fieldsAfterRead = []
        Map<String, Field> fieldMap = [:]
        fields.each {
            fieldMap.put(it.getName(), it)
        }

        List<String> missingFields = []

        headers.each {
            Field field1 = fieldMap.remove(it)
            if (field1 == null) {
                missingFields.add(it)
            } else {
                fieldsAfterRead.add(field1)
            }
        }

        if (!missingFields.isEmpty()) {
            onMissingFields(missingFields)
        }

        Collection<Field> notFoundFields = fieldMap.values().toList()
        if (!notFoundFields.isEmpty()) {
            onNotFoundFields(notFoundFields)
        }
        fields = fieldsAfterRead
    }

    void onNotFoundFields(List<Field> notFoundFields) {
        throw new Exception(notFoundFields.join(','))
    }

    void onMissingFields(List<String> missingFields) {
        throw new Exception(missingFields.join(','))
    }


    List readList(List<String> beans) {
        beans = beans.findAll { isGoodLine(it) }
        if (readHeader) {
            handleHeader(beans.remove(0).tokenize(fieldsSeparator))
        }
        List r = []
        int countt = -1
        beans.each {
            countt++
            try {
                r.add readOneEl(it)
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
        return r;
    }

    Object createNewJaveBean() {
        beanClass.newInstance()
    }

    Object readOneEl(String line) {
        Object javaBean = createNewJaveBean()
        List<String> tokenize = line.tokenize(fieldsSeparator)
        int countt = -1
        fields.each {
            countt++
            try {
                readOneField(it, tokenize[countt], javaBean)
            } catch (Throwable e) {
                log.info "failed write field ${it.getName()} : ${e}"
                failedWriteCountedEl(it, countt, e)
                throw e
            }
        }
        return javaBean
    }

    void readOneField(Field f, String valueS, Object javaBean) {
        Object fieldValue
        if (fieldValue == nullObject) {
        } else {
            fieldValue = objectReader.convertFromStringToType(valueS, f.getType(), f.getGenericType())
        }
        writeProp(f, fieldValue, javaBean)
    }

    void writeProp(Field fieldName, Object serValue, Object javaBean) {
        fieldName.set(javaBean, serValue)
    }


}
