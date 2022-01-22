package net.sf.jremoterun.utilities.nonjdk.sshsup

import groovy.transform.CompileStatic

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class JscpLoggerMapper {

    public static Map<Integer, Level> levelsMapping = [:];


    static {
        levelsMapping.put(com.jcraft.jsch.Logger.DEBUG, Level.FINE);
        levelsMapping.put(com.jcraft.jsch.Logger.INFO, Level.INFO);
        levelsMapping.put(com.jcraft.jsch.Logger.WARN, Level.WARNING);
        levelsMapping.put(com.jcraft.jsch.Logger.ERROR, Level.SEVERE);
        levelsMapping.put(com.jcraft.jsch.Logger.FATAL, Level.SEVERE);
    }



}
