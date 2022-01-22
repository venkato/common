package net.sf.jremoterun.utilities.nonjdk.store.complexwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.runners.GroovyConfigLoaderJrr
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.Brakets
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.linestore.LineInfo
import net.sf.jremoterun.utilities.nonjdk.store.configloader.instancecreation.ListInstanceCreationMethods

import java.util.logging.Logger

@CompileStatic
class ListStore2<T> extends StoreComplex<List<T>>{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public Class<T> templateClass

    ListStore2() {
        super(List);
    }

    @Override
    String writeTemplate(Class configClass2) {
        if(templateClass==null) {
            return super.writeTemplate(configClass2)
        }
        return "${addImportWithName(configClass2)}<${addImportWithName(templateClass)}>"
    }

    @Override
    String saveComplexObject(List<T> list) throws Exception {
        assert list!=null
//        if(templateClass!=null){
//            writer3Importer.addImport(templateClass)
//        }
        int counttt = -1;
        list.each {
            try {
                counttt++
                writer7Sub.body.add new LineInfo(Brakets.netural, writeEl(it))
            } catch (Throwable e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                onFailedWriteEl(it,counttt,e)
            }
        }
        onBodyCreated()
        return buildResult();
    }

    String writeEl(T el) {
        String obj = objectWriter.writeObject(writer3Importer, el)
        return writeEL2(obj)
    }

    public String elSuffix = ''

    String writeEL2(String key1) {
        return "${writer7Sub.varNameThis}.add ${key1} ${semiColumn}${elSuffix}"
    }

    static List loadSettingsS(File file2) {
        if (file2.exists()) {
            List createNew1 = new ListInstanceCreationMethods().createNew()
            GroovyConfigLoaderJrr.configLoaderAdvance.parseConfig(file2).loadConfig(createNew1)
            //return new ConfigLoaderInstanceList<List>(file2,GroovyConfigLoaderJrr.configLoader).loadSettings() as List
            return createNew1
        }
        return []

    }

}
