package net.sf.jremoterun.utilities.nonjdk.idea;

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils

import java.util.logging.Level;
import java.util.logging.Logger;

@CompileStatic
class Log4jLevelToIdeaLevel {


    public static Map<com.intellij.openapi.diagnostic.LogLevel,java.util.logging.Level> idea2jdk= [:]
    public static Map<java.util.logging.Level,com.intellij.openapi.diagnostic.LogLevel> jdk2idea= [:]

    static {
        init1()
    }

    static void init1(){
        mapping( com.intellij.openapi.diagnostic.LogLevel.ALL,java.util.logging.Level.ALL);
        mapping( com.intellij.openapi.diagnostic.LogLevel.TRACE,java.util.logging.Level.FINER);
        mapping( com.intellij.openapi.diagnostic.LogLevel.DEBUG,java.util.logging.Level.FINE);
        mapping( com.intellij.openapi.diagnostic.LogLevel.INFO,java.util.logging.Level.INFO);
        mapping( com.intellij.openapi.diagnostic.LogLevel.WARNING,java.util.logging.Level.WARNING);
        mapping( com.intellij.openapi.diagnostic.LogLevel.ERROR,java.util.logging.Level.SEVERE);
        mapping( com.intellij.openapi.diagnostic.LogLevel.OFF,java.util.logging.Level.OFF);
    }



    static void mapping (com.intellij.openapi.diagnostic.LogLevel idea,Level jdlLevel){
        idea2jdk.put(idea,jdlLevel)
        jdk2idea.put(jdlLevel, idea)
    }


}
