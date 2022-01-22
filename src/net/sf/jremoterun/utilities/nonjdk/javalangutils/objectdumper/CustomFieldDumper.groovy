package net.sf.jremoterun.utilities.nonjdk.javalangutils.objectdumper

import groovy.transform.CompileStatic

@CompileStatic
interface CustomFieldDumper {

    Object dumpField(String fieldName,ObjectDumperProvider dumperProvider, Object obj)


}
