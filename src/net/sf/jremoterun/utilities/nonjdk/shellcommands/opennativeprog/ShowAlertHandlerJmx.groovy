package net.sf.jremoterun.utilities.nonjdk.shellcommands.opennativeprog

import groovy.transform.CompileStatic
import net.sf.jremoterun.ExceptionNotSerializableException
import net.sf.jremoterun.JrrUtils
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MBeanClient
import net.sf.jremoterun.utilities.MBeanFromJavaBean
import net.sf.jremoterun.utilities.MbeanConnectionCreator
import net.sf.jremoterun.utilities.groovystarter.seqrunnerhelper.JustStackTrace3
import net.sf.jremoterun.utilities.nonjdk.idwutils.alerttable.ShowAlertJmx

import java.util.logging.Logger

@CompileStatic
class ShowAlertHandlerJmx implements ShowAlertJmx{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public MbeanConnectionCreator connectionCreator;
    public ShowAlertJmx mbeanClientProxy

    ShowAlertHandlerJmx(MbeanConnectionCreator connectionCreator) {
        this.connectionCreator = connectionCreator
        mbeanClientProxy = MBeanClient.buildMbeanClient(ShowAlertJmx, connectionCreator, ShowAlertJmx.objectName1)
    }

    @Override
    void addAlert6(String msg, Throwable ex) {
        if(ex==null) {
            ex = new JustStackTrace3()
        }else {
            try {
                JrrUtils.serialize(ex)
            } catch (NotSerializableException eee) {
                log.info3("failed serilize1 ${ex}", eee)
                ex = new ExceptionNotSerializableException(ex)
            } catch (Exception eee) {
                log.info3("failed serilize2 ${ex}", eee)
                ex = new ExceptionNotSerializableException(ex)
            }
        }
        mbeanClientProxy.addAlert6(msg,ex)
    }
}
