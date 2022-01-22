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

/**
 * @see java.lang.management.ManagementFactory#newPlatformMXBeanProxy
 * @see java.lang.management.ManagementFactory#getPlatformMXBeans
 * @see java.lang.management.ManagementFactory#getPlatformMXBean
 */
@CompileStatic
class DefaultMbeanNamesReceiver {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static ObjectName gcOn = new ObjectName('java.lang:type=GarbageCollector,*')
    public static ObjectName memoryPoolOn = new ObjectName('java.lang:type=MemoryPool,*')



    static Collection<GarbageCollectorMXBean> findGc(MbeanConnectionCreator mbeanConnectionCreator){
        return findMBeans(mbeanConnectionCreator,gcOn).collect {MBeanClientCompositeDataSupport.buildMbeanClient2(GarbageCollectorMXBean,mbeanConnectionCreator,it.getObjectName())};
    }

    static Collection<MemoryPoolMXBean> findMemoryPool(MbeanConnectionCreator mbeanConnectionCreator){
        return findMBeans(mbeanConnectionCreator,memoryPoolOn).collect {MBeanClientCompositeDataSupport.buildMbeanClient2(MemoryPoolMXBean,mbeanConnectionCreator,it.getObjectName())};
    }

    static Set<ObjectInstance> findMBeans(MbeanConnectionCreator mbeanConnectionCreator,ObjectName onn){
        MBeanServerConnection connection = mbeanConnectionCreator.getMBeanServerConnection();
        Set<ObjectInstance> beans = connection.queryMBeans(onn, null)
        if(beans == null || beans.size()==0){
            throw new InstanceNotFoundException("${onn}")
        }
        return beans
    }

}
