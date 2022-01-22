package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.util.logging.Logger

@CompileStatic
abstract class StoreComplex {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    public Writer7Sub writer7Sub
    public ObjectWriter objectWriter = new ObjectWriter();
    public boolean writeCreatedAt = true

    StoreComplex(Class configClass) {
        writer7Sub = new Writer7Sub(configClass){
            @Override
            String writeTemplate(Class configClass2) {
                return writeTemplate2(configClass2)
            }
        }
    }

    String buildResult() {
        if(writeCreatedAt){
            writer7Sub.addCreatedAtHeader()
        }
        writer7Sub.buildResult()
    }

    String writeTemplate2(Class configClass2) {
        return configClass2.getSimpleName()
    }

}
