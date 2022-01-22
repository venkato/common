package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.util.logging.Logger

@CompileStatic
abstract class StoreComplex<T> extends Writer7Sub {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Deprecated
    public Writer7Sub writer7Sub = this
    public Writer3Import writer3Importer = this
    public ObjectWriter objectWriter = new ObjectWriter();


    StoreComplex(Class configClass) {
        super(configClass)
//        writer7Sub = new Writer7Sub(configClass){
//            @Override
//            String writeTemplate(Class configClass2) {
//                return writeTemplate2(configClass2)
//            }
//        }
    }


    void onBodyCreated() {

    }

    void onFailedWriteEl(Object el, int countInList, Throwable ex) {
        throw ex
    }

    abstract String saveComplexObject(T obj) throws Exception

//    String buildResult() {
//        writer7Sub.buildResult()
//    }

//    String writeTemplate2(Class configClass2) {
//        return configClass2.getSimpleName()
//    }

}
