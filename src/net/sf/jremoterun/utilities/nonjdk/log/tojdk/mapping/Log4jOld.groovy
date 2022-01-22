package net.sf.jremoterun.utilities.nonjdk.log.tojdk.mapping

import groovy.transform.CompileStatic
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.collections.MapUtils


import java.util.logging.Logger

@CompileStatic
class Log4jOld {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Map<org.apache.log4j.Level, java.util.logging.Level> log4j1ToLog4j2Map = new HashMap() {
        {
            put(org.apache.log4j.Level.ALL, java.util.logging.Level.ALL);
            put(org.apache.log4j.Level.DEBUG, java.util.logging.Level.FINE);
            put(org.apache.log4j.Level.ERROR, java.util.logging.Level.SEVERE);
            put(org.apache.log4j.Level.FATAL, java.util.logging.Level.SEVERE);
            put(org.apache.log4j.Level.INFO, java.util.logging.Level.INFO);
            put(org.apache.log4j.Level.OFF, java.util.logging.Level.OFF);
            put(org.apache.log4j.Level.TRACE, java.util.logging.Level.FINER);
            put(org.apache.log4j.Level.WARN, java.util.logging.Level.WARNING);
        }
    };


    public static Map<java.util.logging.Level, org.apache.log4j.Level> log4j2ToLog4j1Map = MapUtils.invertMap(log4j1ToLog4j2Map);




}
