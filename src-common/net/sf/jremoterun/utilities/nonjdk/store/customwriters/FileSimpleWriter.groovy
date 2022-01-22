package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3

import java.util.logging.Logger

@CompileStatic
class FileSimpleWriter implements CustomWriter<File> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    String save(Writer3 writer3, ObjectWriter objectWriter, File f) {
        return writeClass(writer3, objectWriter, f)
    }


    static String writeClass(Writer3 writer3, ObjectWriter objectWriter, File f) {
        String ss3 = objectWriter.writeObject(writer3, f.getCanonicalFile().getAbsoluteFile().getAbsolutePath().replace('\\', '/'))
        return "${ss3} as File";
    }


    @Override
    Class<File> getDataClass() {
        return File
    }
}
