package net.sf.jremoterun.utilities.nonjdk.store.csvstore

import groovy.transform.CompileStatic

@CompileStatic
interface ObjectWriterCsvI<T> {

    String writeObject( T obj)

//    void failedWriteCountedEl(Object el, int countt, Throwable e)
}
