package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger

@CompileStatic
class ClassSimpleWriter implements CustomWriter<Class> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, Class str) {
        return writeClass(writer3, str)
    }


    static String writeClass(Writer3Import writer3, Class clazz) {
        String name1 = clazz.getName()
//        log.info "${name1}"
        int i = name1.indexOf('$')
//        log.info "i = ${i}"
        if (i > 0) {
            int j = name1.lastIndexOf('.', i)
            int k = name1.indexOf('$', j)
            String begin = name1.substring(0, k)
//            log.info "begin = ${begin}"
            Class<?> parentClass = clazz.getClassLoader().loadClass(begin)
            writer3.addImport parentClass

            String remaining = name1.substring(j + 1).replace('$', '.')
            return remaining;
        }
        writer3.addImport(clazz)
        return clazz.getSimpleName()
    }


    @Override
    Class<Class> getDataClass() {
        return Class
    }
}
