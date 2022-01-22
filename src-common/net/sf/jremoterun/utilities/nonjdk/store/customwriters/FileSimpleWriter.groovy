package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

import java.util.logging.Logger

@CompileStatic
class FileSimpleWriter implements CustomWriter<File> {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    @Override
    String save(Writer3Import writer3, ObjectWriterI objectWriter, File f) {
        return writeClass(writer3, objectWriter, f)
    }


    static String writeClass(Writer3Import writer3, ObjectWriterI objectWriter, File f) {
        String filePath = f.getCanonicalFile().getAbsoluteFile().getAbsolutePath().replace('\\', '/')
        String ss3 = objectWriter.writeObject(writer3, filePath)
        return "${ss3} as File";
    }


    @Override
    Class<File> getDataClass() {
        return File
    }
}
