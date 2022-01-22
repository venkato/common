package net.sf.jremoterun.utilities.nonjdk.idea.telemetrydisable

import groovy.transform.CompileStatic
import javassist.CtMethod;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef
import net.sf.jremoterun.utilities.nonjdk.classpath.refsdef.MethodRef
import net.sf.jremoterun.utilities.nonjdk.javassist.JrrJavassistUtils
import net.sf.jremoterun.utilities.nonjdk.javassist.RedefinitionBase;

import java.util.logging.Logger;

@CompileStatic
class IdeaTelementryDisable  extends RedefinitionBase{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static MethodRef methodRef1 = new MethodRef(new ClRef('com.intellij.platform.diagnostic.telemetry.exporters.meters.TelemetryMeterJsonExporter'), 'export', 1)

    IdeaTelementryDisable() {
        super(methodRef1.clRef.clRef)
    }

    void disableTelemtry(){
        //Class class2 = methodRef1.clRef.clRef.loadClass2()

        CtMethod method = JrrJavassistUtils.findMethod(methodRef1, cc)
        method.setBody """
// return io.opentelemetry.sdk.metrics.export.MetricExporter.ResultCode.SUCCESS ; 
return io.opentelemetry.sdk.common.CompletableResultCode.ofSuccess() ; 

"""

        doRedefine()
    }


}
