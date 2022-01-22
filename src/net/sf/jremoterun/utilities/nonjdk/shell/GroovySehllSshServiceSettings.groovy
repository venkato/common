package net.sf.jremoterun.utilities.nonjdk.shell

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import net.sf.jremoterun.utilities.classpath.ClRef;

import java.util.logging.Logger;

@CompileStatic
class GroovySehllSshServiceSettings {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    static void setSshProps() {
        ClRef clRef = new ClRef('org.apache.sshd.netty.NettyIoServiceFactoryFactory')
        try {
            clRef.loadClass2()
            System.setProperty(new ClRef('org.apache.sshd.common.io.IoServiceFactoryFactory').className, clRef.className)
        }catch(ClassNotFoundException e){
            log.info("failed set factory ${clRef}",e)
        }
    }


}
