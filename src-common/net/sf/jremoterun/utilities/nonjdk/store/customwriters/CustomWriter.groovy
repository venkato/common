package net.sf.jremoterun.utilities.nonjdk.store.customwriters

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.nonjdk.store.ObjectWriter
import net.sf.jremoterun.utilities.nonjdk.store.Writer3
import net.sf.jremoterun.utilities.nonjdk.store.Writer3Import;

import java.util.logging.Logger;

@CompileStatic
interface CustomWriter<T> {

    String save(Writer3Import writer3, ObjectWriter objectWriter, T obj);

    Class<T> getDataClass()

}
