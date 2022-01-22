package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.json

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.JavaBeanCommon
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo

import java.util.logging.Logger

@CompileStatic
class ListStore2Json<T> extends StoreComplexJson<List<T>> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public JavaBeanStore2Json javaBeanStore2Json

    ListStore2Json(OutputFormat outputFormat) {
        super(outputFormat)
        if (outputFormat == OutputFormat.json) {
            javaBeanStore2Json = new JavaBeanStore2Json(false, true)

        } else {
            assert outputFormat == OutputFormat.csv
            javaBeanStore2Json = new JavaBeanStore2Json(true, false)
            javaBeanStore2Json.csvSeparator = csvSeparator
        }
    }

    public boolean headerAdded = false
    public String csvSeparator = ','

    String buildHeader(Object sampleObj) {
        return javaBeanStore2Json.objectFieldsReceiver.getFieldsFilteredPublicObj1(sampleObj).collect { it.getName() }.join(csvSeparator)
    }

    @Override
    String saveComplexObject(List<T> list) throws Exception {
        if (outputFormat == OutputFormat.json) {
            writer7Sub.body.add new LineInfo(Brakets.openBacket, '[')
        }
        assert list != null
        int counttt = -1;
        list.each {
            try {
                counttt++
                if (outputFormat == OutputFormat.csv) {
                    if (!headerAdded) {
                        header.add buildHeader(it)
                        headerAdded = true
                    }
                }
                writer7Sub.body.add new LineInfo(Brakets.netural, writeEl(it))
            } catch (Throwable e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                onFailedWriteEl(it, counttt, e)
            }
        }
        if (outputFormat == OutputFormat.json) {
            writer7Sub.body.add new LineInfo(Brakets.closeBacket, ']')
        }
        onBodyCreated()
        return buildResult();
    }

    public String firstCharLineJson = ','

    @Override
    List<String> transformBody() {
        List<String> res = []
        boolean isFirst = true
        body.each {
            if (outputFormat == OutputFormat.json) {
                if (it.brakets == Brakets.netural) {
                    if (isFirst) {
                        res.add it.lineContent
                    } else {
                        res.add(firstCharLineJson + it.lineContent)
                    }
                    isFirst = false
                } else {
                    res.add it.lineContent
                }
            } else {
                res.add it.lineContent
            }
        }
        return res
    }

    String writeEl(T el) {
        String obj = javaBeanStore2Json.save(writer3Importer, objectWriter, el as JavaBeanCommon)
        if (outputFormat == OutputFormat.json) {
            return '{' + obj + '}'
        }
        return obj
    }

    @Override
    String joinLines(List<String> res) {
        return res.join('\n')
    }
}
