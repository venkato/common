package net.sf.jremoterun.utilities.nonjdk.jmx

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.MbeanConnectionCreator
import net.sf.jremoterun.utilities.classpath.ClRef

import javax.management.InstanceNotFoundException
import javax.management.MBeanServerConnection
import javax.management.ObjectInstance
import javax.management.ObjectName
import java.lang.management.GarbageCollectorMXBean
import java.lang.management.MemoryPoolMXBean;
import java.util.logging.Logger;

@CompileStatic
class DefaultMbeanNamesReceiverSun {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static List<com.sun.management.GarbageCollectorMXBean> find2GcLocal() {
        (List)java.lang.management.ManagementFactory.getGarbageCollectorMXBeans()
    }

    static Collection<com.sun.management.GarbageCollectorMXBean> find2Gc(MbeanConnectionCreator mbeanConnectionCreator) {
        return DefaultMbeanNamesReceiver.findMBeans(mbeanConnectionCreator, DefaultMbeanNamesReceiver.gcOn).collect { MBeanClientCompositeDataSupport.buildMbeanClient2(com.sun.management.GarbageCollectorMXBean, mbeanConnectionCreator, it.getObjectName()) };
    }

}
