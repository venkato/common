package net.sf.jremoterun.utilities.nonjdk.groovyruntime.antlr

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.codehaus.groovy.control.ParserPlugin
import org.codehaus.groovy.control.ParserPluginFactory;

import java.util.logging.Logger;

@CompileStatic
class ParserPluginFactoryJrr extends ParserPluginFactory{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public static Antlr4ParserPluginJrr antlr4ParserPluginJrr = new Antlr4ParserPluginJrr();

    @Override
    ParserPlugin createParserPlugin() {
        return antlr4ParserPluginJrr
    }
}
