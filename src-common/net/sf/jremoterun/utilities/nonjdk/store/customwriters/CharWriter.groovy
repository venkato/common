package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.lang.reflect.Field
import java.util.logging.Logger

@CompileStatic
class CharWriter implements CustomWriter<Character> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean writeFullName = false


    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, Character character1) {
        return saveImpl(character1)
    }

    String saveImpl( Character character1) {
        if(writeFullName){
            return "'${character1}'.charAt(0)"
        }
        return "'${character1}'"
    }



    @Override
    Class<Character> getDataClass() {
        return Character
    }
}
