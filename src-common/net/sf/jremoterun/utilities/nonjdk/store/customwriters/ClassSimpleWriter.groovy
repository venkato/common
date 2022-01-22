package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger

@CompileStatic
class ClassSimpleWriter implements CustomWriter<Class> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public boolean writeFullName = false

    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, Class clazz) {
        if (writeFullName) {
            return clazz.getName().replace('$', '.')
        }
        return writeClass(writer3, clazz)
    }


    static String writeClass(Writer3Import writer3, Class clazz) {
        String name1 = clazz.getName()
        int i = name1.indexOf('$')
        if (i > 0) {
            return writeNestedClass(writer3, clazz)
        }

        return writer3.addImportWithName(clazz)
    }

    static String writeNestedClass(Writer3Import writer3, Class clazz) {
        String name1 = clazz.getName()
        int i = name1.indexOf('$')
        assert i > 0
        int j = name1.lastIndexOf('.', i)
        int k = name1.indexOf('$', j)
        String begin = name1.substring(0, k)
        Class<?> parentClass = clazz.getClassLoader().loadClass(begin)
        String name14 = writer3.addImportWithName(parentClass)

        String remaining = name1.substring(j + 1).replace('$', '.')
        return remaining;

    }


    @Override
    Class<Class> getDataClass() {
        return Class
    }
}
