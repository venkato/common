package net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable

import groovy.transform.CompileStatic
import net.infonode.docking.SplitWindow
import net.infonode.docking.View
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.OsInegrationClientI
import net.sf.jremoterun.utilities.nonjdk.idwutils.IdwUtils
import net.sf.jremoterun.utilities.nonjdk.idwutils.TextAreaAndView
import net.sf.jremoterun.utilities.nonjdk.problemchecker.JustStackTrace
import org.codehaus.groovy.runtime.MethodClosure

import javax.swing.*
import javax.swing.table.DefaultTableModel
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.text.SimpleDateFormat
import java.util.List
import java.util.logging.Logger

@CompileStatic
class AlertTableWrapper {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void addAlertS(String msg) {
        addAlertS(msg, null)
    }

    static void addAlertS(String msg, Throwable e) {
        if (e == null) {
            e = new JustStackTrace()
        }
        log.info(msg, e);
        AlertTable.addAlertS(msg,e)
    }

    static void wrapTextInAlertEachClosure(List<Closure<Void>> closures){
        closures.each {
            MethodClosure methodClosure1 = it as MethodClosure
            wrapTextInAlert(methodClosure1)
        }
    }

    static void wrapTextInAlert(MethodClosure mc){
        String method13 = mc.getMethod()
        log.info "running ${method13} .."
        try {
            mc.call()
            log.info "finished fine ${method13}"
        }catch(Throwable ee){
            addAlertS("failed invoke ${method13}",ee)
        }

    }




}
