package net.sf.jremoterun.utilities.nonjdk.lang

import com.sun.management.HotSpotDiagnosticMXBean
import com.sun.management.VMOption
import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.Java5VM
import net.sf.jremoterun.utilities.JavaVM;
import net.sf.jremoterun.utilities.JrrClassUtils

import javax.management.ObjectName;
import java.util.logging.Logger;

/**
 * @see sun.management.VMManagementImpl
 * @see com.sun.management.internal.HotSpotDiagnostic
 * @see sun.management.HotSpotDiagnostic
 */
@CompileStatic
class JavaDiagnostic1Options {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ObjectName objectName = new ObjectName('com.sun.management:type=HotSpotDiagnostic')

    public HotSpotDiagnosticMXBean hotSpotDiagnostic

    JavaDiagnostic1Options() {
        Java5VM m = JavaVM.javaVM as Java5VM
        hotSpotDiagnostic = m.getMBeanObject(objectName) as HotSpotDiagnosticMXBean

    }

    void heapDump(File f, boolean live) {
        hotSpotDiagnostic.dumpHeap(f.getAbsolutePath(), live)
    }


    void setOption(JavaDiagnostic1OptionsEnum e, Object value) {
        hotSpotDiagnostic.setVMOption(e.name(), value.toString())
    }

    VMOption getOption(JavaDiagnostic1OptionsEnum e) {
        return hotSpotDiagnostic.getVMOption(e.name())
    }

}
