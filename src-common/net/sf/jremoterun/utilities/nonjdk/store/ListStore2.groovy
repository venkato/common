package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2
import net.sf.jremoterun.utilities.groovystarter.runners.RunnableFactory
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.util.logging.Logger

@CompileStatic
class ListStore2<T> extends StoreComplex{

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    File file;
    Class templateClass


    ListStore2(File file) {
        super(List);
        this.file = file
    }


    void saveToFile(List<T> list) {
        file.text = saveS(list)
    }

    @Override
    String writeTemplate2(Class configClass2) {
        if(templateClass==null) {
            return super.writeTemplate2(configClass2)
        }
        return "${configClass2.getSimpleName()}<${templateClass.getSimpleName()}>"
    }

    String saveS(List<T> list) {
        assert list!=null
        if(templateClass!=null){
            writer7Sub.addImport(templateClass)
        }
        int counttt = -1;
        writer7Sub.body.addAll(list.collect {
            try {
                counttt++
                return writeEl(it)
            } catch (Exception e) {
                log.info("Failed write ${counttt} el from list : ${e}")
                throw e
            }
        })

        return buildResult();
    }

    String writeEl(T el) {
        String obj = objectWriter.writeObject(writer7Sub, el)
        return "${writer7Sub.varNameThis}.add ${obj} ;"
    }

    List<T> loadSettingsS(String scriptSource, String scriptName) {
        GroovyConfigLoader2 configLoader2 = RunnableFactory.groovyClassLoader.parseClass(scriptSource).newInstance()
        List<T> list = []
        configLoader2.loadConfig(list)
        return list
    }

    List<T> loadsettings() {
        if (file.exists()) {
            try {
                return loadSettingsS(file.text, file.getName())
            } catch (Throwable e) {
                log.info("failed load : ${file}", e)
                throw e
            }
        }
        return []
    }

}
