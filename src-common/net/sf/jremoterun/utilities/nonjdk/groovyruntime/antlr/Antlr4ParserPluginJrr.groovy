package net.sf.jremoterun.utilities.nonjdk.groovyruntime.antlr

import groovy.transform.CompileStatic;
import net.sf.jremoterun.utilities.JrrClassUtils
import org.apache.groovy.parser.antlr4.Antlr4ParserPlugin
import org.codehaus.groovy.ast.ModuleNode
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.syntax.Reduction;

import java.util.logging.Logger;

@CompileStatic
class Antlr4ParserPluginJrr extends Antlr4ParserPlugin{
    private static final Logger log = JrrClassUtils.getJdkLogForCurrentClass();

    public volatile boolean doLogging = true

    void onSourceUnit(SourceUnit sourceUnit){

    }

    @Override
    Reduction parseCST(SourceUnit sourceUnit, Reader reader) {
        if(doLogging){
            log.info("su1 = ${sourceUnit.getName()}")
        }
        onSourceUnit(sourceUnit)
        return super.parseCST(sourceUnit, reader)
    }

    @Override
    ModuleNode buildAST(SourceUnit sourceUnit, ClassLoader classLoader, Reduction cst) {
        if(doLogging){
            log.info("su2 = ${sourceUnit.getName()}")
        }
        onSourceUnit(sourceUnit)
        return super.buildAST(sourceUnit, classLoader, cst)
    }
}
