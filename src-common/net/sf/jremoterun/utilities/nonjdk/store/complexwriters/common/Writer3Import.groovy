package net.sf.jremoterun.utilities.nonjdk.store.complexwriters.common

import groovy.transform.CompileStatic

@CompileStatic
interface Writer3Import {

    @Deprecated
    void addImport(Class clazz)

    String addImportWithName(Class clazz)


}
