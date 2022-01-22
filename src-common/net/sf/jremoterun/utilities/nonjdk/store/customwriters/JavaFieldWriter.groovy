package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
class JavaFieldWriter implements CustomWriter<Field> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean writeFullName = false


    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, Field field1) {
        return saveImpl(writer3,field1)
    }

    String saveImpl(Writer3Import writer3, Field field1) {
        return writeClass(writer3, field1.getDeclaringClass()) + '.' + field1.getName()
    }




    String writeClass(Writer3Import writer3, Class clazz) {
        if(writeFullName){
            return clazz.getName().replace('$','.')
        }
        return writer3.addImportWithName(clazz)
    }


    @Override
    Class<Field> getDataClass() {
        return Field
    }
}
