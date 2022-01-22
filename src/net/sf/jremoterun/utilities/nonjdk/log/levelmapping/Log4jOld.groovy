package net.sf.jremoterun.utilities.nonjdk.log.levelmapping

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.commons.collections.MapUtils
import org.apache.logging.log4j.Level;

import java.util.logging.Logger;

@CompileStatic
class Log4jOld {
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Map<org.apache.log4j.Level, org.apache.logging.log4j.Level> log4j1ToLog4j2Map = new HashMap<org.apache.log4j.Level, Level>() {
        {
            put(org.apache.log4j.Level.ALL, Level.ALL);
            put(org.apache.log4j.Level.DEBUG, Level.DEBUG);
            put(org.apache.log4j.Level.ERROR, Level.ERROR);
            put(org.apache.log4j.Level.FATAL, Level.FATAL);
            put(org.apache.log4j.Level.INFO, Level.INFO);
            put(org.apache.log4j.Level.OFF, Level.OFF);
            put(org.apache.log4j.Level.TRACE, Level.TRACE);
            put(org.apache.log4j.Level.WARN, Level.WARN);
        }
    };


    public static Map<org.apache.logging.log4j.Level, org.apache.log4j.Level> log4j2ToLog4j1Map = MapUtils.invertMap(log4j1ToLog4j2Map);




}
