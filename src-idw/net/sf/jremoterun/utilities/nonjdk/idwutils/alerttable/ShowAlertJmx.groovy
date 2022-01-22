package net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable

import groovy.transform.CompileStatic

import javax.management.ObjectName

@CompileStatic
interface ShowAlertJmx {

    public static ObjectName objectName1 = new ObjectName('iff:type=alerts')


    void addAlert6(String msg, Throwable ex)


}
