package net.sf.jremoterun.utilities.nonjdk.log.levelmapping

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.logging.log4j.jul.DefaultLevelConverter;

import java.util.logging.Logger;

@CompileStatic
class Log4j2New {


    public static org.apache.logging.log4j.jul.DefaultLevelConverter defaultLevelConverter = new DefaultLevelConverter();




    static   java.util.logging.Level convertToJdkLevel(org.apache.logging.log4j.Level from){
        return defaultLevelConverter.toJavaLevel(from)
    }

    static   org.apache.logging.log4j.Level  convertToLog4jLevel(   java.util.logging.Level from){
        return defaultLevelConverter.toLevel(from)
    }

}
