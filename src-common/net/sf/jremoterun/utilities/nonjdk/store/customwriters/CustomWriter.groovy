package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common.Writer3Import

@CompileStatic
interface CustomWriter<T> {

    String save(Writer3Import writer3, ObjectWriterI objectWriter, T obj);

    Class<T> getDataClass()

}
