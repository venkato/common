package net.sf.jremoterun.utilities.nonjdk.store

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.classpath.sl.GroovySettingsLoader

import java.util.logging.Logger

@CompileStatic
class ListStore<T> {

    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static String varName = net.sf.jremoterun.utilities.groovystarter.GroovyConfigLoader2.varName
    public String varNameList = net.sf.jremoterun.utilities.nonjdk.store.Writer6Sub.varName

    File file;

    public Writer3Sub writer3;
    public ObjectWriter objectWriter;

    ListStore(File file) {
        this.file = file
    }


    void saveToFile(List<T> list) {
        file.text = saveS(list)
    }

    Writer3 createWriter(){
        if(writer3==null){
            writer3=new Writer3Sub()
        }
        return writer3
    }

    ObjectWriter createObjectWriter(){
        if(objectWriter==null){
            objectWriter = new ObjectWriter()
        }
        return objectWriter
    }


    String saveS(List<T> list) {
        Writer3 writer3 = createWriter()
        assert writer3!=null
        ObjectWriter writer = createObjectWriter()
        writer3.addCreatedAtHeader()
        writer3.body.add "List ${varNameList} = ${writer3.generateGetProperty(varName)} as List;".toString()
        int counttt = -1;
        writer3.body.addAll list.collect {
            try {
                counttt++;
                //String obj = writer.writeObject(writer3, it)
                return writeEl(writer3,writer,it)
            }catch(Exception e){
                log.info("Failed write ${counttt} el from list : ${e}")
                throw e
            }

        }
        return writer3.buildResult();
    }

    String writeEl(Writer3 writer3, ObjectWriter writer, T el) {
        String obj = writer.writeObject(writer3, el)
        return "${varNameList}.add ${obj} ;" as String
    }

    List<T> loadSettingsS(String scriptSource, String scriptName) {
        List<T> list = []
        Binding binding = new Binding()
        binding.setVariable(varName, list)

        Script script = GroovySettingsLoader.groovySettingsLoader.createScript(scriptSource, scriptName, binding)
        script.run();
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

//    List<T> loadSettingsS(File file) {
//        return loadSettingsS(file.text, file.name)
//    }

}
