package net.sf.jremoterun.utilities.nonjdk.log

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.slf4j.impl.StaticLoggerBinder

import java.util.logging.Logger

@CompileStatic
class Sl4jLoggerCommon2 {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();


    static void setLoggerImpl2(org.slf4j.spi.SLF4JServiceProvider providerImpl) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        providerImpl.initialize()
        JrrClassUtils.setFieldValue(org.slf4j.LoggerFactory,"PROVIDER" ,providerImpl);
    }


}
