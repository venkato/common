package net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.JustStackTrace3;

import java.util.logging.Logger;

@CompileStatic
class AlertTableCreator {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public volatile static AlertTableCreator alertTableCreator = new AlertTableCreator()
    public volatile AlertTable defaultAlertTable
    public volatile JustStackTrace3 justStackTrace3


    AlertTable getInstanceOrCreate() {
        if (defaultAlertTable == null) {
            defaultAlertTable = new AlertTable();
            defaultAlertTable.registerStuff()
            justStackTrace3 = new JustStackTrace3()
        }
        return defaultAlertTable
    }




    static void addAlertS(String msg) {
        addAlertS(msg, null)
    }


    static void addAlertS(String msg, Throwable e) {
        alertTableCreator.getInstanceOrCreate().addAlert(msg,e)
    }

    void addAlert(String msg, Throwable e) {
        AlertTable defaultAlertTable1 = getInstanceOrCreate()
        if (defaultAlertTable1 == null) {
            log.warn(msg, e)
        } else {
            defaultAlertTable1.addAlert6(msg, e)
        }
    }

}
